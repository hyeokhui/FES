package com.ezfarm.fes.service;

import java.util.List;

import com.ezfarm.fes.vo.UserVo;
import com.ezfarm.fes.vo.UserVo;

public interface UserService {
	
	public void insertUser(UserVo userVo);
	
	public UserVo selectUserById(String userId);
	
	public List<UserVo> selectUserList(UserVo userVo);
	
	public void updateUser(UserVo userVo);
}
