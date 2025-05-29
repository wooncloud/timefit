# API 문서

TimeFit API 엔드포인트와 사용법에 대한 문서입니다.

::: info
이 문서는 개발 진행에 따라 지속적으로 업데이트됩니다.
:::

## API 기본 정보

- **Base URL**: `https://api.timefit.example.com/v1`
- **인증**: Bearer Token (JWT)
- **응답 형식**: JSON

## 인증 API

### 소셜 로그인
```http
POST /auth/oauth/{provider}
```

지원 프로바이더: `google`, `kakao`, `apple`

## 사용자 API

### 프로필 조회
```http
GET /users/profile
```

### 프로필 수정
```http
PUT /users/profile
```

## 사업자 API

### 사업체 정보 관리
```http
GET /businesses/me
POST /businesses
PUT /businesses/{id}
```

### 예약 시간 설정
```http
POST /businesses/{id}/availability
PUT /businesses/{id}/availability/{id}
```

## 예약 API

### 예약 생성
```http
POST /reservations
```

### 예약 조회
```http
GET /reservations
GET /reservations/{id}
```

### 예약 수정/취소
```http
PUT /reservations/{id}
DELETE /reservations/{id}
```

## 채팅 API

### 채팅방 목록
```http
GET /chats
```

### 메시지 전송
```http
POST /chats/{chatId}/messages
```

### WebSocket 연결
```
ws://api.timefit.example.com/ws/chat/{chatId}
```

## 에러 응답

모든 API는 다음 형식의 에러 응답을 반환합니다:

```json
{
  "error": {
    "code": "INVALID_REQUEST",
    "message": "요청이 올바르지 않습니다.",
    "details": {}
  }
}
```

## 개발 가이드

- 모든 날짜는 ISO 8601 형식 사용
- 페이지네이션은 `page`, `size` 파라미터 사용
- 필터링은 쿼리 파라미터로 지원

::: tip
API 명세서는 개발 진행에 따라 Swagger/OpenAPI 문서로 자동 생성될 예정입니다.
::: 