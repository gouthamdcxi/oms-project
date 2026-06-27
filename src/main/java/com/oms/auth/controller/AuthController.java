package com.oms.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oms.auth.dto.AuthRequest;
import com.oms.auth.dto.LoginRequest;
import com.oms.auth.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("auth-service/oms/")
public class AuthController {

	private AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public String register(@Valid @RequestBody AuthRequest request) {

		String response = authService.register(request);

		return response;
	}
	
	@GetMapping("/profile")
	public String profile(Authentication authentication) {

	    Jwt jwt = (Jwt) authentication.getPrincipal();

	    String email = jwt.getClaim("email");

	    return "Logged in user: " + email;
	}
	
	@PostMapping("/login")
	public Map<String, Object> login(@Valid @RequestBody LoginRequest request) {
	    return authService.login(request);
	}

}
