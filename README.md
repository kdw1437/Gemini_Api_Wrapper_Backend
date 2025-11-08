# 🚀 Gemini Chat Backend API

Spring Boot 기반의 AI 채팅 애플리케이션 백엔드 서버입니다.

## 📋 프로젝트 개요

Gemini AI를 활용한 대화형 채팅 서비스의 백엔드 API 서버입니다.
사용자 인증, 대화 관리, 메시지 저장 등의 기능을 제공하며,
Python 마이크로서비스와 연동하여 AI 응답을 생성합니다.

## 🛠 기술 스택

### 핵심 기술

- **Java 21**
- **Spring Boot 3.4.11**
- **Spring Security** - 인증 및 보안
- **MyBatis** - 데이터베이스 매핑
- **MySQL 8.0** - 데이터베이스

### 주요 의존성

- Spring Boot Starter Web
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Mail
- MyBatis Spring Boot Starter 3.0.3
- MySQL Connector
- Lombok
- JWT (io.jsonwebtoken)
- SpringDoc OpenAPI (Swagger)

## ✨ 주요 기능

### 1. 사용자 인증 시스템

- ✅ 회원가입 (이메일 중복 검증)
- ✅ 로그인 (비밀번호 암호화 BCrypt)
- ✅ 비밀번호 찾기 (이메일 인증)
- ✅ 비밀번호 재설정 (토큰 기반)

### 2. 대화 관리

- ✅ 새 대화 생성
- ✅ 대화 목록 조회 (최신순 정렬)
- ✅ 대화 삭제 (CASCADE)
- ✅ 대화 제목 자동 생성

### 3. 메시지 처리

- ✅ 메시지 전송 및 저장
- ✅ 대화별 메시지 조회
- ✅ AI 응답 생성 (Python 마이크로서비스 연동)
- ✅ 메시지 히스토리 관리

### 4. 보안

- ✅ CORS 설정
- ✅ CSRF 보호
- ✅ 비밀번호 암호화
- ✅ 토큰 기반 인증 준비

## 📁 프로젝트 구조

```
backend/
├── src/main/java/com/example/demo/
│   ├── config/                      # 설정 파일
│   │   ├── GeminiProperties.java    # Python 서비스 설정
│   │   ├── JwtProperties.java       # JWT 설정
│   │   ├── SecurityConfig.java      # Spring Security 설정
│   │   ├── RestTemplateConfig.java  # HTTP 클라이언트 설정
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── controller/                  # REST API 컨트롤러
│   │   ├── AuthController.java      # 인증 API
│   │   └── ChatController.java      # 채팅 API
│   │
│   ├── dto/                         # 데이터 전송 객체
│   │   ├── request/
│   │   │   ├── RegisterRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── ForgotPasswordRequest.java
│   │   │   ├── ResetPasswordRequest.java
│   │   │   ├── ChatRequest.java
│   │   │   └── GeminiRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── MessageResponse.java
│   │       ├── ChatMessageResponse.java
│   │       ├── ConversationResponse.java
│   │       └── GeminiResponse.java
│   │
│   ├── mapper/                      # MyBatis 매퍼 인터페이스
│   │   ├── UserMapper.java
│   │   ├── PasswordResetTokenMapper.java
│   │   ├── ConversationMapper.java
│   │   └── MessageMapper.java
│   │
│   ├── model/                       # 도메인 모델
│   │   ├── User.java
│   │   ├── PasswordResetToken.java
│   │   ├── Conversation.java
│   │   └── Message.java
│   │
│   ├── service/                     # 비즈니스 로직
│   │   ├── AuthService.java         # 인증 서비스
│   │   ├── EmailService.java        # 이메일 서비스
│   │   ├── ChatService.java         # 채팅 서비스
│   │   └── GeminiService.java       # AI 서비스 연동
│   │
│   └── DemoApplication.java         # 메인 애플리케이션
│
├── src/main/resources/
│   ├── mapper/                      # MyBatis XML 매퍼
│   │   ├── UserMapper.xml
│   │   ├── PasswordResetTokenMapper.xml
│   │   ├── ConversationMapper.xml
│   │   └── MessageMapper.xml
│   │
│   └── application.yml              # 애플리케이션 설정
│
├── build.gradle                     # Gradle 빌드 설정
└── README.md
```

---

**마지막 업데이트:** 2025-11-08
