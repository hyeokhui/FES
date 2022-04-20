package com.ezfarm.fes.service;

import com.ezfarm.fes.vo.TokenVo;

public interface LoginService {
	
	public TokenVo selectRefreshToken(String id);
	
	public int updateRefreshToken(TokenVo tokenVo);
	
}
