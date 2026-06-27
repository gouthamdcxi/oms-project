package com.oms.auth.service;

import java.util.Map;

import com.oms.auth.dto.AuthRequest;
import com.oms.auth.dto.LoginRequest;

public interface AuthService {

	String register(AuthRequest request);
	
	Map<String, Object> login(LoginRequest request);
}
