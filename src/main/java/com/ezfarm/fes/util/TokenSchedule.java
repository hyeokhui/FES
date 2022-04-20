package com.ezfarm.fes.util;

import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ezfarm.fes.service.AuthService;
import com.ezfarm.fes.service.UserService;
import com.ezfarm.fes.vo.TokenVo;
import com.ezfarm.fes.vo.UserVo;

@Component
public class TokenSchedule {
	
	@Autowired
	private UserService userService;
	
	//@Autowired
	//private LoginService loginService;
	
	@Autowired
	private AuthService authService;
	
	//@Autowired
	//private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SHA256 sha256;
	
	@Value("${aiad.api.id}")
	private String apiId;

	@Value("${aiad.api.pw}")
	private String apiPw;
	
	Logger logger = LoggerFactory.getLogger(TokenSchedule.class);
	
	@Scheduled(fixedDelay = 1000*60*60*24)
	//@Scheduled(cron = "*/50 * * * * *", zone = "Asia/Seoul")
	public void batchRefreshToken() throws Exception {
		
	    //System.out.println("The current date (1) : " + LocalDateTime.now());
		
		String userId = apiId;
		UserVo user = userService.selectUserById(userId);
		
		TokenVo tokenVo = authService.selectRefreshToken(userId);
		String token = tokenVo.getRefreshToken();
		
		HashMap<String, Object> resultMap = new HashMap();
        resultMap.put("userId", userId);
        resultMap.put("password", user.getPassword());
        ObjectMapper mapper = new ObjectMapper();
        
        String param = mapper.writeValueAsString(resultMap);
		
		JSONObject resultObj = new JSONObject();
        
        resultObj = HttpUtil.callApi(token, param, "LOGOUT", "POST");
        
        if(null == resultObj || null != resultObj.get("errorCode")) {
        	
        	System.out.println("responseJson :: " + resultObj);
        	
        	TokenVo updateTokenVo = new TokenVo();
            updateTokenVo.setUserId(userId);
            updateTokenVo.setRefreshToken("-");
            
            authService.updateRefreshToken(updateTokenVo);
            
            System.out.println("Current date : " + LocalDateTime.now());
    		System.out.println("Delete token : " + token + "\n");
        }
		
		// user id
		//String userId = apiId;
		//SHA256으로 암호화된 비밀번호
        //String password = sha256.encrypt(apiPw);
        
        HashMap<String, Object> resultMap2 = new HashMap();
        resultMap2.put("userId", apiId);
        resultMap2.put("password", sha256.encrypt(apiPw));
        ObjectMapper mapper2 = new ObjectMapper();
        
        String apiParam = mapper.writeValueAsString(resultMap2);
		
        JSONObject resultObj2 = new JSONObject();
        
        // POST 방식으로 호출.(GET, POST, PUT, DELETE)
        resultObj2 = HttpUtil.callApi(null, apiParam, "LOGIN", "POST");
		
        // 토큰 발행
		String accessToken = resultObj2.getString("token");
		accessToken = URLEncoder.encode(accessToken, "utf-8");
		
		TokenVo token2 = new TokenVo();
		token2.setUserId(userId);
		token2.setRefreshToken(accessToken);
		
		authService.updateRefreshToken(token2);
		
		System.out.println("Current date : " + LocalDateTime.now());
		System.out.println("Access token : " + accessToken + "\n");
	}
	
	
}
