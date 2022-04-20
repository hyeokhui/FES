package com.ezfarm.fes.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.MapUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//import com.ezfarm.fes.controller.QnaController.ElasticThread;
//import com.ezfarm.fes.controller.QnaController.MrcThread;
//import com.ezfarm.fes.controller.QnaController.Task;
import com.ezfarm.fes.elastic.ElasticResultMap;
import com.ezfarm.fes.elastic.service.ElasticService;
import com.ezfarm.fes.mapper.QnaMapper;
import com.ezfarm.fes.mapper.RoleMapper;
import com.ezfarm.fes.service.QnaService;
import com.ezfarm.fes.service.RoleService;
import com.ezfarm.fes.util.HttpUtil;
import com.ezfarm.fes.util.SHA256;
import com.ezfarm.fes.vo.AnswerVo;
import com.ezfarm.fes.vo.QuestionVo;
import com.ezfarm.fes.vo.RoleVo;

@Service
public class QnaServiceImpl implements QnaService {
	
	@Autowired
	private SHA256 sha256;
	
	@Autowired
	QnaMapper qnaMapper;
	
	@Autowired
	private ElasticService elasticService;
	

	@Override
	public AnswerVo selectAnswer(AnswerVo answerVo) {
		return qnaMapper.selectAnswer(answerVo);
	}

	@Override
	public void insertAnswer(AnswerVo answerVo) {
		qnaMapper.insertAnswer(answerVo);
	}

	@Override
	public QuestionVo selectQuestion(QuestionVo questionVo) {
		return qnaMapper.selectQuestion(questionVo);
	}

	@Override
	public List<QuestionVo> selectQuestionList(QuestionVo questionVo) {
		return qnaMapper.selectQuestionList(questionVo);
	}

	@Override
	public void insertQuestionOpinion(QuestionVo questionVo) {
		qnaMapper.insertQuestionOpinion(questionVo);
	}

	@Override
	public void updateQuestionOpinion(QuestionVo questionVo) {
		qnaMapper.updateQuestionOpinion(questionVo);
	}
	
