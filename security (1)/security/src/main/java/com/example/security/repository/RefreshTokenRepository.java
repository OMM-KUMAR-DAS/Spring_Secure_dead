package com.example.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Integer> {

}
