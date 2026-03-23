package com.example.security.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="refresh_token")
public class RefreshTokenEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	@Column(name="username",nullable = false)
	String userName;
	
	@Column(name="token",nullable = false,unique = true)
	String token;
	
	@Column(name="token_created_at",nullable = false)
	LocalDateTime createdAt;
	
	@Column(name="token_validity",nullable = false)
	LocalDateTime validity;
	
	
	
	String status;
	

}
