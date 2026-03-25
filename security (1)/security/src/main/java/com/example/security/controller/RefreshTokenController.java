package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RefreshTokenController {
	
	
	@Autowired
	RefreshTokenService refreshTokenService;
	
	
	@PostMapping("/refreshToken")
	public Object refreshToken(@CookieValue(name="refreshToken",required = true) String token)
	{
		log.info("<<<START>>> refreshToken");
	    return refreshTokenService.refreshToken(token);
	}


}
