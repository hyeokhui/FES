package com.ezfarm.fes.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.ezfarm.fes.vo.AnswerVo;
import com.ezfarm.fes.vo.TokenVo;

@Mapper
@Repository
public interface AnswerMapper {
	
	public AnswerVo selectAnswer(String id);

	public int insertAnswer(AnswerVo answerVo);
	
}
