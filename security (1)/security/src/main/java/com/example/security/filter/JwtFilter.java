package com.example.security.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.security.entity.UserEntity;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter  {
	
	@Autowired
	JwtUtil jwtUtil;
	
	@Autowired
	UserRepository userRepo;

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain)
	        throws ServletException, IOException {

	    try {
	        log.info("➡ Incoming request: {}", request.getRequestURI());

	        String authHeader = request.getHeader("Authorization");

	        // 🔹 Step 1: Check header
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            log.info("⚠ No Authorization header or invalid format");
	            filterChain.doFilter(request, response);
	            return;
	        }

	        // 🔹 Step 2: Extract token
	        String token = authHeader.split("Bearer ")[1];
	        log.info("✅ Token extracted successfully");

	        // 🔹 Step 3: Validate token
	        if (jwtUtil.validateToken(token)) {
	            log.info("✅ Token is valid");

	            String email = jwtUtil.extractUsername(token);
	            log.info("👤 Username extracted: {}", email);

	            // 🔹 Step 4: Check if already authenticated
	            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

	                log.info("🔍 No existing authentication found, proceeding...");

	                UserEntity user = userRepo.findByEmail(email).orElseThrow();
	                log.info("📦 User fetched from DB: {}", user.getEmail());
	                log.info("user role:{}",user.getRole());

	                Authentication authenticatedUser =
	                        new UsernamePasswordAuthenticationToken(
	                        		email,
	                                null,
	                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
	                        );

	                // 🔹 Step 5: Set authentication
	                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
	                log.info("🔐 Authentication set for user: {}", email);

	            } else {
	                log.info("ℹ User already authenticated, skipping...");
	            }

	        } else {
	            log.info("❌ Invalid or expired token");
	        }

	        // 🔹 Step 6: Continue filter chain
	        filterChain.doFilter(request, response);

	    } catch (Exception ex) {
	        log.error("🚨 Error in JWT filter: {}", ex.getMessage());
	        throw ex;
	    }
	}

}
