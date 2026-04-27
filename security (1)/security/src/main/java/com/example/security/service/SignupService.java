package com.example.security.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.security.entity.UserEntity;
import com.example.security.records.request.SignupRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SignupService {
	
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Transactional
	public GenericResponse signup(SignupRequest request)
	{
		try {
			
			Optional<UserEntity> user=  userRepo.findByEmail(request.email());
			
			if(!user.isEmpty())
			{
				return new GenericResponse("User Already exist",HttpStatus.OK.value());
			}
			
			UserEntity registerUser= UserEntity.builder()
					                  .email(request.email())
					                 .password(passwordEncoder.encode(request.password()))
					                 .role(request.role())
					                 .build();
			
			userRepo.save(registerUser);
			
			return new GenericResponse("User Registers successfully",HttpStatus.OK.value());

			
		}catch(Exception ex)
		{
			throw ex;
		}
	}

}
