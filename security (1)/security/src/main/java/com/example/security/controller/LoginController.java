package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.records.request.LoginRequest;
import com.example.security.service.LoginService;


import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LoginController {
	
	
	@Autowired
	private LoginService loginService;
	
	@PostMapping("/login")
	public Object login(@RequestBody LoginRequest request, HttpServletResponse response)
	{
		log.info("<<<START>>> LOGIN");
	    return loginService.login(request, response);
	}

}
