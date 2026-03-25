package com.example.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.security.records.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalException {
	
	
	 @ExceptionHandler(UserNotFoundException.class)
	    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
		 
		 
		 GenericResponse response= new GenericResponse(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value());
		 
		 return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(response);
	        
	    }
	 
	 

	    
	 @ExceptionHandler(Exception.class)
	 public ResponseEntity<?> handleOtherExceptions(Exception ex) {

	     log.error("🚨 Unhandled exception occurred: {}", ex.getMessage(), ex);

	     GenericResponse response =
	             new GenericResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());

	     return ResponseEntity
	             .status(HttpStatus.INTERNAL_SERVER_ERROR)
	             .body(response);
	 }

}
