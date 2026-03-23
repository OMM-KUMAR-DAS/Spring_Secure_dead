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


@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) {
		
		try {
			UserEntity user = userRepo.findByUserName(username)
		            .orElseThrow(() -> new UserNotFoundException("User not found"));

			return User.builder()
			        .username(user.getUserName())
			        .password(user.getPassword())
			        .roles(user.getRole()) 
			        .build();
			
		}catch(Exception ex)
		{
			throw ex;
		}
		
		
	}

}
