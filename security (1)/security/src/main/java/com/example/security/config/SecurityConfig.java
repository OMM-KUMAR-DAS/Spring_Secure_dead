package com.example.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.security.filter.JwtFilter;
import com.example.security.records.response.GenericResponse;
import com.example.security.service.Oauth2SuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class SecurityConfig {
	
	
	    @Autowired
	    private PasswordEncoder passEncoder;
	 
	    @Autowired
	    private JwtFilter jwtFilter;
	    
	    @Autowired
	    private Oauth2SuccessHandler oauth2SuccessHandler;
	
	
	@Bean
	public SecurityFilterChain securityConfigs(HttpSecurity http) throws Exception
	{
		try {
			
		        return http
		        		
		        		.csrf(csrf -> csrf.disable())

		               
		                .sessionManagement(session -> 
		                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		                )
		        		.authorizeHttpRequests(auth->
			        	auth
			        	.requestMatchers("/signup/**","/login/**","/refreshToken").permitAll()
			        	.requestMatchers("/admin/**").hasAnyRole("ADMIN")
			        	.requestMatchers("/user/**").hasAnyRole("ADMIN","USER")
			        	.anyRequest().authenticated()
			        
			        )   
		            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
		            .oauth2Login(oauth->{
		            	
		            	oauth.successHandler(oauth2SuccessHandler);
		            	
		            	oauth.failureHandler((request, response, exception) -> {

		                    log.error("OAuth Login Failed: {}", exception.getMessage());

		                    response.setStatus(HttpServletResponse.SC_OK);
		                    response.setContentType("application/json");

		                    GenericResponse errorResponse =
		                            new GenericResponse(
		                                    "OAuth login failed",
		                                    HttpServletResponse.SC_UNAUTHORIZED
		                            );

		                    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
		                });
		            })
		            .build();
			
		}catch(Exception ex)
		{
			log.error("error occured:{}",ex.getMessage());
			throw ex;
		}
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
	
//	@Bean
//	public UserDetailsService getUserData()
//	{
//		UserDetails user= User.builder().
//				username("ommbhai")
//                .password(passEncoder.encode("omm@2002"))
//                .roles("USER")
//                .build();
//		
//		UserDetails admin= User.builder().
//				username("adminbhai")
//                .password(passEncoder.encode("admin@2002"))
//                .roles("ADMIN")
//                .build();
//		
//		return new InMemoryUserDetailsManager(user,admin);
//	}
//	
	
	
	
	
	

}
