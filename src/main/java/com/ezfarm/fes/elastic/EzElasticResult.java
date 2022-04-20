/**
 * 
 */
package com.ezfarm.fes.elastic;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * @Class Name : EzElasticResult.java
 * @Description : 
 * @Modification Information
 *
 * @author 
 * @since 
 * @version 1.0
 * @see
 *
 * << 개정이력(Modification Information) >>
 *
 *   수정일      수정자          수정내용
 *  -----------------------------------------
 */

public class EzElasticResult {
	private String jsonStr;
	private Gson gson;
	
	JsonObject joResult = null;		// result set 전체
	JsonObject joShards = null;		// _shards
	JsonObject joHits = null;		// hits
	JsonArray jaHits = null;		// hits.hits array
	JsonObject joAggr = null;		// aggregations
	
	EzEsResultShards ezEsResultShards = null;		// _shards
	List<EzEsResultHits> ezEsResultHits = null;		// hits.hits[]
	
	public EzElasticResult() {
		super();
		gson = new Gson();
		
		jsonStr = null;
	}
	
	public EzElasticResult(String jsonStr) {
		super();
		gson = new Gson();
		
		this.set(jsonStr);
	}

	public EzElasticResult set(String jsonStr) {
		this.jsonStr = jsonStr;

		if (this.jsonStr == null || this.jsonStr.length() == 0) {
			joResult = null;
			return null;
		}
		
		joResult = gson.fromJson(jsonStr, JsonObject.class);
		if (joResult.isJsonNull())
			return null;
		
		if (joResult.has("_shards"))
			joShards = joResult.getAsJsonObject("_shards");
		if (joResult.has("hits"))
			joHits = joResult.getAsJsonObject("hits");
		if (joResult.has("aggregations"))
			joAggr = joResult.getAsJsonObject("aggregations");
		if (joHits != null && joHits.has("hits"))
			jaHits = joHits.getAsJsonArray("hits");
		
		ezEsResultShards = gson.fromJson(joShards, EzEsResultShards.class);
		ezEsResultHits = gson.fromJson(jaHits, new TypeToken<List<EzEsResultHits>>(){}.getType());

		return this;
	}
	
	public String toString() {
		return this.jsonStr;
	}
	
	/**
	 * JSON Array string을 List<T> class 에 담는다.
	 * @param json JSON Array String
	 * @param clazz 결과를 저장할 Class type.
	 * @return 결과가 저장된 List<T>
	 */
	public static <T> List<T> jsonArrayToList(String json, Class<T> clazz) {
	    if (json == null) {
	        return null;
	    }
	    
	    Gson gson = new Gson();
	    
	    return gson.fromJson(json, new TypeToken<List<T>>(){}.getType());
	}
	
	/**
	 * ElasticSearch result를 JsonObject로 반환한다.
	 * @return
	 */
	public JsonObject getResultObject() {
		return joResult;
	}
	
	/**
	 * ElasticSearch result에서 "took" 값을 반환한다.
	 * @return took 값.
	 */
	public long getTook() {
		return joResult.get("took").getAsLong();
	}
	
	/**
	 * ElasticSearch result에서 "timed_out" 값을 반환한다.
	 * @return
	 */
	public boolean getTimedOut() {
		return joResult.get("timed_out").getAsBoolean();
	}
	
	/**
	 * ElasticSearch result에서 "_shards"를 반환한다.
	 * @return 
	 */
	public EzEsResultShards getShards() {
		return ezEsResultShards;
	}
	
	/**
	 * ElasticSearch result에서 "hits" : { "total" } 값을 반환한다.
	 * @return
	 */
	public long getHitsTotal() {
		return joHits.get("total").getAsLong();
	}
	
	/**
	 * ElasticSearch result에서 "hits" : { "max_score" } 값을 반환한다.
	 * @return null 인 경우 -1 return.
	 */
	public long getHitsMaxScore() {
		JsonElement jeMaxScore = joHits.get("max_score");
		
		if (jeMaxScore.isJsonNull())
			return -1;
		
		return jeMaxScore.getAsLong();
	}
	
	/**
	 * ElasticSearch result에서 hits.hits 를 JsonArray형식으로 반환한다.
	 * @return
	 */
	public JsonArray getHitsArray() {
		return jaHits;
	}
	
	/**
	 * ElasticSearch result에서 hits.hits array를 반환한다.
	 * @return
	 */
	public List<EzEsResultHits> getHitsList() {
	    return ezEsResultHits;
	}
	
	/**
	 * ElasticSearch result에서 hits.hits._source 를 List<T> 형식으로 반환한다.
	 * @param clazz 결과를 저장할 Class Type
	 * @return 결과가 저장된 List<T>
	 * @throws Exception
	 */
	public <T> List<T> getHitsSourceList(Class<T> clazz) throws Exception {
		List<T> listObj = new ArrayList<T>();
		
		if (ezEsResultHits == null)
			return null;

		for (EzEsResultHits h : ezEsResultHits) {
			listObj.add(gson.fromJson(h.get_source(), clazz));
		}
		
		return listObj;
	}
	
	/**
	 * hits.hits._source[] 에서 주어진 element name의 값을 array로 반환한다.
	 * @param elementName 값을 가져오고자 하는 element name
	 * @return List<String> 형식의 반환값
	 * @throws Exception
	 */
	public List<String> getHitsSourceStringList(String elementName) throws Exception {
		List<String> listString = new ArrayList<String>();
		
		if (ezEsResultHits == null)
			return null;
		
		for (EzEsResultHits h : ezEsResultHits) {
			listString.add(h.get_source().getAsJsonObject().get(elementName).getAsString());
		}
		
		return listString;
	}
	
