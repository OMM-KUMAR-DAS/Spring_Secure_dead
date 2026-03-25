package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security.entity.RefreshTokenEntity;
import com.example.security.entity.UserEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {
	
	Optional<RefreshTokenEntity> findByTokenAndStatus(String username,String status);

}
