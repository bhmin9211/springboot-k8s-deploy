# 인증 API (헥사고날 아키텍처)

## 개요

이 프로젝트는 인증(Authentication) 기능을 Java로 구현한 것으로, 헥사고날 아키텍처 패턴을 적용하여 설계되었습니다.

## 아키텍처 구조

### 헥사고날 아키텍처 구성요소

```
┌─────────────────────────────────────────────────────────────┐
│                    인바운드 어댑터 (Adapter In)              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              AuthController                         │   │
│  │              (REST API 엔드포인트)                   │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    포트 (Ports)                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                AuthUseCase                          │   │
│  │              (인바운드 포트)                         │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    핵심 비즈니스 로직 (Core)                │
│  ┌─────────────────────────────────────────────────────┐   │
│  │                AuthService                          │   │
│  │              (인증 비즈니스 로직)                    │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    포트 (Ports)                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │UserRepository   │  │TokenProvider    │  │Password     │ │
│  │     Port        │  │     Port        │  │Encoder      │ │
│  └─────────────────┘  └─────────────────┘  │Port         │ │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   아웃바운드 어댑터 (Adapter Out)            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │UserRepository   │  │TokenProvider    │  │Password     │ │
│  │   Adapter       │  │   Adapter       │  │Encoder      │ │
│  └─────────────────┘  └─────────────────┘  │Adapter      │ │
└─────────────────────────────────────────────────────────────┘
```

## 주요 특징

### 1. Builder 패턴 적용
- 모든 모델 클래스에 `@Builder` 어노테이션 적용
- 객체 생성 시 가독성과 유연성 향상
- 예시:
```java
LoginRequest request = LoginRequest.builder()
    .username("user123")
    .password("password123")
    .build();
```

### 2. 헥사고날 아키텍처 적용
- **인바운드 포트**: `AuthUseCase`
- **아웃바운드 포트**: `UserRepositoryPort`, `TokenProviderPort`, `PasswordEncoderPort`
- **핵심 서비스**: `AuthService`
- **어댑터**: `AuthController`, `UserRepositoryAdapter`, `TokenProviderAdapter`, `PasswordEncoderAdapter`

### 3. 의존성 역전 원칙
- 핵심 비즈니스 로직이 외부 시스템에 의존하지 않음
- 포트를 통한 인터페이스 정의
- 테스트 용이성 및 유지보수성 향상

## API 엔드포인트

### 인증 관련 API
- `POST /auth/login` - 사용자 로그인
- `POST /auth/register` - 사용자 회원가입
- `POST /auth/validate` - 토큰 유효성 검증
- `GET /auth/me` - 현재 사용자 정보 조회
- `GET /auth/health` - 인증 서비스 헬스체크

## 모델 클래스

### LoginRequest
```java
@Builder
public class LoginRequest {
    private String username;
    private String password;
}
```

### RegisterRequest
```java
@Builder
public class RegisterRequest {
    private String username;
    private String password;
}
```

### LoginResponse
```java
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private String message;
}
```

### RegisterResponse
```java
@Builder
public class RegisterResponse {
    private String username;
    private String message;
    private boolean success;
}
```

## 사용 예시

### 1. 사용자 로그인
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user123",
    "password": "password123"
  }'
```

**성공 응답:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "user123",
  "role": "USER",
  "message": "로그인 성공"
}
```

**실패 응답:**
```json
{
  "message": "비밀번호가 일치하지 않습니다."
}
```

### 2. 사용자 회원가입
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "newpassword123"
  }'
```

**성공 응답:**
```json
{
  "username": "newuser",
  "message": "회원가입이 완료되었습니다."
}
```

**실패 응답:**
```json
{
  "message": "이미 존재하는 사용자명입니다."
}
```

### 3. 토큰 유효성 검증
```bash
curl -X POST http://localhost:8080/auth/validate \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**성공 응답:**
```json
{
  "valid": true,
  "username": "user123",
  "message": "토큰이 유효합니다."
}
```

**실패 응답:**
```json
{
  "valid": false,
  "message": "토큰이 유효하지 않습니다."
}
```

### 4. 현재 사용자 정보 조회
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**성공 응답:**
```json
{
  "username": "user123",
  "message": "사용자 정보 조회 성공"
}
```

## 비즈니스 로직

### 로그인 프로세스
1. 사용자명으로 사용자 조회
2. 비밀번호 일치 여부 확인
3. JWT 토큰 생성
4. 로그인 응답 반환

### 회원가입 프로세스
1. 사용자명 중복 확인
2. 비밀번호 암호화
3. 새 사용자 엔티티 생성
4. 사용자 저장
5. 회원가입 응답 반환

### 토큰 검증 프로세스
1. JWT 토큰 파싱
2. 토큰 유효성 검증
3. 사용자명 추출
4. 검증 결과 반환

## 장점

1. **유지보수성**: 명확한 책임 분리로 코드 유지보수 용이
2. **테스트 용이성**: 포트를 통한 인터페이스 분리로 단위 테스트 가능
3. **확장성**: 새로운 어댑터 추가 시 기존 코드 변경 없이 확장
4. **가독성**: Builder 패턴과 한글 로깅으로 코드 이해도 향상
5. **보안성**: JWT 토큰 기반 인증으로 안전한 사용자 인증
6. **의존성 역전**: 핵심 비즈니스 로직이 외부 시스템에 의존하지 않음

## 의존성

- Spring Boot 3.1.5
- Spring Security
- JWT (JSON Web Token)
- JPA (Java Persistence API)
- MariaDB Driver
- Lombok

## 보안 고려사항

1. **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 암호화
2. **JWT 토큰**: 서명된 JWT 토큰으로 안전한 인증
3. **토큰 만료**: 1시간 후 자동 만료되는 토큰
4. **입력 검증**: 사용자 입력에 대한 적절한 검증
5. **에러 처리**: 보안 정보 노출을 방지하는 안전한 에러 처리 