package com.ezfarm.fes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ezfarm.fes.mapper.RoleMapper;
import com.ezfarm.fes.mapper.UserMapper;
import com.ezfarm.fes.service.UserService;
import com.ezfarm.fes.vo.UserVo;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserMapper userMapper;
	
	@Autowired
	RoleMapper roleMapper;
	
	
	@Transactional
	@Override
	public void insertUser(UserVo userVo) {
		userMapper.insertUser(userVo);
		roleMapper.insertRolesByUserSeq(userVo);
	}

	@Override
	public UserVo selectUserById(String userId) {
		return userMapper.selectUserById(userId);
	}

	@Override
	public List<UserVo> selectUserList(UserVo userVo) {
		return userMapper.selectUserList(userVo);
	}
	
	@Override
	public void updateUser(UserVo userVo) {
		userMapper.updateUser(userVo);
	}
	
	
	
	
	
}
