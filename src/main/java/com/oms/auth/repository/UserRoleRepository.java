package com.oms.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.auth.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	Optional<UserRole> findByRoleName(String roleName);
}
