package com.bugwarriors.chickstable.security;

import com.bugwarriors.chickstable.common.JwtUtils;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	@Autowired
	private UsersRepository userRepository;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		UsersEntity userEntity = userRepository.findByUserId(userDetails.getUsername());

		String jwtToken = jwtUtils.createAccessToken(userEntity);

		// 응답 헤더에 생성한 토큰을 설정
		response.setHeader("token", jwtToken); // JWT 값
		response.setHeader("id", userEntity.getId().toString()); // Users: ID 값(PK값)
	}
}