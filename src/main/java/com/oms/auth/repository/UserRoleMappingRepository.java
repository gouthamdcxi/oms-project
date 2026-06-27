package com.oms.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.auth.entity.UserRoleMapping;
import com.oms.auth.entity.UserRoleMappingId;

public interface UserRoleMappingRepository extends JpaRepository<UserRoleMapping, UserRoleMappingId> {

}
