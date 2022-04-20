package com.ezfarm.fes.jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
	
	// TokenProvider, JwtFilter를 SecurityConfig에 적용하는 역할을 하는 클래스
	
	private TokenProvider tokenProvider;
	
	// TokenProvider를 주입받음
	public JwtSecurityConfig(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	@Override
	public void configure(HttpSecurity http) {
		JwtFilter customFilter = new JwtFilter(tokenProvider);
		
		// JwtFilter를 Security 로직에 필터로써 등록함
		http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
}
