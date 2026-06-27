package com.oms.auth.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleMappingId implements Serializable {

    private Long userId;
    private Long roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleMappingId)) return false;
        UserRoleMappingId that = (UserRoleMappingId) o;
        return userId.equals(that.userId) &&
               roleId.equals(that.roleId);
    }

    
    public UserRoleMappingId() {

		// TODO Auto-generated constructor stub
	}


	@Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
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
