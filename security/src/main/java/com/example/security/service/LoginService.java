package com.example.security.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;


import com.example.security.records.request.LoginRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class LoginService {
	
	
	@Autowired
	UserRepository userRepo;
	
	
	@Autowired
	AuthenticationManager authManager;
	
	
	
	@Transactional
	public GenericResponse login(LoginRequest request)
	{
		try {
			
			
		}catch(Exception ex)
		{
			throw ex;
		}
	}

}
