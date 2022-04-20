package com.ezfarm.fes.config;

import com.ezfarm.fes.jwt.JwtAccessDeniedHandler;
import com.ezfarm.fes.jwt.JwtAuthenticationEntryPoint;
import com.ezfarm.fes.jwt.JwtSecurityConfig;
import com.ezfarm.fes.jwt.TokenProvider;
import com.ezfarm.fes.service.CustomUserDetailsService;
import com.ezfarm.fes.util.SHA256;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	// @EnableGlobalMethodSecurity : @PreAuthorize 검증 어노테이션을 메소드 단위로 사용하기 위해 추가
	
    private final TokenProvider tokenProvider;
    //private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;	// 권한 없을 때 처리하기 위한 핸들러
    //private final JwtAccessDeniedHandler jwtAccessDeniedHandler;	// 권한 없을 때 처리하기 위한 핸들러
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
 
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        //this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        //this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
 
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SHA256 sha256() {
        return new SHA256();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
 
    @Override
    public void configure(WebSecurity web) {
    	// 정적 resouces 경로는 security 적용 안함 (/resources/static/**)
    	//web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    	web.ignoring().antMatchers("/css/**", "/js/**");
    	web.ignoring().antMatchers("/img/**", "/fonts/**", "/pdf/**");
    	web.ignoring().antMatchers("/favicon.ico");
    	web.ignoring().antMatchers("/error");
    }
 
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()	// 토큰방식을 사용하므로 csrf는 disable
 
                .exceptionHandling()
                //.authenticationEntryPoint(jwtAuthenticationEntryPoint)	// 권한 없을 때 처리하기 위한 핸들러
                //.accessDeniedHandler(jwtAccessDeniedHandler)	// 권한 없을 때 처리하기 위한 핸들러
                
                // session을 사용하지 않을때 설정을 STATELESS로 함
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                
                .and()
                .authorizeRequests()
                //.antMatchers("/").permitAll()
                
                // 정적 resouces 경로는 security 적용 안함 (/resources/static/**)
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/fonts/**").permitAll()
                .antMatchers("/pdf/**").permitAll()
                
                //.antMatchers("/login/loginForm").permitAll()	// 로그인 페이지
                //.antMatchers("/login/loginProc").permitAll()	// 토큰을 받기위한 login api
                .antMatchers("/fes/**").permitAll()	// 토큰을 받기위한 login api
                .antMatchers("/login/**").permitAll()	// 토큰을 받기위한 login api
                .antMatchers("/login/logoutProc").permitAll()	// logout
                .antMatchers("/user/joinForm").permitAll()		// 회원가입을 위한 api
                
                .antMatchers("/qna/**").permitAll()	// 질의응답 controller
                
                .anyRequest().authenticated()
                
                .and()
                .formLogin()
                .loginPage("/login/loginForm")	// 최초 로그인 페이지로 redirect
                //.loginProcessingUrl("/login/loginProc")
                //.defaultSuccessUrl("/")
                .permitAll()
 
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://localhost:8020");
        configuration.addAllowedOrigin("http://mrc.ezfarm.biz/");
//        configuration.addAllowedOrigin("chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop");
//        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod(HttpMethod.PUT);
        configuration.addAllowedMethod(HttpMethod.GET);
        configuration.addAllowedMethod(HttpMethod.POST);
        configuration.addAllowedMethod(HttpMethod.OPTIONS);
        configuration.addAllowedMethod(HttpMethod.DELETE);
//        configuration.setAllowCredentials(true);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
 
}