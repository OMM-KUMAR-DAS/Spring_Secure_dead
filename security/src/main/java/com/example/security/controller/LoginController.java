package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.security.records.request.LoginRequest;
import com.example.security.records.request.SignupRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.service.LoginService;
import com.example.security.service.SignupService;

@Service
public class LoginController {
	
	
	@Autowired
	private LoginService loginService;
	
	@PostMapping("/login")
	public GenericResponse login(@RequestBody LoginRequest request)
	{
		return loginService.login(request);
	}

}
