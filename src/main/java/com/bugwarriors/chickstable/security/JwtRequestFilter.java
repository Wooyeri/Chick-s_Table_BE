package com.bugwarriors.chickstable.security;

import com.bugwarriors.chickstable.common.JwtUtils;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UsersRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// 헤더 검증 생략 API 목록
		String uri = request.getRequestURI();
		if (uri.equals("/join") || uri.equals("/login")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		if(CorsUtils.isPreFlightRequest(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String jwtToken = null;
		String subject = null;

		// Authorization 요청 헤더 존재 여부를 확인하고, 헤더 정보를 추출
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwtToken = authorizationHeader.substring(7); // "Bearer " 이후의 모든 내용
			subject = jwtUtils.getSubjectFromToken(jwtToken);
		} else {
			log.error("Authorization 헤더 누락 또는 토큰 형식 오류");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid JWT token");
			response.getWriter().flush();
			return;
		}
		
		UsersEntity userEntity = userRepository.findByUserId(subject);
		
		if(userEntity == null || !jwtUtils.validateToken(jwtToken, userEntity)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().write("Invalid JWT token");
			response.getWriter().flush();
			return;
		}
		
		Authentication authentication = jwtUtils.getAuthentication(jwtToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());

		filterChain.doFilter(request, response);
	}
}