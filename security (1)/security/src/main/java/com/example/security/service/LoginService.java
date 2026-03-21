package com.example.security.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import com.example.security.records.request.LoginRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtUtil;

import jakarta.transaction.Transactional;

@Service
public class LoginService {
	
	
	@Autowired
	UserRepository userRepo;
	
	
	@Autowired
	AuthenticationManager authManager;
	
	
	@Autowired
	JwtUtil jwtUtil;
	
	
	@Transactional
	public GenericResponse login(LoginRequest request)
	{
		try {
			
			Authentication auth= new UsernamePasswordAuthenticationToken(request.username(), request.password());
			
			
			if(!auth.isAuthenticated())
			{
				return new GenericResponse("User Not Found",HttpStatus.NO_CONTENT.value());
			}
			
			
			UserDetails user= (UserDetails) auth.getPrincipal();
			
			jwtUtil.generateAccessToken(user.getUsername());
			jwtUtil.generateRefreshToken(user.getUsername());
			
			
			
			return null;
			
			
			
			
			
		}catch(Exception ex)
		{
			throw ex;
		}
	}

}
