package com.ezfarm.fes.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.ezfarm.fes.vo.RoleVo;
import com.ezfarm.fes.vo.UserVo;

@Mapper
@Repository
public interface RoleMapper {

	public List<RoleVo> selectRoles();

	public List<String> selectRolesByUserUserSeq(Long userSeq);
	
	public void insertRolesByUserSeq(UserVo userVO);

	public void deleteRolesByUserSeq(UserVo userVO);

}
