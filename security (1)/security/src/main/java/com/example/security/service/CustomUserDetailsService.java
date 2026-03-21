package com.example.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.security.exception.UserNotFoundException;
import com.example.security.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) {
		
		try {
			return (UserDetails) userRepo.findByUserName(username).orElseThrow(()-> new UserNotFoundException("User not found with username:"+username));

		}catch(Exception ex)
		{
			throw ex;
		}
		
		
	}

}
