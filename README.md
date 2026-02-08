# Timefit

**비즈니스를 위한 일정, 예약 및 고객 관리 풀스택 예약 관리 시스템**

## 개요

Timefit은 Next.js 15와 Spring Boot 3.4.5로 구축된 현대적인 웹 애플리케이션으로, 비즈니스가 예약, 일정 및 고객 관계를 효율적으로 관리할 수 있도록 설계되었습니다. 데스크톱과 모바일 기기 모두에 최적화된 반응형 디자인을 제공합니다.

### 주요 기능

- 📅 **캘린더 관리**: 예약과 일정을 관리하는 인터랙티브 캘린더 인터페이스
- 👥 **팀 관리**: 역할 기반 접근 제어를 통한 팀원 조직 및 관리
- 🏢 **비즈니스 프로필**: 운영 시간 및 서비스 세부 정보가 포함된 커스터마이징 가능한 비즈니스 프로필
- 🔐 **보안 인증**: 암호화된 세션 관리를 통한 JWT 기반 인증
- 📱 **모바일 우선 디자인**: 전용 모바일 라우트가 있는 반응형 UI

## 기여자

이 프로젝트는 다음 사람들이 관리합니다:

- **wooncloud** - [@wooncloud](https://github.com/wooncloud)
- **sehan528** - [@sehan528](https://github.com/sehan528)

## 아키텍처

### 프론트엔드 ([timefit-front/](timefit-front/))
- **프레임워크**: Next.js 15, React 19, TypeScript
- **UI**: shadcn/ui 디자인 시스템 (Tailwind CSS)
- **상태 관리**:
  - 서버 사이드: iron-session (암호화된 쿠키 세션)
  - 클라이언트 사이드: Zustand (localStorage 지속성)
- **주요 기능**:
  - Turbopack을 사용한 빠른 개발을 위한 App Router
  - 모바일/데스크톱 라우트 분리
  - 디바이스 감지 미들웨어
  - 포괄적인 에러 핸들링

### 백엔드 ([timefit-back/](timefit-back/))
- **프레임워크**: Spring Boot 3.4.5, Java 21
- **아키텍처**: 멀티 모듈 Gradle 프로젝트
  - `web`: 컨트롤러, DTO, 서비스, 보안
  - `domain`: 비즈니스 엔티티, 리포지토리
  - `user`: 사용자 관리
  - `jpa-common`: 공유 JPA 설정
- **데이터베이스**: PostgreSQL (JPA/Hibernate, QueryDSL)
- **보안**: Spring Security를 통한 JWT 토큰
- **주요 기능**:
  - RESTful API 디자인
  - 포괄적인 예외 처리
  - 복잡한 쿼리를 위한 QueryDSL
  - 모니터링을 위한 Spring Actuator

## 기술 스택

### 프론트엔드
| 카테고리 | 기술 |
|----------|-----------|
| 프레임워크 | Next.js 15, React 19, TypeScript |
| UI 컴포넌트 | shadcn/ui, Radix UI primitives |
| 스타일링 | Tailwind CSS, CSS Variables |
| 상태 관리 | Zustand, iron-session |
| 아이콘 | Lucide React |
| 날짜 처리 | Day.js, React Day Picker |
| 캘린더 | FullCalendar |
| 알림 | Sonner |

### 백엔드
| 카테고리 | 기술 |
|----------|-----------|
| 프레임워크 | Spring Boot 3.4.5, Java 21 |
| 데이터베이스 | PostgreSQL |
| ORM | JPA/Hibernate, QueryDSL |
| 보안 | Spring Security, JWT |
| 빌드 도구 | Gradle (멀티 모듈) |
| 모니터링 | Spring Actuator |

## 프로젝트 구조

```
timefit/
├── timefit-front/              # Next.js 프론트엔드
│   ├── src/
│   │   ├── app/                # Next.js App Router
│   │   │   ├── (auth)/         # 인증 페이지
│   │   │   ├── (business)/     # 비즈니스 대시보드
│   │   │   ├── m/              # 모바일 라우트
│   │   │   └── api/            # API 라우트
│   │   ├── components/         # React 컴포넌트
│   │   │   └── ui/             # shadcn/ui 컴포넌트
│   │   ├── hooks/              # 커스텀 React 훅
│   │   ├── lib/                # 유틸리티 및 헬퍼
│   │   ├── stores/             # Zustand 스토어
│   │   └── types/              # TypeScript 타입
│   └── public/                 # 정적 자산
│
└── timefit-back/               # Spring Boot 백엔드
    ├── web/                    # 컨트롤러, DTO, 서비스, 보안
    ├── domain/                 # 비즈니스 엔티티, 리포지토리
    ├── user/                   # 사용자 관리
    └── jpa-common/             # 기본 엔티티, JPA 설정
```

## 주요 기능 및 패턴

### 인증 플로우

1. **로그인**: 클라이언트가 자격 증명 제출 → 백엔드 검증 → JWT 토큰 반환
2. **토큰 저장**: 암호화된 iron-session 쿠키에 토큰 저장 (서버 전용)
3. **인증된 요청**: `apiFetch()` 헬퍼가 토큰 추출 → 백엔드로 전달
4. **토큰 갱신**: 401 에러 시 자동 갱신 및 재시도
5. **로그아웃**: 세션 정리 → 홈페이지로 리다이렉트

### 상태 관리

**2계층 아키텍처**:
- **서버 계층 (iron-session)**: 사용자 프로필, 비즈니스 정보, JWT 토큰
- **클라이언트 계층 (Zustand)**: 사용자 프로필, 비즈니스 정보 (토큰 제외)

**동기화**: `BusinessLayoutProvider`가 서버 세션에서 클라이언트 스토어로 데이터 동기화

### API 디자인

**프론트엔드 API 라우트** (`/api/*`):
- `apiFetch()` 헬퍼로 자동 인증 처리 (토큰 추출, 헤더 주입, 401 갱신)
- 표준화된 응답 형식 반환
- `handleApiError()`로 에러 처리

**백엔드 REST API** (`/api/*`):
- 보호된 라우트에 JWT 인증 적용
- 공개 라우트: `/api/auth/**`, `/api/business/search/**`, `/api/validation/**`
- 도메인별 에러 코드를 사용한 예외 처리

### UI 컴포넌트

**디자인 시스템**: shadcn/ui ("new-york" 스타일)
- 일관된 로딩 상태 (`Loader2` 아이콘)
- 표준화된 에러 상태 (`AlertCircle` 아이콘)
- 토스트 알림 (`sonner`)
- 확인 대화상자 (`ConfirmDialog` 컴포넌트)


