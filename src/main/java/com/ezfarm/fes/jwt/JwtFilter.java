package com.ezfarm.fes.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.ezfarm.fes.service.AuthService;
import com.ezfarm.fes.service.LoginService;
import com.ezfarm.fes.vo.TokenVo;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;


public class JwtFilter extends GenericFilterBean {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	public static final String AUTHORIZATION_HEADER = "Auth-Token";
	private TokenProvider tokenProvider;
	private AuthService authService;
	
	public JwtFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	// 토큰의 인증 정보를 SecurityContext에 저장하는 역할
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		
		if(null == authService){
            ServletContext servletContext = servletRequest.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            authService = webApplicationContext.getBean(AuthService.class);
        }
		
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String requestURI = httpServletRequest.getRequestURI();
		//String jwt = resolveToken(httpServletRequest);	// request header에서 토큰 정보를 꺼냄
		
		HttpSession httpSession = httpServletRequest.getSession(true);
		String userId = (String) httpSession.getAttribute("USER_ID");
		
		String jwt = "";
		
		// DB에서 refreshToken 가져옴
		if(!StringUtils.isEmpty(userId)) {
			TokenVo token = authService.selectRefreshToken(userId);
			if(null != token) {
				jwt = token.getRefreshToken();
			}
		}
		
		// 쿠키 방식을 쓸 경우
		/*String jwt = "";
		
		if(null != httpServletRequest.getCookies()) {
			jwt = Arrays.stream(httpServletRequest.getCookies()) .filter(c -> c.getName().equals(AUTHORIZATION_HEADER)) .findFirst() .map(Cookie::getValue) .orElse(null);
			jwt = resolveToken("Bearer " + jwt);
		}*/

		if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {	// 토큰 유효성 검사
			
			// 토큰에서 Authentication 객체를 받아와서 SecurityContext에 셋팅
			try {
				Authentication authentication;
				authentication = tokenProvider.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}
	
	// 쿠키 방식을 쓸 경우
	// String으로 넘긴 토큰 정보를 꺼냄
	/*private String resolveToken(String jwt) {
		
	    String bearerToken = jwt;
	      
	    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
	    	return bearerToken.substring(7);
	    }
	    
	    //if (StringUtils.hasText(bearerToken)) {
	    //	return bearerToken.substring(7);
	    //}
	      
	    return null;
	}*/

	// request header에서 토큰 정보를 꺼냄
	private String resolveToken(HttpServletRequest request) {
		
	    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
	      
	    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
	    	return bearerToken.substring(7);
	    }
	    
	    //if (StringUtils.hasText(bearerToken)) {
	    //	return bearerToken.substring(7);
	    //}
	      
	    return null;
	}
}
