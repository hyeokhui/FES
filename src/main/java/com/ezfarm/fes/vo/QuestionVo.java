package com.ezfarm.fes.vo;

import java.util.Date;

import lombok.Data;

@Data
public class QuestionVo {
	private Integer qstSeq;
	private Integer ansSeq;
	private String userId;
	private String qstContent;
	private String qstOpinion;
	private Integer qstAnsRate;
	private Date creDt;
	private Date updDt;
}
