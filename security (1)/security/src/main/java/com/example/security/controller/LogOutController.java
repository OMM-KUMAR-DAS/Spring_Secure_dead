package com.example.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.service.LogOutService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class LogOutController {
	
	@Autowired
	LogOutService logoutService;
	
	@PostMapping("/system/logout")
	public Object logouts(@CookieValue(name="refreshToken",required=true) String token, HttpServletResponse response)
	{
		log.info("<<<START>>> LOGOUT");
	    return logoutService.logouts(token, response);
	}

}
