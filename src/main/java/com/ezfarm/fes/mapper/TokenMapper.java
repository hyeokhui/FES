package com.ezfarm.fes.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.ezfarm.fes.vo.TokenVo;

@Mapper
@Repository
public interface TokenMapper {
	
	public TokenVo selectRefreshToken(String id);

	public int updateRefreshToken(TokenVo tokenVo);
	
}
