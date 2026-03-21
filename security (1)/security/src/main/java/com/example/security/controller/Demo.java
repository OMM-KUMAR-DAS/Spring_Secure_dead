	package com.example.security.controller;
	
	import java.util.HashMap;
	import java.util.Map;
	
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.RestController;
	
	import lombok.extern.slf4j.Slf4j;
	
	@Slf4j
	@RestController
	public class Demo {
		
		
		@GetMapping("/user/getUsers")
		public Map<String,String> getUsers()
		{
			Map<String,String> users= new HashMap<>();
			
			users.put("1", "Omm");
			
			users.put("2", "james");
			
			return users;
		}
		
		@GetMapping("/admin/getDoctors")
		public Map<String,String> getDoctors()
		{
			Map<String,String> users= new HashMap<>();
			
			users.put("1", "Dr. Joe");
			
			users.put("2", "Dr. Jack");
			
			return users;
		}
		
		@GetMapping("/admin/getPlanes")
		public Map<String,String> getPlanes()
		{
			Map<String,String> users= new HashMap<>();
			
			users.put("1", "B777");
			
			users.put("2", "A321Neo");
			
			return users;
		}
		
		@GetMapping("/public/getBooks")
		public Map<String,String> getBooks()
		{
			Map<String,String> books= new HashMap<>();
			
			books.put("1", "L1");
			
			books.put("2", "L2");
			
			return books;
		}
	
	
	
	}
