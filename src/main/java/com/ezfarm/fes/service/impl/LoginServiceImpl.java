package com.ezfarm.fes.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezfarm.fes.mapper.TokenMapper;
import com.ezfarm.fes.service.LoginService;
import com.ezfarm.fes.vo.TokenVo;

@Service
public class LoginServiceImpl implements LoginService {
	
	@Autowired
    private TokenMapper tokenMapper;
	
	
	@Override
	public TokenVo selectRefreshToken(String id) {
		return tokenMapper.selectRefreshToken(id);
	}

	@Override
	public int updateRefreshToken(TokenVo tokenVo) {
		return tokenMapper.updateRefreshToken(tokenVo);
	}
	
}
