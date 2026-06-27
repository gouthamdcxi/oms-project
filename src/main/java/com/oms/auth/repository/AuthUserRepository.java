package com.oms.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.auth.entity.AuthUser;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long>{

	Optional<AuthUser> findByEmail(String email);
}