	/**
	 * ElasticSearch result 에서 hits.hits[index]._source 를 Class Object로 반환 
	 * @param index 가져오고자 하는 hits 위치
	 * @param clazz Class type
	 * @return
	 * @throws Exception
	 */
	public <T> T getHitsSource(int index, Class<T> clazz) throws Exception {
		return gson.fromJson(ezEsResultHits.get(index).get_source(), clazz);
	}
	
	/**
	 * Elastic query 단건의 result 결과에서 특정 key의 값을 가져온다.
	 * @param index hits[] 위치 
	 * @param elementName 가져오고자 하는 Key 값
	 * @return null이면 해당 값이 없음.
	 * @throws Exception
	 */
	public String getHitsSourceValue(int index, String elementName) throws Exception {
		JsonElement jsonElement = ezEsResultHits.get(index).get_source().get(elementName);
		
		if (jsonElement == null || jsonElement.isJsonNull())
			return null;
		
		return jsonElement.getAsString();
	}
	
	/**
	 * Elastic query 결과에서 aggregation sum, avg 등의 결과 중 원하는 Key 값을 return 한다.
	 * @param aggrName 가져오고자 하는 aggregation name
	 * @return
	 * @throws Exception
	 */
	public String getAggrValue(String aggrName) throws Exception {
		JsonObject jsonObjectKey = joAggr.getAsJsonObject(aggrName);
		if (jsonObjectKey == null || jsonObjectKey.isJsonNull())
			return null;
		
		return jsonObjectKey.get("value").getAsString();
	}

	/**
	 * Elastic query 결과에서 aggregation 의 buckets[] 결과를 List<class> 로 return 한다.
	 * @param aggrName aggregation name
	 * @param clazz Class 형식
	 * @return List<class> 결과
	 * @throws Exception
	 */
	public <T> List<T> getAggrBuckets(String aggrName, Class<T> clazz) throws Exception {
		JsonArray jaBuckets = joAggr.getAsJsonObject(aggrName).getAsJsonArray("buckets");
		
		return gson.fromJson(jaBuckets, new TypeToken<List<T>>(){}.getType());
	}
	
	/**
	 * Elastic query 결과에서 aggregation 의 buckets[] 을 JsonArray로 return 한다.
	 * @param aggrName aggregation name
	 * @return JsonArray 형식의 bucket[]
	 * @throws Exception
	 */
	public JsonArray getAggrBuckets(String aggrName) throws Exception {
		if (joAggr == null)
			return null;
		
		return joAggr.getAsJsonObject(aggrName).getAsJsonArray("buckets");
	}
	
	/**
	 * 주어진 Aggregation 내 buckets[] 의 개수를 return 한다.
	 * @param aggrName Aggregation name
	 * @return buckets 결과 개수
	 * @throws Exception
	 */
	public int getAggrBucketsCount(String aggrName) throws Exception {
		JsonArray jaBuckets = joAggr.getAsJsonObject(aggrName).getAsJsonArray("buckets");
		
		return jaBuckets.size();
	}
	
	/**
	 * 주어진 Aggregation name의 index 번째에 위치한 bucket의 element name에 해당 하는 값을 String 형식으로 return 한다.
	 * @param aggrName Aggregation mame
	 * @param index bucket index
	 * @param elementName Element name
	 * @return String 형식의 값
	 * @throws Exception
	 */
	public String getAggrBucketsValueAsString(String aggrName, int index, String elementName) throws Exception {
		JsonArray jaBuckets = joAggr.getAsJsonObject(aggrName).getAsJsonArray("buckets");
		
		return jaBuckets.get(index).getAsJsonObject().get(elementName).getAsString();
	}
	
	/**
	 * 주어진 Aggregation name의 index 번째에 위치한 bucket의 element name에 해당 하는 값을 int 형식으로 return 한다.
	 * @param aggrName Aggregation mame
	 * @param index bucket index
	 * @param elementName Element name
	 * @return int 형식의 값
	 * @throws Exception
	 */
	public int getAggrBucketsValueAsInt(String aggrName, int index, String elementName) throws Exception {
		JsonArray jaBuckets = joAggr.getAsJsonObject(aggrName).getAsJsonArray("buckets");
		
		return jaBuckets.get(index).getAsJsonObject().get(elementName).getAsInt();
	}
	
	/**
	 * JSON object of aggregations."aggrName".buckets[index].top_hits.hits.hits[0]
	 * @param aggrName
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public EzEsResultHits getAggrTopHits(String aggrName, int index) throws Exception {
		JsonObject hits0 = getAggrBuckets(aggrName).get(index).getAsJsonObject().
				getAsJsonObject("top_hits").getAsJsonObject("hits").getAsJsonArray("hits").get(0).getAsJsonObject();
		
		return gson.fromJson(hits0, EzEsResultHits.class);
	}
	
	/**
	 * List of aggregations."aggrName".buckets[index].top_hits.hits.hits[0]
	 * @param aggrName
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> getAggrTopHitsList(String aggrName, Class<T> clazz) throws Exception {
		JsonArray jaBuckets = this.getAggrBuckets(aggrName);

		List<T> listObj = new ArrayList<T>();
		
		for (int i = 0; i < jaBuckets.size(); i++) {
			listObj.add(gson.fromJson(jaBuckets.get(i).getAsJsonObject().getAsJsonObject("top_hits").
					getAsJsonObject("hits").getAsJsonArray("hits").get(0).getAsJsonObject().getAsJsonObject("_source"), clazz));
		}
		
		return listObj;
	}
}
