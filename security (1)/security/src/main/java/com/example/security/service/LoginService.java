package com.example.security.service;



import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.security.entity.RefreshTokenEntity;
import com.example.security.records.request.LoginRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.records.response.LoginResponse;
import com.example.security.repository.RefreshTokenRepository;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginService {
	
	
	@Autowired
	UserRepository userRepo;
	
	
	@Autowired
	AuthenticationManager authManager;
	
	
	@Autowired
	RefreshTokenRepository refreshTokenRepo;
	
	
	@Autowired
	JwtUtil jwtUtil;
	
	
	@Transactional
	public Object login(LoginRequest request, HttpServletResponse response) {
	    try {

	        log.info("Login attempt for user: {}", request.email());

	        Authentication auth = authManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.email(),
	                        request.password()
	                )
	        );

	        if (!auth.isAuthenticated()) {
	            log.info("Authentication failed for user: {}", request.email());
	            return new GenericResponse("User Not Found", HttpStatus.NO_CONTENT.value());
	        }

	        UserDetails user = (UserDetails) auth.getPrincipal();
	        log.info("Authentication successful for user: {}", user.getUsername());

	        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
	        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

	        log.info("Access and Refresh tokens generated for user: {}", user.getUsername());

	        RefreshTokenEntity refreshTokenentity = RefreshTokenEntity.builder()
	                .createdAt(LocalDateTime.now())
	                .email(user.getUsername())
	                .status("Y")
	                .token(refreshToken)
	                .validity(LocalDateTime.now().plusDays(7))
	                .build();

	        refreshTokenRepo.save(refreshTokenentity);
	        log.info("Refresh token saved in DB for user: {}", user.getUsername());

	        // ✅ Create Cookie
	        Cookie cookie = new Cookie("refreshToken", refreshToken);
	        cookie.setHttpOnly(true);  //prevents xss attack
	        cookie.setMaxAge(7 * 24 * 60 * 60);
	        cookie.setSecure(false); // true in production
	        cookie.setPath("/api");
	        cookie.setAttribute("SameSite", "Strict");

	        response.addCookie(cookie);
	        log.info("Refresh token cookie added to response for user: {}", user.getUsername());

	        return new LoginResponse(HttpStatus.OK.value(), accessToken);

	    } catch (Exception ex) {
	        log.error("Login failed for user: {} | Error: {}", request.email(), ex.getMessage());
	        throw ex;
	    }
	}

}
