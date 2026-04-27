package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.security.entity.AuthIdentityEntity;

@Repository
public interface AuthIdentityRepository extends JpaRepository<AuthIdentityEntity,Integer> {
	
	Optional<AuthIdentityEntity> findByProviderUserIdAndProvider(
		    String providerUserId,
		    String provider
		);
}
