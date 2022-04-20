package com.ezfarm.fes.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;

import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.MapUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import com.ezfarm.fes.elastic.ElasticResultMap;
import com.ezfarm.fes.elastic.service.ElasticService;
import com.ezfarm.fes.elastic.service.impl.ElasticServiceImpl;
import com.ezfarm.fes.enumeration.ApiUrl;
import com.ezfarm.fes.service.AuthService;
import com.ezfarm.fes.service.QnaService;
import com.ezfarm.fes.util.HttpUtil;
import com.ezfarm.fes.util.SHA256;
import com.ezfarm.fes.vo.AnswerVo;
import com.ezfarm.fes.vo.QuestionVo;
import com.ezfarm.fes.vo.TokenVo;


@Controller
@RequestMapping("/qna")
public class QnaController {

	@Autowired
	private SHA256 sha256;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private QnaService qnaService;
	
	@Value("${aiad.api.id}")
	private String apiId;
	
	@Value("${minio.url}")
	private String minioUrl;
	
	@Value("${minio.id}")
	private String minioId;

	@Value("${minio.pw}")
	private String minioPw;
	
	Logger logger = LoggerFactory.getLogger(QnaController.class);
	
	
	// 최초 답변을 가져옴
	@RequestMapping("/getResult")
	public ResponseEntity<?> getResult(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {


		HttpSession httpSession = request.getSession(true);
		//String userId = (String) httpSession.getAttribute("USER_ID");
		String userId = apiId;
		
		TokenVo tokenVo = authService.selectRefreshToken(userId);
		String token = tokenVo.getRefreshToken();
		
		// 질문
		String question = (String) param.get("question");
		//SHA256으로 암호화된 질문해시
        String questionHash = sha256.encrypt(question);
        String apiParam = "";
        
        String ansType = (String) param.get("ansType");	// 답변 타입 : 백서/뉴스/공지
        String searchType = (String) param.get("searchType");	// 검색 타입 : 간편검색/정밀검색
        
        ObjectMapper mapper = new ObjectMapper();
        
        long beforeTime1 = System.currentTimeMillis();
        
        String index = "";
		String qry = "";
		
		ElasticResultMap pdfResult = null;
		ElasticResultMap articleResult = null;
		ElasticResultMap guideResult = null;
		
		List<Map<String, Object>> pdfParagraphSources = null;
		List<Map<String, Object>> articleParagraphSources = null;
		List<Map<String, Object>> guideParagraphSources = null;
		
		List<Map<String, Object>> paragraphResultSources = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> splitParagraphResultSources = new ArrayList<Map<String,Object>>();
		
		int pdfParagraphArrSize = 50;
		int articleParagraphArrSize = 600;
		int guideParagraphArrSize = 600;
		
		if("all".equals(ansType)) {
			pdfResult = qnaService.getPdfParagraphResult(question);
			articleResult = qnaService.getArticleParagraphResult(question);
			guideResult = qnaService.getGuideParagraphResult(question);
			
			// 랭킹 정렬 (평균 score 계산 + 점수 내림차순 정렬)
			pdfParagraphSources = qnaService.getRankedParagraphList(pdfResult.getSources());
			articleParagraphSources = qnaService.getRankedParagraphList(articleResult.getSources());
			guideParagraphSources = qnaService.getRankedParagraphList(guideResult.getSources());
			
			if(pdfParagraphSources.size() < 50) {
				pdfParagraphArrSize = pdfParagraphSources.size();
			}
			
			if(articleParagraphSources.size() < 600) {
				articleParagraphArrSize = articleParagraphSources.size();
			}
			
			if(guideParagraphSources.size() < 600) {
				guideParagraphArrSize = guideParagraphSources.size();
			}
			
			// 최종 문단 리스트
			pdfParagraphSources = qnaService.getParagraphResultByDocId(pdfParagraphSources.subList(0, pdfParagraphArrSize), question);
			articleParagraphSources = qnaService.getParagraphResultByDocId(articleParagraphSources.subList(0, articleParagraphArrSize), question);
			guideParagraphSources = qnaService.getParagraphResultByDocId(guideParagraphSources.subList(0, guideParagraphArrSize), question);
			
			paragraphResultSources.addAll(pdfParagraphSources);
			paragraphResultSources.addAll(articleParagraphSources);
			paragraphResultSources.addAll(guideParagraphSources);
			
		} else if("pdf".equals(ansType)) {
			pdfResult = qnaService.getPdfParagraphResult(question);
			
			pdfParagraphSources = qnaService.getRankedParagraphList(pdfResult.getSources());
			
			if(pdfParagraphSources.size() < 50) {
				pdfParagraphArrSize = pdfParagraphSources.size();
			}
			
			pdfParagraphSources = qnaService.getParagraphResultByDocId(pdfParagraphSources.subList(0, pdfParagraphArrSize), question);
			
			paragraphResultSources.addAll(pdfParagraphSources);
			
		} else if("article".equals(ansType)) {
			articleResult = qnaService.getArticleParagraphResult(question);
			
			articleParagraphSources = qnaService.getRankedParagraphList(articleResult.getSources());
			
			if(articleParagraphSources.size() < 600) {
				articleParagraphArrSize = articleParagraphSources.size();
			}
			
			articleParagraphSources = qnaService.getParagraphResultByDocId(articleParagraphSources.subList(0, articleParagraphArrSize), question);
			
			paragraphResultSources.addAll(articleParagraphSources);
			
		}  else if("guide".equals(ansType)) {
			guideResult = qnaService.getGuideParagraphResult(question);
			
			guideParagraphSources = qnaService.getRankedParagraphList(guideResult.getSources());
			
			if(guideParagraphSources.size() < 600) {
				guideParagraphArrSize = guideParagraphSources.size();
			}
			
			guideParagraphSources = qnaService.getParagraphResultByDocId(guideParagraphSources.subList(0, guideParagraphArrSize), question);
			
			paragraphResultSources.addAll(guideParagraphSources);
		}
		
		// 문장리스트를 유사도점수(score) 내림차순으로 정렬
		Collections.sort(paragraphResultSources, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String strProb1 = String.valueOf(o1.get("score")) == ""? "0" : String.valueOf(o1.get("score"));
				String strProb2 = String.valueOf(o2.get("score")) == ""? "0" : String.valueOf(o2.get("score"));
				
				Double prob1 = Double.parseDouble(strProb1);
				Double prob2 = Double.parseDouble(strProb2);
				
				prob1 = (prob1 == null) ? 0 : prob1;
				prob2 = (prob2 == null) ? 0 : prob2;
				
				return prob2.compareTo(prob1);
			}
		});
		
