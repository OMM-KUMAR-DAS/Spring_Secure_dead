package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.records.request.SignupRequest;
import com.example.security.records.response.GenericResponse;
import com.example.security.service.SignupService;

@RestController
public class SignupController {
	
	@Autowired
	private SignupService signupService;
	
	@PostMapping("/signup")
	public GenericResponse signup(@RequestBody SignupRequest request)
	{
		return signupService.signup(request);
	}

}
