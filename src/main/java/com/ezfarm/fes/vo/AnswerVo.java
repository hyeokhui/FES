package com.ezfarm.fes.vo;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AnswerVo {
	private Integer ansSeq;
	private String ansContent;
	private Double ansRate;
	private String docId;
	private String paragraphId;
	private Date creDt;
	private Date updDt;
	private List<QuestionVo> qstList;
}
