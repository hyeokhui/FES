package com.ezfarm.fes.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
	
	SUPERADMIN("ROLE_SUPER_ADMIN"),
	ADMIN("ROLE_ADMIN"),
	USER("ROLE_USER");
	
	private final String role;
	Role(String role) {
		this.role = role;
	}
	
	@JsonValue
	@Override
	public String toString() {
		return role;
	}
}
