package com.example.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.security.entity.UserEntity;
import com.example.security.exception.UserNotFoundException;
import com.example.security.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) {

	    try {
	        log.info("🔐 Attempting to load user by username: {}", username);

	        UserEntity user = userRepo.findByUserName(username)
	                .orElseThrow(() -> {
	                    log.error("❌ User not found in DB: {}", username);
	                    return new UserNotFoundException("User not found");
	                });

	        log.info("✅ User found: {}", user.getUserName());
	        log.info("🔑 User role: {}", user.getRole());

	        UserDetails userDetails = User.builder()
	                .username(user.getUserName())
	                .password(user.getPassword())
	                .roles(user.getRole())
	                .build();

	        log.info("📦 UserDetails object created successfully for: {}", username);

	        return userDetails;

	    } catch (Exception ex) {
	        log.error("🚨 Error while loading user: {} | Reason: {}", username, ex.getMessage());
	        throw ex;
	    }
	}
		
		
	}

