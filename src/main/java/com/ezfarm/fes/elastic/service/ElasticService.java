/**
 *
 */
package com.ezfarm.fes.elastic.service;

import java.io.IOException;

import com.ezfarm.fes.elastic.ElasticResultMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * 엘라스틱
 * @author hyunseongkil
 *
 */
public interface ElasticService {
	static final Logger LOG = LoggerFactory.getLogger(ElasticService.class);

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String POSTFIX_SEARCH = "/_search";
	public static final String POSTFIX_COUNT = "/_count";
	public static final String POSTFIX_MAPPING = "/_mapping";
	public static final String POSTFIX_DOC = "/_doc";


	/**
	 * post방식으로 요청
	 * @param index 인덱스
	 * @param qry 쿼리
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @since
	 * 	0204	init
	 */
	void postDoc(String index, String qry) throws IOException, InstantiationException, IllegalAccessException;
	/**
	 * post방식으로 요청
	 * @param index 인덱스
	 * @param qry 쿼리
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @since
	 * 	0204	init
	 */
	ElasticResultMap postSearch(String index, String qry) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * post방식으로 요청
	 * @param index 인덱스
	 * @param qry 쿼리
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @since
	 * 	0204	init
	 */
	ElasticResultMap fesSearch(String index, String qry) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * post방식으로 요청
	 * @param index 인덱스
	 * @param qry 쿼리
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @since
	 * 	0204	init
	 */
	ElasticResultMap postCount(String index, String qry) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * post방식으로 요청
	 * @param index 인덱스
	 * @param qry 쿼리
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @since
	 * 	0204	init
	 */
	ElasticResultMap postMapping(String index, String qry) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * @param index 인덱스명
	 * @param clazz vo's class
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	ElasticResultMap getSearch(String index, Class<?> clazz) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * @param index 인덱스명
	 * @param clazz vo's class
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	ElasticResultMap getCount(String index, Class<?> clazz) throws IOException, InstantiationException, IllegalAccessException;

	/**
	 * @param index 인덱스명
	 * @param clazz vo's class
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	ElasticResultMap getMapping(String index, Class<?> clazz) throws IOException, InstantiationException, IllegalAccessException;

}