		//double afterTime1 = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
		//double secDiffTime1 = (afterTime1 - beforeTime1)/1000; //두 시간에 차 계산
		//System.out.println("elastic size 200 시간차이(m) : "+secDiffTime1);
		
		int searchVolum = 0;
		// 간편 : 20 , 정밀 : 240
        if("simple".equals(searchType)) {
        	searchVolum = 20;
        } else if("exact".equals(searchType)) {
        	searchVolum = 240;
        }
		
		List<HashMap<String, Object>> mrcResultMapList = new ArrayList<HashMap<String, Object>>();
		// 간편/정밀 검색에 따라 해당 리스트 개수만큼 기계독해 api 호출
		mrcResultMapList = qnaService.getMrcResultMapList(token, question, paragraphResultSources.subList(0, searchVolum), searchType);
		
		// mrc 결과 리스트 docId와 매칭되는 aiad_paragraph source 순서 정렬
		List<Map<String, Object>> mrcParagraphSources = new ArrayList<Map<String,Object>>();
		
		for(int i=0; i<mrcResultMapList.size(); i++) {
			String docId = (String) mrcResultMapList.get(i).get("docId");
			String mrcScoreStr = String.valueOf(mrcResultMapList.get(i).get("probability")) == ""? "0" : String.valueOf(mrcResultMapList.get(i).get("probability"));
			Double mrcScore = Double.parseDouble(mrcScoreStr);
			
			// 문장리스트 value에는 mrc score가 없으므로 mrc docId와 매칭되는 문장리스트에 mrc score를 넣음 
			Map result = paragraphResultSources.stream().filter(x -> x.get("doc_id_STR").equals(docId)).findAny().get();
			result.put("mrcScore", mrcScore);
			
			mrcParagraphSources.add(result);
		}
		
		// ElasticSearch (index - aiad_doc)통신 결과값 return
		List<Map<String, Object>> aiadDocResultSources = new ArrayList<Map<String,Object>>();
		aiadDocResultSources = qnaService.getAiadDocList(mrcResultMapList);
		
		Map<String, Object> res = new HashMap<>();
		res.put("mrcResultMapList", mrcResultMapList);	// 기계독해 결과 list
		res.put("mrcParagraphSources", mrcParagraphSources);	// ElasticSearch (index - aiad_paragraph)통신 결과 source
		res.put("aiadDocResultSources", aiadDocResultSources);	// ElasticSearch (index - aiad_doc)통신 결과 source
		res.put("paramMap", param);	// 질문 parameter
		
