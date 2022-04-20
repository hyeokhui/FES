package com.ezfarm.fes.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.ezfarm.fes.service.AuthService;
import com.ezfarm.fes.service.LoginService;
import com.ezfarm.fes.vo.TokenVo;


public class HttpUtil {
	
	@Autowired
	private static AuthService authService;
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	public static final String AUTHORIZATION_HEADER = "Auth-Token";
	public static final String LOGIN = "http://api.aibigdata.northstar.co.kr/api/common/login.do";
	public static final String LOGOUT = "http://api.aibigdata.northstar.co.kr/api/common/logout.do";
	public static final String EMBEDDING = "http://api.aibigdata.northstar.co.kr/api/nlp/embedding.do";
	public static final String MRC = "http://api.aibigdata.northstar.co.kr/api/nlp/mrc.do";
	
	//public static JSONObject callApi(HttpServletRequest request, String param, String urlType, String type){
	public static JSONObject callApi(String token, String param, String urlType, String type){
        
		/*if(null == authService){
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            authService = webApplicationContext.getBean(AuthService.class);
        }*/
		
        HttpURLConnection conn = null;
        JSONObject responseJson = null;
        String callUrl = "";
        
        switch(urlType) {
        	case "LOGIN" : 
        		callUrl = LOGIN;
        		break;
        	case "LOGOUT" : 
        		callUrl = LOGOUT;
        		break;
        	case "EMBEDDING" : 
        		callUrl = EMBEDDING;
        		break;
        	case "MRC" : 
        		callUrl = MRC;
        		break;
        	default : 
        		callUrl = "";
        		break;
        }
        
        try {
            //URL 설정
            //URL url = new URL("http://localhost:8080/test/api/action");
        	URL url = new URL(callUrl);
 
            conn = (HttpURLConnection) url.openConnection();
            
            //String jwt = "";
            
    		// HttpHeader 에 토큰 담음
    		if(!"LOGIN".equals(urlType)) {
    			/*HttpSession httpSession = request.getSession(true);
    			String userId = (String) httpSession.getAttribute("USER_ID");
    			
    			// DB에서 refreshToken 가져옴
    			if(!StringUtils.isEmpty(userId)) {
    				TokenVo token = authService.selectRefreshToken(userId);
    				if(null != token) {
    					jwt = token.getRefreshToken();
    					
    				}
    			}*/
    			
    			if(null != token) {
    				conn.setRequestProperty(AUTHORIZATION_HEADER, token);
    			}
    		}
    		
            // 쿠키에서 토큰 꺼냄
    		/*if(null != request.getCookies()) {
    			jwt = Arrays.stream(request.getCookies()) .filter(c -> c.getName().equals(AUTHORIZATION_HEADER)) .findFirst() .map(Cookie::getValue) .orElse(null);
    			
    			// HttpHeader 에 토큰 담음
    			if(!"LOGIN".equals(urlType)) {
    				conn.setRequestProperty(AUTHORIZATION_HEADER, jwt);
    			}
    			
    		}*/
            
            // type의 경우 POST, GET, PUT, DELETE 가능
            conn.setRequestMethod(type);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Transfer-Encoding", "chunked");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setDoOutput(true);
            
            OutputStream os = conn.getOutputStream();
            os.write(param.getBytes("UTF-8"));
            os.flush();
            
            // 보내고 결과값 받기
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
            	
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                responseJson = new JSONObject(sb.toString());
                
                // 응답 데이터
                System.out.println("responseJson :: " + responseJson);
                
                return responseJson;
            } 
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("not JSON Format response");
            e.printStackTrace();
        }
        
		return null;
    }
}
