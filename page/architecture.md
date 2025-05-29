# 시스템 아키텍처

TimeFit의 전체 시스템 구조와 주요 설계 결정사항을 설명합니다.

## 🏛 전체 구조

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  Client (PWA)   │────│   API Gateway    │────│  Spring Boot    │
│   SvelteKit     │    │  Load Balancer   │    │     Server      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
                                                         ▼
                                               ┌─────────────────┐
                                               │   PostgreSQL    │
                                               │   (Supabase)    │
                                               └─────────────────┘
```

## 🧩 주요 컴포넌트

### Frontend Layer
```
SvelteKit PWA
├── 반응형 UI 컴포넌트
├── 상태 관리 (Svelte stores)
├── 라우팅 및 네비게이션
├── PWA 매니페스트
└── 서비스 워커 (캐싱, 오프라인)
```

### Backend Layer
```
Spring Boot Application
├── REST API 컨트롤러
├── 비즈니스 로직 서비스
├── 데이터 접근 계층 (JPA)
├── WebSocket 채팅 서버
├── OAuth 인증 처리
└── 스케줄링 (예약 알림 등)
```

### Data Layer
```
Supabase (PostgreSQL)
├── 사용자/사업자 테이블
├── 예약 관리 테이블  
├── 채팅 메시지 저장
├── 실시간 구독 (Realtime)
├── 파일 스토리지 (이미지)
└── Row Level Security
```

## 📋 데이터 모델

### 핵심 엔티티
```sql
-- 사용자 (고객 + 사업자)
users
├── id (UUID)
├── email
├── name
├── role (CUSTOMER, BUSINESS)
└── oauth_provider

-- 사업체 정보
businesses  
├── id (UUID)
├── owner_id (users.id)
├── name
├── category
├── address
└── business_hours

-- 예약
reservations
├── id (UUID) 
├── customer_id (users.id)
├── business_id (businesses.id)
├── datetime
├── status (PENDING, CONFIRMED, CANCELLED)
└── notes

-- 채팅
chat_rooms
└── messages
```

## 🔄 주요 플로우

### 예약 생성 플로우
1. **고객**: 사업체 검색 및 선택
2. **시스템**: 예약 가능 시간 조회  
3. **고객**: 날짜/시간 선택
4. **시스템**: 예약 생성 및 사업자 알림
5. **사업자**: 예약 승인/거절
6. **시스템**: 고객에게 결과 알림

### 실시간 채팅 플로우
1. **연결**: WebSocket 연결 수립
2. **인증**: JWT 토큰 검증
3. **채팅방**: 기존 채팅방 조회 또는 신규 생성
4. **메시지**: 실시간 송수신
5. **상태**: 읽음 상태 업데이트

## 🛡 보안 설계

### 인증/인가
```
OAuth 2.0 Flow
├── 소셜 로그인 (Google, Kakao, Apple)
├── JWT 토큰 발급 (Access + Refresh)  
├── API 엔드포인트 보호
└── 역할 기반 접근 제어 (RBAC)
```

### 데이터 보호
- **개인정보 암호화**: 민감 정보 AES 암호화
- **SQL Injection 방지**: Prepared Statement 사용
- **XSS 방지**: 입력 값 검증 및 이스케이핑
- **CSRF 방지**: CSRF 토큰 및 SameSite 쿠키

## 🚀 배포 전략

### 개발 환경
```
Local Development
├── Spring Boot DevTools (Hot Reload)
├── Vite HMR (Frontend)
├── H2 Database (로컬 테스트)
└── Supabase Local (향후)
```

### 프로덕션 환경
```
Production Stack
├── Docker 컨테이너화
├── GitHub Actions CI/CD
├── Vercel/Netlify (Frontend)
├── Railway/Heroku (Backend)
└── Supabase (Database)
```

## 📊 성능 고려사항

### Frontend 최적화
- **코드 스플리팅**: 라우트별 번들 분리
- **이미지 최적화**: WebP 포맷, 지연 로딩
- **캐싱**: 서비스 워커로 정적 자원 캐싱

### Backend 최적화  
- **DB 인덱싱**: 자주 쿼리되는 컬럼 인덱스
- **커넥션 풀**: HikariCP 설정 최적화
- **응답 캐싱**: Redis 도입 고려 (향후)

## 🔍 모니터링 (향후)

### 로깅
- **구조화된 로그**: JSON 형태로 출력
- **중요 이벤트**: 회원가입, 로그인, 예약 변경
- **에러 추적**: Sentry 연동 고려

### 메트릭
- **애플리케이션**: 응답 시간, 처리량
- **인프라**: CPU, 메모리, 디스크 사용률
- **비즈니스**: 예약 전환율, 사용자 활동 