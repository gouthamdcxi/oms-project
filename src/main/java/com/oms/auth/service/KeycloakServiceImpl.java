package com.oms.auth.service;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
public class KeycloakServiceImpl implements KeycloakService {

	private final Keycloak keycloak;

	public KeycloakServiceImpl(Keycloak keycloak) {

		this.keycloak = keycloak;
	}

	@Override
	public void createUser(String username, String email, String password) {
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setTemporary(false);
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(password);

		UserRepresentation user = new UserRepresentation();
		user.setUsername(email);
		user.setEmail(email);
		user.setEnabled(true);
		user.setCredentials(List.of(credential));

		keycloak.realm("springboot-realm").users().create(user);
	}

}