	// pdf 계열 문장 list (map형태의 source를 return)
	@Override
	public ElasticResultMap getPdfParagraphResult(String question) {
		ElasticResultMap pdfResult = null;
		List<Map<String, Object>> pdfParagraphSources = null;
		String index = "";
		String qry = "";
		
		index = "/aiad_paragraph";
		// pdf 계열 검색쿼리
		qry += "{";
		qry += "	\"size\": 200,";	
		qry += "	\"_source\": {";
		qry += "	  \"includes\": [\"doc_id_STR\", \"paragraph_text_STR\", \"paragraph_page_INT\", \"paragraph_number_INT\", \"paragraph_id_STR\"]";
		qry += "	},";
		qry += "	\"query\": {";
		qry += "		\"bool\": {";
		qry += "			\"must\": [";
		qry += "				{";
		qry += "					\"match\": {";
		qry += "						\"paragraph_text_STR\": \"" + question + "\"";
		qry += "					}";
		qry += "				},";
		qry += "				{";
		qry += "					\"bool\":	{";
		qry += "						\"should\": [";
		qry += "							{\"match\": {\"channel_name_STR\": \"파일 업로드\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"농림축산검역본부 도서관\"}}";
		qry += "						]";
		qry += "					}";
		qry += "				}";
		qry += "			]";
		qry += "		}";
		qry += "	}";
		qry += "}";
		
		try {
			pdfResult = elasticService.postSearch(index, qry);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		pdfParagraphSources = pdfResult.getSources();
		
		return pdfResult;
	}
	
	// 뉴스 계열 문장 list (map형태의 source를 return)
	@Override
	public ElasticResultMap getArticleParagraphResult(String question) {
		ElasticResultMap articleResult = null;
		String index = "";
		String qry = "";
		
		index = "/aiad_paragraph";
		// 뉴스 계열 검색쿼리
		qry += "{";
		qry += "	\"size\": 600,";	
		qry += "	\"_source\": {";
		qry += "	  \"includes\": [\"doc_id_STR\", \"paragraph_text_STR\", \"paragraph_page_INT\", \"paragraph_number_INT\", \"paragraph_id_STR\"]";
		qry += "	},";
		qry += "	\"query\": {";
		qry += "		\"bool\": {";
		qry += "			\"must\": [";
		qry += "				{";
		qry += "					\"match\": {";
		qry += "						\"paragraph_text_STR\": \"" + question + "\"";
		qry += "					}";
		qry += "				},";
		qry += "				{";
		qry += "					\"bool\":	{";
		qry += "						\"should\": [";
		qry += "							{\"match\": {\"channel_name_STR\": \"양돈타임즈 칼럼\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"양돈타임즈 현장\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"양돈타임즈 해외양돈\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"양돈타임즈 오늘의 뉴스\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"돼지와사람 전체 기사\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"데일리벳 뉴스\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"농민신문 최신 기사\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"축산신문 전체 기사\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"ProMed-Mail\"}}";
		qry += "						]";
		qry += "					}";
		qry += "				}";
		qry += "			]";
		qry += "		}";
		qry += "	}";
		qry += "}";
		
		try {
			articleResult = elasticService.postSearch(index, qry);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return articleResult;
	}

	// 기관공고 계열 문장 list (map형태의 source를 return)
	@Override
	public ElasticResultMap getGuideParagraphResult(String question) {
		ElasticResultMap guideResult = null;
		String index = "";
		String qry = "";
		
		index = "/aiad_paragraph";
		// 기관공고 계열 검색쿼리
		qry += "{";
		qry += "	\"size\": 600,";	
		qry += "	\"_source\": {";
		qry += "	  \"includes\": [\"doc_id_STR\", \"paragraph_text_STR\", \"paragraph_page_INT\", \"paragraph_number_INT\", \"paragraph_id_STR\"]";
		qry += "	},";
		qry += "	\"query\": {";
		qry += "		\"bool\": {";
		qry += "			\"must\": [";
		qry += "				{";
		qry += "					\"match\": {";
		qry += "						\"paragraph_text_STR\": \"" + question + "\"";
		qry += "					}";
		qry += "				},";
		qry += "				{";
		qry += "					\"bool\":	{";
		qry += "						\"should\": [";
		qry += "							{\"match\": {\"channel_name_STR\": \"농림축산식품부 알림소식 보도자료\"}},";
		qry += "							{\"match\": {\"channel_name_STR\": \"농림축산검역본부 알림마당-보도/해명자료\"}}";
		qry += "						]";
		qry += "					}";
		qry += "				}";
		qry += "			]";
		qry += "		}";
		qry += "	}";
		qry += "}";
		
		try {
			guideResult = elasticService.postSearch(index, qry);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return guideResult;
	}
	
	// 기계독해 api 호출
	@Override
	public List<HashMap<String, Object>> getMrcResultMapList(String token, String question, List<Map<String, Object>> paragraphList, String searchType) throws Exception {
		
		// 기계독해 (MRC) API 호출
		// 문장 리스트 사이즈만큼 스레드풀 생성
		ExecutorService executorService1 = Executors.newFixedThreadPool(paragraphList.size());
		executorService1 = Executors.newFixedThreadPool(paragraphList.size());
		List<Future<MrcThread>> mrcFutures = new ArrayList<>();
		
		String apiParam = "";
		ObjectMapper mapper = new ObjectMapper();
		
		for(int i=0; i<paragraphList.size(); i++) {
			
			Map<String,Object> source = paragraphList.get(i);
			
			// MRC API param 값 셋팅
			String score = Double.toString((Double) source.get("score"));
			
			String docId = (String) source.get("doc_id_STR");
			String sentenceIndex = String.valueOf(source.get("paragraph_number_INT"));
			Double sentenceIndexDouble = Double.parseDouble(sentenceIndex);
			//Double sentenceIndex = (Double) source.get("paragraph_number_INT");
			String sentenceIndexStr = Integer.toString(sentenceIndexDouble.intValue());
			String sentence = (String) source.get("paragraph_text_STR");
			
			HashMap<String, Object> mrcParamMap = new HashMap();
			mrcParamMap.put("docId", docId);
			mrcParamMap.put("sentenceIndex", sentenceIndexStr);
			mrcParamMap.put("sentence", sentence);
			mrcParamMap.put("questionHash", sha256.encrypt(question));
			mrcParamMap.put("question", question);
			mrcParamMap.put("similarity", score);
	        //ObjectMapper mapper = new ObjectMapper();
	        
	        apiParam = mapper.writeValueAsString(mrcParamMap);
	        
	        // mrc api 요청 thread 처리
	        MrcThread mrcThread = new MrcThread();
	        Task task = new Task(mrcThread, token, apiParam,  i);
	        
	        Future<MrcThread> future = executorService1.submit(task, mrcThread);
	        
	        mrcFutures.add(future);
		}
		
		// future에서 thread 결과값을 안정적으로 받기 위해 잠시 sleep 
		if("exact".equals(searchType)) {
			Thread.sleep(1000*10);
        }
		
		executorService1.shutdown();
		
		List<HashMap<String, Object>> mrcResultMapList = new ArrayList<HashMap<String, Object>>();
		
		for(int i=0; i<mrcFutures.size(); i++) {
			Future<MrcThread> future = mrcFutures.get(i);
			
			if(!MapUtils.isEmpty(future.get().getResult())) {
				mrcResultMapList.add(future.get().getResult());
			}
		}
		
		// 기계독해(MRC) API return 리스트 정렬(probability 정답확률 내림차순으로 리스트 정렬)
		Collections.sort(mrcResultMapList, new Comparator<HashMap<String, Object>>() {
			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
				
				if(String.valueOf(o1.get("probability")) == "") {
					o1.put("probability", "0.0");
				}
				
				if(String.valueOf(o2.get("probability")) == "") {
					o1.put("probability", "0.0");
				}
				
				String strProb1 = String.valueOf(o1.get("probability")) == ""? "0" : String.valueOf(o1.get("probability"));
				String strProb2 = String.valueOf(o2.get("probability")) == ""? "0" : String.valueOf(o2.get("probability"));
				
				Double prob1 = Double.parseDouble(strProb1);
				Double prob2 = Double.parseDouble(strProb2);
				
				prob1 = (prob1 == null) ? 0 : prob1;
				prob2 = (prob2 == null) ? 0 : prob2;
				
				return prob2.compareTo(prob1);
			}
		});
		
		return mrcResultMapList;
	}
	
	@Override
	public List<Map<String, Object>> getAiadDocList(List<HashMap<String, Object>> mrcMapList) throws Exception {
		
		List<Map<String, Object>> aiadDocResultSources = new ArrayList<Map<String,Object>>();
		
		for(int i=0; i<mrcMapList.size(); i++) {
			
			String index = "/aiad_doc";
			String qry = "";
			String docId = (String) mrcMapList.get(i).get("docId");
			String mrcScoreStr = String.valueOf(mrcMapList.get(i).get("probability")) == ""? "0" : String.valueOf(mrcMapList.get(i).get("probability"));
			Double mrcScore = Double.parseDouble(mrcScoreStr);
			
			qry +=  "{"; 
			qry +=	"	\"_source\": {";
			qry +=  "		\"includes\": [ \"article_date_DT\", \"article_date_STR\", \"channel_language_STR\", \"channel_name_STR\", \"channel_nation_STR\", ";
			qry +=  "			\"crawl_date_DT\", \"detection_keyword_LIST\", \"doc_article_name_STR\", \"doc_author_STR\", \"doc_complete_date_DT\", ";
			qry +=  "			\"doc_id_STR\", \"doc_page_cnt_INT\", \"doc_pdf_url_STR\", \"doc_source_STR\", \"doc_summary_STR\", ";
			qry +=  "			\"doc_text_STR\", \"doc_title_STR\", \"doc_type_STR\", \"extract_keyword_LIST\", \"isbn_STR\", ";
			qry +=  "			\"paragraph_count\", \"published_date_DT\", \"published_date_STR\", \"publisher_STR\", \"trans_txt_url_STR\"";
			qry +=	"		]";
			qry +=	"	},";
			qry +=	"	\"query\": {";
			qry +=	"		\"match\" : {";
			qry +=	"			\"doc_id_STR\": \"" + docId + "\""; 
			qry +=	"		}"; 
			qry +=	"	}";
			qry +=	"}";
			
			ElasticResultMap aiadDocResult = elasticService.postSearch(index, qry);
			
			Map<String, Object> aiadDocResultMap = aiadDocResult.getSources().get(0);
			aiadDocResultMap.put("mrcScore", mrcScore);
			
			aiadDocResultSources.add(aiadDocResult.getSources().get(0));
		}
		
		return aiadDocResultSources;
	}
	
	@Override
	public List<Map<String, Object>> getRankedParagraphList(List<Map<String, Object>> paragraphSources){
		
		Map<String, Map<String, Object>> avgMap = new HashMap<String, Map<String, Object>>();
		
		// 결과값 score 평균
		for(int i=0; i<paragraphSources.size(); i++){
			String docId = paragraphSources.get(i).get("doc_id_STR").toString(); //KEY VALUE
			Map<String, Object> mapObj = (Map<String, Object>) avgMap.get(docId);
			
			if(avgMap.containsKey(docId)){
				//KEY값이 존재하면 해당 키값의 해당되는 값을 가져와 더해줌
				Double frequency = Double.parseDouble(mapObj.get("frequency").toString()) + 1;
				Double score_sum = Double.parseDouble(mapObj.get("score_sum").toString()) + Double.parseDouble(paragraphSources.get(i).get("score").toString());
				Double score_avg = score_sum/frequency;
				
				mapObj.put("frequency", frequency);
				mapObj.put("score_sum", score_sum);
				mapObj.put("score_avg", score_avg);
			}else{
				paragraphSources.get(i).put("frequency", 1);
				paragraphSources.get(i).put("score_sum", Double.parseDouble(paragraphSources.get(i).get("score").toString()));
				paragraphSources.get(i).put("score_avg", Double.parseDouble(paragraphSources.get(i).get("score").toString()));
				
				//KEY값이 존재하지 않으면 MAP에 데이터를 넣어줌
				avgMap.put(docId, paragraphSources.get(i));
			}
		}
		
		List<Map<String, Object>> rankedMapList = avgMap.values().stream().collect(Collectors.toList());
		// 평균값 높은순으로 랭킹
		Collections.sort(rankedMapList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String strProb1 = String.valueOf(o1.get("score_avg"));
				String strProb2 = String.valueOf(o2.get("score_avg"));
				
				Double prob1 = Double.parseDouble(strProb1);
				Double prob2 = Double.parseDouble(strProb2);
				
				prob1 = (prob1 == null) ? 0 : prob1;
				prob2 = (prob2 == null) ? 0 : prob2;
				
				return prob2.compareTo(prob1);
			}
		});
		
		return rankedMapList;
	}
	
