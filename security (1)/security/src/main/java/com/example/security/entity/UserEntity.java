package com.example.security.entity;


import jakarta.persistence.Entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
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
@Table(name="users")
public class UserEntity  {
	
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	 // Main user email
    @Column(nullable = false, unique = true)
    private String email;

    // Null for OAuth users
    @Column
    private String password;

    @Column
    private String role;

}
