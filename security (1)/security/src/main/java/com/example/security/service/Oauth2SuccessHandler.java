package com.example.security.service;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.security.entity.AuthIdentityEntity;
import com.example.security.entity.RefreshTokenEntity;
import com.example.security.entity.UserEntity;
import com.example.security.records.response.LoginResponse;
import com.example.security.repository.AuthIdentityRepository;
import com.example.security.repository.RefreshTokenRepository;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthIdentityRepository authIdentityRepo;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtUtil jwtUtil;
    private final OAuth2AuthorizedClientService authorizedClientService;
    
    @Value("${GITHUB_API}")
    private String githubUrl;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        OAuth2AuthenticationToken token =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oauthUser = token.getPrincipal();

        String provider = token.getAuthorizedClientRegistrationId();

        // -----------------------------
        // STEP 1: Normalize provider data
        // -----------------------------
        String providerUserId = null;
        String email = null;

        if ("google".equals(provider)) {
            providerUserId = oauthUser.getAttribute("sub").toString();
            Object rawEmail = oauthUser.getAttribute("email");
            email = rawEmail != null ? rawEmail.toString() : null;
        }
        if ("github".equals(provider)) {
            Object rawId = oauthUser.getAttribute("id");
            providerUserId = rawId.toString();

            Object rawEmail = oauthUser.getAttribute("email");
            email = rawEmail != null ? rawEmail.toString() : null;
            
           //fetch email if null
            if (email == null) {

                var authorizedClient = authorizedClientService.loadAuthorizedClient(
                        token.getAuthorizedClientRegistrationId(),
                        token.getName()
                );

                String accessToken = authorizedClient.getAccessToken().getTokenValue();

                email = fetchGithubEmail(accessToken);
            }
        }


        log.info("OAuth Login => provider:{} providerId:{} email:{}",
                provider, providerUserId, email);

        UserEntity user;

        // -----------------------------
        // STEP 2: Check identity first (MOST IMPORTANT)
        // -----------------------------
        Optional<AuthIdentityEntity> existingIdentity =
                authIdentityRepo.findByProviderUserIdAndProvider(
                        providerUserId,provider);

        if (existingIdentity.isPresent()) {

            AuthIdentityEntity identity = existingIdentity.get();
            identity.setLastLogin(LocalDateTime.now());
            authIdentityRepo.save(identity);

            user = identity.getUser();

            log.info("Existing provider login success");
        } 
        else {

            // -----------------------------
            // STEP 3: Email-based linking (SAFE)
            // -----------------------------
            Optional<UserEntity> existingUser = Optional.empty();

            if (email != null) {
                existingUser = userRepo.findByEmail(email);
            }

            if (existingUser.isPresent()) {

                user = existingUser.get();

                log.info("User found by email. Linking provider.");
            } 
            else {

                // -----------------------------
                // STEP 4: Create new user
                // -----------------------------
                user = UserEntity.builder()
                        .email(email)   // can be null for GitHub
                        .password(null)
                        .role("USER")
                        .build();

                user = userRepo.save(user);

                log.info("New user created");
            }

            // -----------------------------
            // STEP 5: Create identity link
            // -----------------------------
            AuthIdentityEntity identity = AuthIdentityEntity.builder()
                    .provider(provider)
                    .providerUserId(providerUserId)
                    .providerEmail(email)
                    .user(user)
                    .createdAt(LocalDateTime.now())
                    .lastLogin(LocalDateTime.now())
                    .build();

            authIdentityRepo.save(identity);

            log.info("Identity linked successfully");
        }

        // -----------------------------
        // STEP 6: Generate tokens
        // -----------------------------
        String accessToken = generateTokens(response, user);

        LoginResponse loginResponse =
                new LoginResponse(HttpStatus.OK.value(), accessToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");

        new ObjectMapper().writeValue(response.getWriter(), loginResponse);
    }

    // -----------------------------
    // TOKEN GENERATION
    // -----------------------------
    public String generateTokens(HttpServletResponse response,
                                 UserEntity user) {

        String accessToken =
                jwtUtil.generateAccessToken(user.getEmail()!=null?user.getEmail():user.getId().toString());

        String refreshToken =
                jwtUtil.generateRefreshToken(user.getEmail()!=null?user.getEmail():user.getId().toString());

        RefreshTokenEntity refreshEntity =
                RefreshTokenEntity.builder()
                        .token(refreshToken)
                        .email((user.getEmail()!=null?user.getEmail():null))
                        .status("Y")
                        .createdAt(LocalDateTime.now())
                        .validity(LocalDateTime.now().plusDays(7))
                        .build();

        refreshTokenRepo.save(refreshEntity);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(false); 
        cookie.setPath("/api");
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);

        return accessToken;
    }
    
    //method to fetch github email of the the authenticated user
    private String fetchGithubEmail(String accessToken) {

        RestTemplate restTemplate = new RestTemplate();
       

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                		githubUrl,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        List<Map<String, Object>> emails = response.getBody();

        if (emails == null || emails.isEmpty()) return null;

        return emails.stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                .filter(e -> Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElse(null);
    }
}


//OAuth2AuthenticationToken token =
//(OAuth2AuthenticationToken) authentication;
//
//OAuth2User user = token.getPrincipal();
//
//String provider = token.getAuthorizedClientRegistrationId(); // google
//
//String providerUserId = user.getAttribute("sub");
//
//String email = user.getAttribute("email");
//
//String name = user.getAttribute("name");
//
//Boolean verified = user.getAttribute("email_verified");
//
//String picture = user.getAttribute("picture");
