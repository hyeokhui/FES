package com.ezfarm.fes.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.ezfarm.fes.vo.AnswerVo;
import com.ezfarm.fes.vo.QuestionVo;
import com.ezfarm.fes.vo.TokenVo;

@Mapper
@Repository
public interface QuestionMapper {
	
	public QuestionVo selectQuestion(String userId);
	
	public List<QuestionVo> selectQuestionList(QuestionVo questionVo);

	public void insertQuestionOpinion(QuestionVo QuestionVo);
	
	public void updateQuestionOpinion(QuestionVo QuestionVo);
	
}
