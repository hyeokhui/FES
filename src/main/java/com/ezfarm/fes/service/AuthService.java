package com.ezfarm.fes.service;

import com.ezfarm.fes.vo.TokenVo;

public interface AuthService {
	
	public TokenVo selectRefreshToken(String id);
	
	public int updateRefreshToken(TokenVo tokenVo);
	
}
