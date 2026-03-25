package com.example.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.security.entity.RefreshTokenEntity;
import com.example.security.records.response.GenericResponse;
import com.example.security.repository.RefreshTokenRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogOutService {
	
	@Autowired
	RefreshTokenRepository refreshTokenRepo;
	
	 public Object logouts(String token,HttpServletResponse response)
	 {
		 try {
			 
			 
			 if(token==null||token.isEmpty())
			 {
				 log.info("❌ Refresh token missing");
		            return new GenericResponse("Token missing", HttpStatus.NO_CONTENT.value());
			 }
			 
			 RefreshTokenEntity entity=  refreshTokenRepo.findByTokenAndStatus(token, "Y").orElse(null);
			 
			 if(entity==null)
			 {
	                return new GenericResponse("Already logged out", 400);
			 }
			 
			 entity.setStatus("D");
			 refreshTokenRepo.save(entity);
			 
			 
			 Cookie cookie= new Cookie("refreshToken", null);
			 
			 //cookie.setHttpOnly(fal);
			 cookie.setMaxAge(0);
			 cookie.setPath("/api");
			 
			 response.addCookie(cookie);
			 
             return new GenericResponse("logged out successfully", HttpStatus.OK.value());
			 
			 
			 
		 }catch(Exception ex)
		 {
			 throw ex;
		 }
	 }

}
