package com.ezfarm.fes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ezfarm.fes.mapper.RoleMapper;
import com.ezfarm.fes.service.RoleService;
import com.ezfarm.fes.vo.RoleVo;

@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	RoleMapper roleMapper;

	@Override
	public List<RoleVo> selectRoles() {
		return roleMapper.selectRoles();
	}	
	
}
