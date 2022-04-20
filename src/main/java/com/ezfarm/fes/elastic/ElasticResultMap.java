/**
 * 
 */
package com.ezfarm.fes.elastic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.gson.Gson;


/**
 * 엘라스틱 호출 결과 저장 맵
 * @author hyunseongkil
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class ElasticResultMap extends HashMap {



	//
	private static final Logger LOG = LoggerFactory.getLogger(ElasticResultMap.class);
	
	
	/**
	 * 원본 문자열
	 */
	public static final String KEY_RAW_STRING = "rawstring";
	/**
	 * data
	 */
	public static final String KEY_DATA = "data";
	/**
	 * hits
	 */
	public static final String KEY_HITS = "hits";
	
	public static final String KEY_HITS_HITS = "hitsHits";
	
	/**
	 * aggregations
	 */
	public static final String KEY_AGGREGATIONS = "aggregations";
	/**
	 * _sources 목록
	 */
	public static final String KEY_SOURCES = "sources";
	/**
	 * _source의 키 목록
	 */
	public static final String KEY_KEYS = "keys";
	/**
	 * vo 목록
	 */
	public static final String KEY_VOS = "vos";
	
	
	/**
	 * jsonString을 각각 나누어 map으로 저장
	 * @param jsonString
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public ElasticResultMap(String jsonString) throws InstantiationException, IllegalAccessException {
		super();
		
		//
		if(CsUtil.isEmpty(jsonString)) {
		//if(StringUtils.isEmpty(jsonString)) {
			LOG.info("<<.ElasticResultMap - empty jsonString");
			return;
		}
		
		//
		if(!jsonString.trim().startsWith("{")) {
			LOG.info("<<.ElasticResultMap - not jsonString:{}", jsonString);
			return;
		}
		
		//
		putRawString(jsonString);
		
		//
		putData((String)this.get(KEY_RAW_STRING));
		
		//
		putHits((Map<String,Object>)this.get(KEY_DATA));
		
		//
		putHitsHits((Map<String,Object>)this.get(KEY_HITS));
		
		//
		putSources((Map<String,Object>)this.get(KEY_HITS));
		
		//
		putKeys((List<Map<String,Object>>)this.get(KEY_SOURCES));
		
		//
		putAggregations((Map<String,Object>)this.get(KEY_DATA));
		
	}


	@SuppressWarnings("unchecked")
	private void putHitsHits(Map<String, Object> hitsMap) {
		if(CsUtil.isEmpty(hitsMap) || !hitsMap.containsKey("hits")) {
		//if((null == hitsMap) || !hitsMap.containsKey("hits")) {
			this.put(KEY_HITS_HITS, new ArrayList<Map<String,Object>>());
			return;
		}
		
		//
		this.put(KEY_HITS_HITS, hitsMap.get("hits"));
	}


	/**
	 * jsonString을 vo로 변환하는 과정 포함
	 * @param <T>
	 * @param jsonString
	 * @param clazz vo's class
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings({ "unchecked" })
	public <T extends CsVO> ElasticResultMap(String jsonString, Class<?> clazz) throws InstantiationException, IllegalAccessException {
//		super();
		
		//
		this(jsonString);
		
		//
		putVos((List<Map<String, Object>>) this.get(KEY_SOURCES), clazz);
				
	}
	
	
	/**
	 * aggregations
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getAggregations(){
		return (Map<String, Object>) this.get(KEY_AGGREGATIONS);
	}


	/**
	 * get data map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getData(){
		return (Map<String, Object>) this.get(KEY_DATA);
	}


	/**
	 * get hits map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getHits(){
		return (Map<String, Object>) this.get(KEY_HITS);
	}
	
	/**
	 * get hits list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getHitsInHits(){
		return (List<Map<String, Object>>) this.get(KEY_HITS_HITS);
	}


	/**
	 * get map's key 목록
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getKeys(){
		return (Set<String>) this.get(KEY_KEYS);
	}


	/**
	 * get _source map 목록
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> getSources(){
		return (List<Map<String, Object>>) this.get(KEY_SOURCES);
	}


	/**
	 * get vo 목록
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends CsVO> List<T> getVos(){
		return (List<T>) this.get(KEY_VOS);
	}
	
	
	/**
	 * 원본 문자열
	 * @return
	 */
	public String getRawString() {
		return (String) this.get(KEY_RAW_STRING);
	}
	
	
	/**
	 * aggregations
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	private void putAggregations(Map<String, Object> data) {
		this.put(KEY_AGGREGATIONS, (data.containsKey(KEY_AGGREGATIONS) ? data.get(KEY_AGGREGATIONS) : new HashMap<String, Object>()));
	}
	
	
	/**
	 * put data map
	 * @param jsonString
	 */
	@SuppressWarnings("unchecked")
	private void putData(String jsonString) {		
		Map<String,Object> dataMap = new Gson().fromJson(jsonString, Map.class);
		
		//
		this.put(KEY_DATA, dataMap);		
	}
	
	
	/**
	 * put hits map
	 * @param dataMap
	 */
	@SuppressWarnings("unchecked")
	private void putHits(Map<String, Object> dataMap) {
		if(CsUtil.isEmpty(dataMap)) {
		//if(null == dataMap) {
			this.put(KEY_HITS, new HashMap<String, Object>());
			return;
		}
		
		//
		if(dataMap.containsKey("hits")) {
			this.put(KEY_HITS, (Map<String,Object>) dataMap.get("hits"));
		}else {
			this.put(KEY_HITS, new HashMap<String, Object>());
		}

	}
	
	/**
	 * put map's key 목록
	 * @param sources
	 */
	@SuppressWarnings("unchecked")
	private void putKeys(List<Map<String, Object>> sources) {
		//
		Set<String> keys = new HashSet<String>();
		this.put(KEY_KEYS, keys);
		
		//
		if(CsUtil.isEmpty(sources)) {
		//if(null == sources) {
			return;
		}
		
		//
		for(Map<String,Object> d : sources) {
			keys.addAll( d.keySet());
		}
	}
	
	
	/**
	 * put source 목록
	 * @param hitsMap
	 */
	@SuppressWarnings("unchecked")
	private void putSources(Map<String, Object> hitsMap) {
		
		List<Map<String,Object>> listOfMap = new ArrayList<Map<String,Object>>();
		this.put(KEY_SOURCES, listOfMap);

		//
		if(CsUtil.isEmpty(hitsMap)) {
		//if(null == hitsMap) {
			return;
		}
		
		
		//
		List<Map<String,Object>> hits = (List<Map<String, Object>>) hitsMap.get("hits");
		
		//
		for(Map<String,Object> d : hits) {
			
			if(d.containsKey("_source")) {
				
				Map<String,Object> sourceMap = (Map<String, Object>) d.get("_source");
				//
				Double score = (Double) d.get("_score");
				sourceMap.put("score", score);
				
				listOfMap.add(sourceMap);
			}
		}	
	}
	
	/**
	 * put 원본 문자열
	 * @param jsonString
	 */
	@SuppressWarnings("unchecked")
	private void putRawString(String jsonString) {
		this.put(KEY_RAW_STRING, jsonString);
	}

	/**
	 * put vo 목록
	 * @param <T>
	 * @param sources
	 * @param clazz vo's class
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private <T extends CsVO> void putVos(List<Map<String, Object>> sources, Class<?> clazz) throws InstantiationException, IllegalAccessException {
		List<T> listOfVo = new ArrayList<T>();
		this.put(KEY_VOS, listOfVo);
		
		//
		for(Map<String,Object> d : sources) {
			T vo = (T) CsUtil.convertToObject(d, clazz);
			listOfVo.add(vo);
		}
	}
	
	
}
