package com.ezfarm.fes.vo;

import lombok.Data;

@Data
public class TokenVo {
	private String accessToken;
	private String refreshToken;
	private String token;
	private Integer userSeq;
	private String userId;
	private UserVo user;
}
