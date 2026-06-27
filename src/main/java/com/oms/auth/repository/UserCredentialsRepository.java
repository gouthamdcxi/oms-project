package com.oms.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oms.auth.entity.UserCredentials;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, Long>{

}