	@Override
	public List<Map<String, Object>> getParagraphResultByDocId(List<Map<String, Object>> rankedMapList, String question) throws InterruptedException, ExecutionException{
		
		// 랭킹 정렬된 리스트를 다시 aiad_paragraph에 size 40으로 es 요청
		ExecutorService executorService1 = Executors.newFixedThreadPool(rankedMapList.size());              
		List<Future<ElasticThread>> elasticThreadFutures = new ArrayList<>();
		List<Map<String, Object>> paragraphShortList = new ArrayList<Map<String,Object>>();	// mrc 최종후보 리스트
		
		for(int i=0; i<rankedMapList.size(); i++) {
			String rankedDocId = (String) rankedMapList.get(i).get("doc_id_STR");
			String index = "/aiad_paragraph";
			String qry = "";
			
			qry += "{";
			qry += "	\"size\": 40,";	
			qry += "	\"_source\": {";
			qry += "	  \"includes\": [\"doc_id_STR\", \"paragraph_text_STR\", \"paragraph_page_INT\", \"paragraph_number_INT\", \"paragraph_id_STR\"]";
			qry += "	},";
			qry += "	\"query\": {";
			qry += "		\"bool\": {";
			qry += "			\"must\": [";
			qry += "				{";
			qry += "					\"match\": {";
			qry += "						\"paragraph_text_STR\": \"" + question + "\"";
			qry += "					}";
			qry += "				},";
			qry += "				{";
			qry += "					\"bool\":	{";
			qry += "						\"should\": [";
			qry += "							{\"match\": {\"doc_id_STR\": \"" + rankedDocId + "\"}}";
			qry += "						]";
			qry += "					}";
			qry += "				}";
			qry += "			]";
			qry += "		}";
			qry += "	}";
			qry += "}";
			
			ElasticThread elasticThread = new ElasticThread();
	        ElasticTask task = new ElasticTask(elasticThread, index, qry, "PARAGRAPH", i);
	        
	        Future<ElasticThread> elasticThreadFuture = executorService1.submit(task, elasticThread);
	        elasticThreadFutures.add(elasticThreadFuture);
		}
		
		Thread.sleep(1000*3);
		executorService1.shutdown();
		
		for(int i=0; i<elasticThreadFutures.size(); i++) {
			Future<ElasticThread> future = elasticThreadFutures.get(i);
			
			if(null != future.get().getResult()) {
				paragraphShortList.addAll(future.get().getResult());
			}
		}
		
		// 평균값 높은순으로 랭킹
		Collections.sort(paragraphShortList, new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String strProb1 = String.valueOf(o1.get("resultScore"));
				String strProb2 = String.valueOf(o2.get("resultScore"));
				
				Integer prob1 = Integer.parseInt(strProb1);
				Integer prob2 = Integer.parseInt(strProb2);
				
				prob1 = (prob1 == null) ? 0 : prob1;
				prob2 = (prob2 == null) ? 0 : prob2;
				
				return prob1.compareTo(prob2);
			}
		});
		
		
		return paragraphShortList;
	}
	
	public class ElasticTask implements Runnable {
		private ElasticThread thread;
		private int num = 0;
		private String index;
		private String query;
		private String type;
		
		
		ElasticTask(ElasticThread thread, String index, String query, String type, int num) {
			this.thread = thread;
			this.index = index;
			this.query = query;
			this.type = type;
			this.num = num;
		}
		
		@Override
		public void run() {
			thread.runElasticThread(index, query, type, num);
		}
    }
	
	public class ElasticThread {
		
		private List<Map<String, Object>> elasticResultMapList = new ArrayList<Map<String, Object>>();
		private Map<String, Object> resultMap = new HashMap<String, Object>();
		private int num = 0;
		
		void runElasticThread(String index, String query, String type, int num) {
			
			try {
				//System.out.println("runElasticThread [" + type + "] : " + num);
				
				ElasticResultMap elasticResult = elasticService.postSearch(index, query);
				List<Map<String, Object>> elasticResultSources = elasticResult.getSources();
				
				//System.out.println("runElasticThread [" + type + "] : " + num + " 종료");
				
				if(type.equals("PARAGRAPH")) {
					for(int i=0; i<elasticResultSources.size(); i++) {
						Map<String, Object> elasticSource = elasticResultSources.get(i);
						
						int resultScore = ((num+1)*100) + (i+1);	// 최종점수 = 문서 내 랭킹*100+문서랭킹
						
						elasticSource.put("resultScore", resultScore);
						resultMap = elasticSource;
						
						elasticResultMapList.add(elasticSource);
						//pdfMapShortList.add(rankedPdfSource);
					}
				} else {
					elasticResultMapList.addAll(elasticResultSources);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public List<Map<String, Object>> getResult() {
			return elasticResultMapList;
		}
	}
	
	public class Task implements Runnable {
		private MrcThread thread;
		private String token;
		private String apiParam;
		private int num = 0;
		
		Task(MrcThread thread, String token, String apiParam, int num) {
			this.thread = thread;
			this.token = token;
			this.apiParam = apiParam;
			this.num = num;
		}
		
		@Override
		public void run() {
			try {
				thread.runMrcThread(token, apiParam, num);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
	
	// 
	public class MrcThread {
		
		private List<HashMap<String, Object>> threadMrcResultMapList = new ArrayList<HashMap<String, Object>>();
		private HashMap<String, Object> mrcMap = new HashMap();
		private int num = 0;
		
		void runMrcThread(String token, String apiParam, int num) throws InterruptedException {
			// 기계독해 (MRC) API 호출
			JSONObject mrcResultObj = new JSONObject();
			JSONObject mrcObj = new JSONObject();
	        
			//System.out.println("runMrcThread : " + num);
	        mrcResultObj = HttpUtil.callApi(token, apiParam, "MRC", "POST");
	        Thread.sleep(1000*1);
	        if(null != mrcResultObj) {
	        	mrcObj = (JSONObject) mrcResultObj.get("data");
	        }
	        
	        try {
				mrcMap = new ObjectMapper().readValue(mrcObj.toString(), HashMap.class);
				System.out.println("runMrcThread : " + num + " 종료");
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		
		public HashMap<String, Object> getResult() {
			return mrcMap;
		}
	}

}