		res.put("pdfParagraphSources", pdfParagraphSources);
		res.put("articleParagraphSources", articleParagraphSources);
		res.put("guideParagraphSources", guideParagraphSources);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	// 추가검색 답변 가져옴
	@RequestMapping("/getMoreResult")
	public ResponseEntity<?> getMoreResult(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		HttpSession httpSession = request.getSession(true);
		//String userId = (String) httpSession.getAttribute("USER_ID");
		String userId = apiId;
		
		TokenVo tokenVo = authService.selectRefreshToken(userId);
		String token = tokenVo.getRefreshToken();
		
		// 질문
		String question = (String) param.get("question");
		
        List<Map<String, Object>> paragraphResultSources = (List<Map<String, Object>>) param.get("moreParagraphList");
        List<HashMap<String, Object>> moreMrcResultMapList = new ArrayList<HashMap<String, Object>>();
        moreMrcResultMapList = qnaService.getMrcResultMapList(token, question, paragraphResultSources, null);
        
        // mrc 결과 리스트 docId와 매칭되는 aiad_paragraph source 순서 정렬
 		List<Map<String, Object>> moreMrcParagraphSources = new ArrayList<Map<String,Object>>();
 		
 		for(int i=0; i<moreMrcResultMapList.size(); i++) {
 			String docId = (String) moreMrcResultMapList.get(i).get("docId");
 			String mrcScoreStr = String.valueOf(moreMrcResultMapList.get(i).get("probability")) == ""? "0" : String.valueOf(moreMrcResultMapList.get(i).get("probability"));
			Double mrcScore = Double.parseDouble(mrcScoreStr);
			
 			Map result = paragraphResultSources.stream().filter(x -> x.get("doc_id_STR").equals(docId)).findAny().get();
 			result.put("mrcScore", mrcScore);
 			
 			moreMrcParagraphSources.add(result);
 		}
 		
 		// ElasticSearch (index - aiad_doc)통신 결과값 return
 		List<Map<String, Object>> moreAiadDocResultSources = new ArrayList<Map<String,Object>>();
 		moreAiadDocResultSources = qnaService.getAiadDocList(moreMrcResultMapList);
		
		Map<String, Object> res = new HashMap<>();
		res.put("moreMrcResultMapList", moreMrcResultMapList);	// 기계독해 결과 list
		res.put("moreMrcParagraphSources", moreMrcParagraphSources);	// ElasticSearch (index - aiad_paragraph)통신 결과 source
		res.put("moreAiadDocResultSources", moreAiadDocResultSources);	// ElasticSearch (index - aiad_doc)통신 결과 source
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	// minio client 연결해서 pdf 가져옴
	@RequestMapping("/getPdf")
	public ResponseEntity<?> getPdf(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		// MINIO api 요청 부분
		
		String url = (String) param.get("url");
		String docTitleStr = (String) param.get("docTitleStr");
		String objectStr = url.replace(minioUrl + "/aiad", "");
		
		MinioClient minioClient =
			    MinioClient.builder()
			        .endpoint(minioUrl)
			        .credentials(minioId, minioPw)
			        .build();
		
		byte[] pdfByteArray = null;
		try {
			boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("aiad").build());
			if (found) {
				//pdfByteArray = IOUtils.toByteArray(minioClient.getObject(GetObjectArgs.builder().bucket("aiad").object(docTitleStr).build()));
				
				// pdf object을 InputStream 처리
				InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket("aiad").object(objectStr).build());
				// InputStream을 byte 배열 처리
				pdfByteArray = IOUtils.toByteArray(stream);
				
			}
		} catch (Exception e) {
			System.out.println("Error occurred: " + e);
		}
		
		// byte 배열을 base64 인코딩
		String endcodedPdfByteArray = Base64.getEncoder().encodeToString(pdfByteArray);
		Map<String, Object> res = new HashMap<>();
		res.put("pdfByteArray", pdfByteArray);
		res.put("endcodedPdfByteArray", endcodedPdfByteArray);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	// 답변 저장
	@RequestMapping("/insertAnswer")
	public ResponseEntity<?> insertAnswer(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		String ansContent = (String) param.get("ansContent");
		String docId = (String) param.get("docId");
		String paragraphId = (String) param.get("paragraphId");
		
		AnswerVo answerVo = new AnswerVo();
		answerVo.setAnsContent(ansContent);
		answerVo.setDocId(docId);
		answerVo.setParagraphId(paragraphId);
		
		qnaService.insertAnswer(answerVo);
		answerVo = qnaService.selectAnswer(answerVo);
		
		int ansSeq = answerVo.getAnsSeq();
		
		Map<String, Object> res = new HashMap<>();
		res.put("ansSeq", ansSeq);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	// 답변 조회
	@RequestMapping("/selectAnswer")
	public ResponseEntity<?> selectAnswer(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		int ansSeq = (int) param.get("ansSeq");
		AnswerVo answerVo = new AnswerVo();
		answerVo.setAnsSeq(ansSeq);
		
		answerVo = qnaService.selectAnswer(answerVo);
		
		Map<String, Object> res = new HashMap<>();
		res.put("answer", answerVo);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	// 의견 저장
	@RequestMapping("/insertQuestionOpinion")
	public ResponseEntity<?> insertQuestionOpinion(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		HttpSession httpSession = request.getSession(true);
		String userId = (String) httpSession.getAttribute("USER_ID");
		int ansSeq = (int) param.get("ansSeq");
		String qstContent = (String) param.get("qstContent");
		String qstOpinion = (String) param.get("qstOpinion");
		int qstAnsRate = (int) param.get("qstAnsRate");
		
		QuestionVo questionVo = new QuestionVo();
		questionVo.setUserId(userId);
		questionVo.setAnsSeq(ansSeq);
		questionVo.setQstContent(qstContent);
		questionVo.setQstOpinion(qstOpinion);
		questionVo.setQstAnsRate(qstAnsRate);
		
		qnaService.insertQuestionOpinion(questionVo);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
