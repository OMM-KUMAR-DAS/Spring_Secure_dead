package com.example.security.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthIdentityRepository authIdentityRepo;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        try {

            OAuth2AuthenticationToken token =
                    (OAuth2AuthenticationToken) authentication;

            OAuth2User oauthUser = token.getPrincipal();

            String provider = token.getAuthorizedClientRegistrationId();
            String providerUserId = oauthUser.getAttribute("sub");
            String email = oauthUser.getAttribute("email");

            log.info("OAuth Login => provider:{} providerId:{} email:{}",
                    provider, providerUserId, email);

            UserEntity user;

            /*
             ------------------------------------------------
             STEP 1 : CHECK EXISTING PROVIDER LINK
             ------------------------------------------------
            */
            Optional<AuthIdentityEntity> existingIdentity =
                    authIdentityRepo.findByProviderUserIdAndProvider(
                            providerUserId,
                            provider
                    );

            if (existingIdentity.isPresent()) {

                AuthIdentityEntity identity = existingIdentity.get();
                identity.setLastLogin(LocalDateTime.now());

                authIdentityRepo.save(identity);

                user = identity.getUser();

                log.info("Existing provider user login success");

            } else {

                /*
                 --------------------------------------------
                 STEP 2 : CHECK USER BY EMAIL
                 --------------------------------------------
                */
                Optional<UserEntity> existingUser =
                        userRepo.findByEmail(email);

                if (existingUser.isPresent()) {

                    user = existingUser.get();

                    log.info("Existing email found. Linking provider.");

                } else {

                    /*
                     ----------------------------------------
                     STEP 3 : CREATE NEW USER
                     ----------------------------------------
                    */
                    user = UserEntity.builder()
                            .email(email)
                            .password(null)
                            .role("USER")
                            .build();

                    user = userRepo.save(user);

                    log.info("New user created");
                }

                /*
                 --------------------------------------------
                 STEP 4 : CREATE AUTH IDENTITY
                 --------------------------------------------
                */
                AuthIdentityEntity newIdentity =
                        AuthIdentityEntity.builder()
                                .provider(provider)
                                .providerUserId(providerUserId)
                                .providerEmail(email)
                                .user(user)
                                .createdAt(LocalDateTime.now())
                                .lastLogin(LocalDateTime.now())
                                .build();

                authIdentityRepo.save(newIdentity);

                log.info("Provider linked successfully");
            }

            /*
             ------------------------------------------------
             STEP 5 : GENERATE TOKENS
             ------------------------------------------------
            */
            String accessToken = generateRefreshAndAccessTokens(response, user);

            /*
             ------------------------------------------------
             STEP 6 : SEND RESPONSE
             ------------------------------------------------
            */
            LoginResponse loginResponse =
                    new LoginResponse(HttpStatus.OK.value(), accessToken);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            new ObjectMapper().writeValue(response.getWriter(), loginResponse);

        } catch (Exception ex) {

            log.error("onAuthenticationSuccess failed due to: {}", ex.getMessage());
            throw ex;
        }
    }

    /*
     --------------------------------------------------------
     COMMON TOKEN METHOD
     --------------------------------------------------------
    */
    public String generateRefreshAndAccessTokens(HttpServletResponse response,
                                                 UserEntity user) {

        try {

            String accessToken =
                    jwtUtil.generateAccessToken(user.getEmail());

            String refreshToken =
                    jwtUtil.generateRefreshToken(user.getEmail());

            log.info("Tokens generated for user: {}", user.getEmail());

            RefreshTokenEntity refreshEntity =
                    RefreshTokenEntity.builder()
                            .createdAt(LocalDateTime.now())
                            .email(user.getEmail())
                            .status("Y")
                            .token(refreshToken)
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

            log.info("Refresh token cookie added");

            return accessToken;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
