# 시스템 아키텍처

TimeFit의 전체 시스템 구조와 주요 컴포넌트에 대한 개요입니다.

## 전체 아키텍처

```
Client (SvelteKit PWA)
    ↓ HTTP/WebSocket
API Gateway / Load Balancer
    ↓
Backend Services (Spring Boot)
    ↓
Database (PostgreSQL/Supabase)
```

## 주요 컴포넌트

### Frontend Layer
- **SvelteKit PWA**: 메인 클라이언트 애플리케이션
- **PWA Features**: 오프라인 지원, 푸시 알림 (향후)
- **Responsive Design**: 모바일 우선 설계

### Backend Layer
- **Spring Boot API**: RESTful API 서버
- **Authentication**: OAuth 기반 인증 시스템
- **Real-time**: WebSocket 채팅 서버

### Data Layer
- **PostgreSQL**: 메인 데이터베이스
- **Supabase**: Backend-as-a-Service
  - 실시간 데이터베이스
  - 인증 서비스
  - 파일 스토리지

## 데이터 모델

### 주요 엔티티
- **User**: 사용자 (고객/사업자)
- **Business**: 사업체 정보
- **Reservation**: 예약 정보
- **Chat**: 채팅 메시지
- **Menu**: 서비스/메뉴 옵션

## API 설계 원칙

### RESTful 설계
- 리소스 기반 URL 구조
- HTTP 메서드 적절한 사용
- 명확한 응답 코드

### 실시간 통신
- WebSocket을 통한 채팅
- Server-Sent Events로 실시간 업데이트

## 보안 고려사항

### 인증/인가
- JWT 기반 토큰 인증
- OAuth 2.0 소셜 로그인
- 역할 기반 접근 제어 (RBAC)

### 데이터 보호
- 개인정보 암호화
- SQL Injection 방지
- XSS/CSRF 보호

## 배포 전략

### 개발 환경
- 로컬 개발 서버
- 핫 리로드 지원

### 프로덕션 환경
- PWA 빌드 최적화
- CDN 활용
- 환경별 설정 분리

## 모니터링 (향후)

- 애플리케이션 로그
- 성능 메트릭
- 에러 트래킹 