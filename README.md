## 📋 당신을 위한 요리 레시피
- ❓ 사용자 질병별 요리 레시피 추천 웹서비스
- 📆 24/09/23 ~ 24/09/24 기획, 24/09/25 ~ 24/09/30 개발

---

### 📎 개발환경 & 적용기술
- IDE : IntelliJ
- OS : Windows
- 개발 언어 : Java
- DBMS : MariaDB
- BackEnd: Spring Boot, Spring Security, JWT, JPA, MVC
- 배포: AWS EC2, AWS S3
- 협업 : Notion, Google Drive, Github Projects

---

### ⭐ 기능 소개
1. 회원가입 및 로그인
   - 로그인, 회원가입
   - 회원 탈퇴
2. 회원
   - 회원 정보 수정 및 조회
   - 비밀번호 변경
3. 챗봇 내용 스크랩 기능
   - 스크랩 및 스크랩 삭제
   - 스크랩 단일 조회 및 스크랩 전체 목록 조회

---

### 💡 주요 기능
1. CORS 세팅
   - `SecurityFilterChain`에 CORS 세팅 추가
      ```java
      http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
         CorsConfiguration configuration = new CorsConfiguration();
         configuration.setAllowedOrigins(List.of("*"));
         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
         configuration.setAllowedHeaders(List.of("*"));
         configuration.setExposedHeaders(List.of("token", "id"));
         return configuration;
      }));
      ```
      외부로부터 오는 모든 `GET`, `POST`, `DELETE`, `PATCH` 요청을 받아들이고, Response Header 값으로 `token`, `id` 값을 받을 수 있도록 합니다.

2. JWT 토큰 관련
   1. Custom Annotation
      ```java
      @Target(ElementType.PARAMETER)
      @Retention(RetentionPolicy.RUNTIME)
      public @interface AuthUser {
      }
      ```
      `AuthUser` 라는 커스텀 어노테이션을 생성하여 사용자를 식별합니다.
   
   2. `AuthenticationSuccessHandler`를 상속 받은 `CustomAuthenticationSuccessHandler` class 작성
      ```java
      @Override
      public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
         UserDetails userDetails = (UserDetails) authentication.getPrincipal();
         UsersEntity userEntity = userRepository.findByUserId(userDetails.getUsername());

         String jwtToken = jwtUtils.createAccessToken(userEntity);

         // 응답 헤더에 생성한 토큰을 설정
         response.setHeader("token", jwtToken); // JWT 값
         response.setHeader("id", userEntity.getId().toString()); // Users: ID 값(PK값)
      }
      ```
      로그인 성공 시, Response Header에 "token" 값, "id" 값을 추가합니다.
   3. `SimpleUrlAuthenticationFailureHandler`를 상속 받은 `CustomAuthenticationFailureHandler` class 작성
      ```java
      @Override
      public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
         response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 인증 실패
         response.getWriter().write("아이디 혹은 비밀번호가 올바르지 않습니다.");
      }
      ```
      로그인 실패 시, Response Status `401 UNAUTHORIZED`를 반환합니다.
   4. `UserDetails`를 상속 받은 `AuthDetails`, `HandlerMethodArgumentResolver`를 상속 받은 `AuthUserResolver` class 작성
      ```java
      @Override // JwtFilter에서 모두 검증하므로, 검증 로직은 추가하지 않음
      public UsersEntity resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
         HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
         String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
         if(authorizationHeader == null)
            return null;
         
         // "Bearer {token}"에서 {token}값만 추출
         String jwtToken = authorizationHeader.substring(7);
         // {token}값으로부터 UserEntity 조회
         UsersEntity user = jwtUtils.getUser(jwtToken);
         
         return user;
      }
      ```
      JWT 토큰이 필요한 모든 요청에서, Request Header로부터 JWT 토큰을 받아 `UsersEntity` 객체를 반환합니다.