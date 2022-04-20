package com.ezfarm.fes.controller;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ezfarm.fes.jwt.JwtFilter;
import com.ezfarm.fes.jwt.TokenProvider;
import com.ezfarm.fes.service.AuthService;
import com.ezfarm.fes.service.UserService;
import com.ezfarm.fes.util.SHA256;
import com.ezfarm.fes.vo.TokenVo;
import com.ezfarm.fes.vo.UserVo;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;




@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Resource
    private Environment environment;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Autowired
	private UserService userService;
	
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
	
	Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	
	@GetMapping("/loginForm")
	public String getLoginForm(Model model) {
		
		return "login/loginForm";
	}
	
	@PostMapping("/loginProc")
	public ResponseEntity<?> loginProc(HttpServletRequest request, HttpServletResponse response, Model model, UserVo param) throws Exception {
		
		// user id
		String userId = param.getUserId();
		//SHA256으로 암호화된 비밀번호
        String password = sha256.encrypt(param.getPassword());
        
        UserVo user = userService.selectUserById(userId);
        
        if(!user.getPassword().equals(password)) {
        	return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        /*
        HashMap<String, Object> resultMap = new HashMap();
        resultMap.put("userId", apiId);
        resultMap.put("password", sha256.encrypt(apiPw));
        ObjectMapper mapper = new ObjectMapper();
        
        String apiParam = mapper.writeValueAsString(resultMap);
		
		
        JSONObject resultObj = new JSONObject();
		//JsonObject param = new JsonObject();
        
        // POST 방식으로 호출.(GET, POST, PUT, DELETE)
        resultObj = HttpUtil.callApi(null, apiParam, "LOGIN", "POST");
		
        // 토큰 발행
		String accessToken = resultObj.getString("token");
		accessToken = URLEncoder.encode(accessToken, "utf-8");
		
		// 토큰 DB 저장
		TokenVo token = new TokenVo();
		token.setUserId(userId);
		token.setRefreshToken(accessToken);
		
		authService.updateRefreshToken(token);
		*/
		
        TokenVo tokenVo = authService.selectRefreshToken(userId);
		String accessToken = tokenVo.getRefreshToken();
        
		// 세션 생성
        HttpSession httpSession = request.getSession(true);
        httpSession.setAttribute("USER_ID", userId);
        httpSession.setMaxInactiveInterval(60*60*24);	// 세션 설정시간 하루
		
		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		Map<String, Object> res = new HashMap<>();
		res.put("token", accessToken);
        
        // HttpHeader 에 토큰 담음
		HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
		
        return new ResponseEntity<>(res, httpHeaders, HttpStatus.OK);
	}
	
	@GetMapping("/logoutProc")
	public String logout(HttpServletRequest request) throws Exception {
		
		HttpSession httpSession = request.getSession(false);
		/*
		String userId = (String) httpSession.getAttribute("USER_ID");
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
		*/
		
		httpSession.removeAttribute("USER_ID");
		
		return "redirect:/login/loginForm";
	}
	
}
