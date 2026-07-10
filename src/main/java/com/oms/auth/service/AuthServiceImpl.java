package com.oms.auth.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.oms.auth.dto.AuthRequest;
import com.oms.auth.dto.LoginRequest;
import com.oms.auth.dto.UserSignupEvent;
import com.oms.auth.entity.AuthUser;
import com.oms.auth.entity.UserCredentials;
import com.oms.auth.entity.UserRole;
import com.oms.auth.entity.UserRoleMapping;
import com.oms.auth.kafka.KafkaProducerService;
import com.oms.auth.repository.AuthUserRepository;
import com.oms.auth.repository.UserCredentialsRepository;
import com.oms.auth.repository.UserRoleMappingRepository;
import com.oms.auth.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
//@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	public static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
	
	private final AuthUserRepository authUserRepository;
	private final UserCredentialsRepository credentialsRepository;
	private final UserRoleRepository roleRepository;
	private final UserRoleMappingRepository mappingRepository;
	private final PasswordEncoder passwordEncoder;
	private final KafkaProducerService kafkaProducerService;

	private final KeycloakService keycloakUserService;

	public AuthServiceImpl(AuthUserRepository authUserRepository, UserCredentialsRepository credentialsRepository,
			UserRoleRepository roleRepository, UserRoleMappingRepository mappingRepository,
			PasswordEncoder passwordEncoder, KeycloakService keycloakUserService,
			KafkaProducerService kafkaProducerService) {
		this.authUserRepository = authUserRepository;
		this.credentialsRepository = credentialsRepository;
		this.roleRepository = roleRepository;
		this.mappingRepository = mappingRepository;
		this.passwordEncoder = passwordEncoder;
		this.keycloakUserService = keycloakUserService;
		this.kafkaProducerService = kafkaProducerService;
	}

	@Override
	@Transactional
	public String register(AuthRequest request) {

	    log.info("Register request received for email: {}", request.getEmail());

	    try {
	        // Check if user already exists
	        if (authUserRepository.findByEmail(request.getEmail()).isPresent()) {
	            log.warn("Registration failed - User already exists with email: {}", request.getEmail());
	            return "User Already Exist";
	        }

	        // Create AuthUser
	        AuthUser authUser = new AuthUser();
	        authUser.setEmail(request.getEmail());
	        authUser.setPhone(request.getPhone());
	        authUser.setActive(true);
	        authUser.setCreatedAt(new Date());
	        authUser.setUpdatedAt(new Date());
	        authUser.setUsername(request.getUsername());

	        authUser = authUserRepository.save(authUser);
	        log.info("AuthUser created successfully with ID: {}", authUser.getId());

	        // Save credentials
	        UserCredentials credentials = new UserCredentials();
	        credentials.setUserId(authUser.getId());
	        credentials.setPassword(passwordEncoder.encode(request.getPassword()));
	        credentials.setAccountNonLocked(true);

	        credentialsRepository.save(credentials);
	        log.debug("User credentials saved for userId: {}", authUser.getId());

	        // Assign role
	        UserRole role = roleRepository.findByRoleName("ROLE_USER")
	                .orElseThrow(() -> {
	                    log.error("Role not found: ROLE_USER");
	                    return new RuntimeException("Role not found");
	                });

	        UserRoleMapping mapping = new UserRoleMapping(authUser.getId(), role.getId());
	        mappingRepository.save(mapping);
	        log.debug("Role mapping created for userId: {}", authUser.getId());
	        
	      //keycloakUserService.createUser(request.getUsername(), request.getEmail(), request.getPassword());

	        // Send Kafka event
	        UserSignupEvent event = new UserSignupEvent();
	        event.setEvent("USER_SIGNUP");
	        event.setEmail(authUser.getEmail());
	        event.setName(authUser.getUsername());

	        kafkaProducerService.sendUserSignupEvent(event);
	        log.info("User signup event sent to Kafka for email: {}", authUser.getEmail());

	    } catch (Exception e) {
	        log.error("Error occurred during user registration for email: {}", request.getEmail(), e);
	        return "Registration Failed";
	    }

	    log.info("User registered successfully with email: {}", request.getEmail());
	    return "User Registered Successfully";
	}
	
	@Override
	public Map<String, Object> login(LoginRequest request) {

	    log.info("Login request received for username: {}", request.getUsername());

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
	        log.debug("Sending authentication request to Keycloak for username: {}", request.getUsername());

	        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

	        log.info("Login successful for username: {}", request.getUsername());

	        return response.getBody();

	    } catch (HttpClientErrorException ex) {

	        // Handle invalid credentials
	        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
	            log.warn("Login failed - Invalid credentials for username: {}", request.getUsername());

	            Map<String, Object> error = new HashMap<>();
	            error.put("message", "Invalid username or password");

	            return error;
	        }

	        log.error("Client error during login for username: {}", request.getUsername(), ex);
	        throw ex;

	    } catch (Exception e) {
	        log.error("Unexpected error during login for username: {}", request.getUsername(), e);
	        throw new RuntimeException("Login failed due to internal error");
	    }
	}

}
