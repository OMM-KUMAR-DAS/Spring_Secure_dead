package com.example.security.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "auth_identities",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "providerUserId"})
    }
)
public class AuthIdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name="provider_type",nullable = false)
    private String provider;

    @Column(name="provider_id",nullable = false)
    private String providerUserId;

    @Column(name="provider_specific_email")
    private String providerEmail;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    private LocalDateTime createdAt;
    
    @Column(name="last_login_at")
    private LocalDateTime lastLogin;
}