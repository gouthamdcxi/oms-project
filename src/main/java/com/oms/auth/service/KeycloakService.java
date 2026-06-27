package com.oms.auth.service;

public interface KeycloakService {

	void createUser(String username, String email, String password);
}
