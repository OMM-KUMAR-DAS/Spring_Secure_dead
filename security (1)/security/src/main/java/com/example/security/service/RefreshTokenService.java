package com.example.security.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.security.entity.RefreshTokenEntity;
import com.example.security.entity.UserEntity;
import com.example.security.records.response.GenericResponse;
import com.example.security.repository.RefreshTokenRepository;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenService {
	
	
	@Autowired
	RefreshTokenRepository refreshTokenRepo;
	
	@Autowired
	JwtUtil jwtUtil;
	
	public Object refreshToken(String token) {

	    try {
	        log.info("🔄 Refresh token request received");

	        // 🔹 Step 1: Check token presence
	        if (token == null || token.isEmpty()) {
	            log.info("❌ Refresh token missing");
	            return new GenericResponse("Token missing", HttpStatus.NO_CONTENT.value());
	        }

	        log.info("🔑 Refresh token received");

	        // 🔹 Step 2: Validate JWT
	        if (!jwtUtil.validateToken(token)) {
	            log.info("❌ Refresh token expired or invalid");
	            return new GenericResponse("Token Expired Login Again", HttpStatus.NO_CONTENT.value());
	        }

	        log.info("✅ Refresh token is valid");

	        // 🔹 Step 3: DB check (token status)
	        RefreshTokenEntity user = refreshTokenRepo.findByTokenAndStatus(token, "Y").orElse(null);

	        if (user == null) {
	            log.info("❌ Token not found in DB or already invalidated");
	            return new GenericResponse("Token Invalidated Login Again", HttpStatus.NO_CONTENT.value());
	        }

	        log.info("📦 Token verified in DB for user: {}", user.getEmail());

	        // 🔹 Step 4: Generate new access token
	        String accessToken = jwtUtil.generateAccessToken(user.getEmail());

	        log.info("🎟 New access token generated for user: {}", user.getEmail());

	        // 🔹 Step 5: Prepare response
	        Map<String, Object> response = new HashMap<>();
	        response.put("accessToken", accessToken);
	        response.put("statusCode", HttpStatus.OK.value());

	        log.info("✅ Refresh token flow completed successfully");

	        return response;

	    } catch (Exception ex) {
	        log.error("🚨 Error during refresh token flow: {}", ex.getMessage());
	        throw ex;
	    }
	}
}
