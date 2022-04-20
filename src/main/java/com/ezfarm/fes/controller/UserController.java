package com.ezfarm.fes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ezfarm.fes.service.UserService;
import com.ezfarm.fes.util.SHA256;
import com.ezfarm.fes.vo.AnswerVo;
import com.ezfarm.fes.vo.QuestionVo;
import com.ezfarm.fes.vo.UserVo;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SHA256 sha256;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
	
	@RequestMapping("/selectUserById")
	public ResponseEntity<?> selectUserById(HttpServletRequest request, Model model) throws Exception {
		
		HttpSession httpSession = request.getSession(true);
		String userId = (String) httpSession.getAttribute("USER_ID");
		
		UserVo userVo = new UserVo();
		userVo = userService.selectUserById(userId);
		
		Map<String, Object> res = new HashMap<>();
		res.put("user", userVo);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@RequestMapping("/updateUser")
	public ResponseEntity<?> updateUser(HttpServletRequest request, Model model, @RequestBody Map<String, Object> param) throws Exception {
		
		HttpSession httpSession = request.getSession(true);
		String userId = (String) httpSession.getAttribute("USER_ID");
		String password = (String) param.get("password");
		String newPassword = (String) param.get("newPassword");
		String name = (String) param.get("name");
		String phone = (String) param.get("phone");
		
		String encPassword = sha256.encrypt(password);
		String encNewPassword = sha256.encrypt(newPassword);
		
		UserVo userVo = new UserVo();
		userVo = userService.selectUserById(userId);
		
		// 비밀번호 틀리면 500 error 던짐
		if(!encPassword.equals(userVo.getPassword())) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		UserVo userVo2 = new UserVo();
		userVo2.setUserId(userId);
		if(newPassword != "") {
			userVo2.setPassword(encNewPassword);
		}
		userVo2.setName(name);
		userVo2.setPhone(phone);
		
		userService.updateUser(userVo2);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	
}
