package com.oms.auth.service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;

@Service
public class KeycloakServiceImpl implements KeycloakService {

	private static final Logger log = LoggerFactory.getLogger(KeycloakServiceImpl.class);
	private final Keycloak keycloak;

	public KeycloakServiceImpl(Keycloak keycloak) {

		this.keycloak = keycloak;
	}

	@Override
	@Transactional
	public void createUser(String username, String email, String password) {

	    log.info("Creating user in Keycloak for email: {}", email);

	    try {
	        // Prepare credentials
	        CredentialRepresentation credential = new CredentialRepresentation();
	        credential.setTemporary(false);
	        credential.setType(CredentialRepresentation.PASSWORD);
	        credential.setValue(password);

	        // Prepare user
	        UserRepresentation user = new UserRepresentation();
	        user.setUsername(email);
	        user.setEmail(email);
	        user.setEnabled(true);
	        user.setCredentials(List.of(credential));

	        log.debug("Sending request to Keycloak to create user for email: {}", email);

	        Response response = keycloak.realm("springboot-realm").users().create(user);

	        if (response.getStatus() == 201) {
	            log.info("User successfully created in Keycloak for email: {}", email);
	        } else {
	            log.error("Failed to create user in Keycloak. Status: {}, email: {}", response.getStatus(), email);
	            throw new RuntimeException("Keycloak user creation failed with status: " + response.getStatus());
	        }

	    } catch (Exception e) {
	        log.error("Exception occurred while creating user in Keycloak for email: {}", email, e);
	        throw new RuntimeException("Error while creating user in Keycloak", e);
	    }
	}

}
