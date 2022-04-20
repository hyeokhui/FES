/**
 * 
 */
package com.ezfarm.fes.elastic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

/**
 * 유틸리티 클래스
 * @author cs1492
 * @date   2018. 3. 16.
 *
 */
public class CsUtil {
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CsUtil.class);
	
	/**
	 * object형을 리스트로 변환
	 * @param <T>
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> convertToList(Object obj){
		//리스트이면
		if(List.class == obj || ArrayList.class == obj) {
			return (List<T>)obj;
		}
		
		//배열이면
		if(Object[].class == obj) {
			List<T> list = new ArrayList<T>();
			
			//
			Object[] arr = (Object[])obj;
			for(Object o : arr) {
				list.add((T)o);
			}
			//
			return list;
		}
		
		//
		List<T> list = new ArrayList<T>();
		list.add((T)obj);
		return list;
	}
	
	

	
	/**
	 * map을 clazz의 object로 변환하기
	 * @param map
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @since
	 * 	0205	init
	 */
	public static Object convertToObject(Map<String,Object> map, Class<?> clazz) throws InstantiationException, IllegalAccessException {
		if(isEmpty(map) || null == clazz) {
			LOGGER.info("<<.convertToObject - empty map or null clazz");
			return null;
		}
		
		//
		Object obj = clazz.newInstance();
		
		String k;
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()) {
			k = iter.next();
			
			String methodString = "set" + k.substring(0, 1).toUpperCase() + k.substring(1);
			Method[] methods = obj.getClass().getDeclaredMethods();
			
			//
			for(Method d : methods) {
				if(d.getName().equals(methodString)) {
					try {
						d.invoke(obj, map.get(k));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						LOGGER.error("{}",e);
					}
				}
			}
		}
		
		//
		return obj;
	}
	
	/**
	 * 공백 여부
	 * @param obj 오브젝트
	 * @return
	 * @history
	 * 	20180322	배열, 리스트 처리 추가
	 * 	20200221	Map관련 추가
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj){
		if(isNull(obj)){
			return true;
		}
		
		//문자열
		if(String.class == obj.getClass() ) {
			return (0 == obj.toString().trim().length());
		}
		
		//
		if(obj instanceof Collection) {
			return (0 ==((Collection)obj).size());
		}
		
		//
		if(obj instanceof Map) {
			return (0 == ((Map)obj).size());
		}
		
		//
		if(Set.class == obj.getClass()) {
			return (0 == ((Set)obj).size());
		}
		
		//리스트
		if(List.class == obj.getClass() || (ArrayList.class == obj.getClass())) {
			return (0 == ((List)obj).size());
		}
		
		
		//배열
		if(obj.getClass().toString().contains("[L")) {
			return (0 == Array.getLength(obj));
		}
		
		//
		return (0 == obj.toString().length());
	}
	
	/**
	 * json 문자열인지 여부
	 * [나 {로 시작하면 json이라고 판단
	 * @param str
	 * @return
	 * @history
	 * 	0102	init
	 */
	public static boolean isJsonString(String str) {
		if(isEmpty(str)) {
			return false;
		}
		
		//
		return str.trim().startsWith("[") || str.trim().startsWith("{");
	}
	
	
	/**
	 * isEmpty의 반대
	 * @param str 문자열
	 * @return true / false
	 * 	true 조건
	 * 		문자열인 경우 공백이 아니면
	 * 		collection(Set, List,...)인 경우 0 < size
	 * 		배열인 경우 0 < length
	 * 		Map인 경우 0 < size
	 */
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}
	
	
	
	
	/**
	 * !널여부
	 * @param obj 오브젝트
	 * @return
	 */
	public static boolean isNotNull(Object obj) {
		return !isNull(obj);
	}
	

	/**
	 * 널여부 검사
	 * @param obj 오브젝트
	 * @return
	 */
	public static boolean isNull(Object obj){
		return (null == obj);
	}
	

	/**
	 * 문자열이 숫자인지 검사
	 * @param str 문자열
	 * @return true(문자열이 숫자) / false
	 */
	public static boolean isNum(String str) {
		try {
			Integer.parseInt(str);
			return true;
		}catch(Exception e) {
			LOGGER.error("{}",e.getMessage());
			return false;
		}
	}
	
	
}
