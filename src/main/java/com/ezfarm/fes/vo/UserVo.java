package com.ezfarm.fes.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@SuppressWarnings("serial")
@Data
public class UserVo implements UserDetails {

	private Long userSeq;
	private String userId;
	private String password;
	private String name;
	private String email;
	private String phone;
	private List<String> role;
	private Date creDt;
	private Date updDt;
	public Collection<Map<String, Object>> authorities;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authority = new ArrayList<>();
		if (this.role != null) {
			for (String _role : this.role) {
				authority.add(new SimpleGrantedAuthority(_role));
			}
			return authority;
		}
		return new ArrayList<SimpleGrantedAuthority>();
	}
	
	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
