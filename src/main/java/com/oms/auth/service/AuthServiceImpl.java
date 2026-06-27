package com.oms.auth.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.oms.auth.dto.AuthRequest;
import com.oms.auth.dto.LoginRequest;
import com.oms.auth.entity.AuthUser;
import com.oms.auth.entity.UserCredentials;
import com.oms.auth.entity.UserRole;
import com.oms.auth.entity.UserRoleMapping;
import com.oms.auth.repository.AuthUserRepository;
import com.oms.auth.repository.UserCredentialsRepository;
import com.oms.auth.repository.UserRoleMappingRepository;
import com.oms.auth.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
//@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthUserRepository authUserRepository;
	private final UserCredentialsRepository credentialsRepository;
	private final UserRoleRepository roleRepository;
	private final UserRoleMappingRepository mappingRepository;
	private final PasswordEncoder passwordEncoder;

	private final KeycloakService keycloakUserService;

	public AuthServiceImpl(AuthUserRepository authUserRepository, UserCredentialsRepository credentialsRepository,
			UserRoleRepository roleRepository, UserRoleMappingRepository mappingRepository,
			PasswordEncoder passwordEncoder, KeycloakService keycloakUserService) {
		this.authUserRepository = authUserRepository;
		this.credentialsRepository = credentialsRepository;
		this.roleRepository = roleRepository;
		this.mappingRepository = mappingRepository;
		this.passwordEncoder = passwordEncoder;
		this.keycloakUserService = keycloakUserService;
	}

	@Override
	public String register(AuthRequest request) {

		if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
//	    		throw new RuntimeException("User Already Exist");
			return "User Already Exist";
		}

		AuthUser authUser = new AuthUser();

		authUser.setEmail(request.getEmail());
		authUser.setPhone(request.getPhone());
		authUser.setActive(true);
		authUser.setCreatedAt(new Date());
		authUser.setUpdatedAt(new Date());
		authUser.setUsername(request.getUsername());

		authUser = authUserRepository.save(authUser);

		UserCredentials credentials = new UserCredentials();
		credentials.setUserId(authUser.getId());
		credentials.setPassword(passwordEncoder.encode(request.getPassword()));
		credentials.setAccountNonLocked(true);

		credentialsRepository.save(credentials);

		UserRole role = roleRepository.findByRoleName("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("Role not found"));

		UserRoleMapping mapping = new UserRoleMapping(authUser.getId(), role.getId());
		mappingRepository.save(mapping);

		keycloakUserService.createUser(request.getUsername(), request.getEmail(), request.getPassword());

		return "User Registered Successfully";
	}

	@Override
	public Map<String, Object> login(LoginRequest request) {

	    String url = "http://localhost:8180/realms/springboot-realm/protocol/openid-connect/token";

	    RestTemplate restTemplate = new RestTemplate();

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
	    body.add("client_id", "oms-client");
	    body.add("grant_type", "password");
	    body.add("username", request.getUsername());
	    body.add("password", request.getPassword());

	    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

	    try {
	        ResponseEntity<Map> response =
	                restTemplate.postForEntity(url, entity, Map.class);

	        return response.getBody();

	    } catch (HttpClientErrorException ex) {

	        // 👇 Handle invalid credentials
	        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {

	            Map<String, Object> error = new HashMap<>();
	            error.put("message", "Invalid username or password");

	            return error;
	        } 

	        throw ex; // other errors
	    }

	    
	}

}
