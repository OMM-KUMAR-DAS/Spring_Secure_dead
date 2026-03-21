package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	
	
	
	Optional<UserEntity> findByUserName(String username);

}
