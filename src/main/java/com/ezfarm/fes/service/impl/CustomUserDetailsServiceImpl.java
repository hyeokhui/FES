package com.ezfarm.fes.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ezfarm.fes.mapper.UserMapper;
import com.ezfarm.fes.service.CustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
	@Autowired
	UserMapper userMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userMapper.selectUserByIdIncludePassword(username);
	}
}
