package com.oms.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_role_mapping")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserRoleMappingId.class)
public class UserRoleMapping {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;
    
    

	public UserRoleMapping() {
		
		// TODO Auto-generated constructor stub
	}

	public UserRoleMapping(Long userId, Long roleId) {
		super();
		this.userId = userId;
		this.roleId = roleId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
    
    
}
