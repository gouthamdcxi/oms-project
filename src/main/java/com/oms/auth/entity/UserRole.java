package com.oms.auth.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles")
@Data
@Builder
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", unique = true, nullable = false)
    private String roleName;
    
    

	public UserRole() {
		
		// TODO Auto-generated constructor stub
	}

	public UserRole(Long id, String roleName) {
		
		this.id = id;
		this.roleName = roleName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
    
    
}