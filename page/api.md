# API 문서

TimeFit REST API 명세와 사용법을 설명합니다.

::: info 📝 개발 진행 상황
이 문서는 개발과 함께 지속적으로 업데이트됩니다.
향후 Swagger/OpenAPI로 자동 생성될 예정입니다.
:::

## 🌐 기본 정보

```
Base URL: https://api.timefit.example.com/v1
인증: Bearer Token (JWT)
응답 형식: JSON
```

## 🔐 인증 API

### 소셜 로그인
OAuth 2.0 기반 소셜 로그인을 지원합니다.

```http
POST /auth/oauth/{provider}
Content-Type: application/json

{
  "code": "oauth_authorization_code",
  "redirect_uri": "https://timefit.example.com/callback"
}
```

**지원 프로바이더**
- `google` - Google OAuth
- `kakao` - 카카오 로그인  
- `apple` - Apple Sign In

**응답**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "홍길동",
    "role": "CUSTOMER"
  }
}
```

### 토큰 갱신
```http
POST /auth/refresh
Authorization: Bearer {refresh_token}
```

## 👤 사용자 API

### 프로필 조회
```http
GET /users/profile
Authorization: Bearer {access_token}
```

### 프로필 수정
```http
PUT /users/profile
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "홍길동",
  "phone": "010-1234-5678"
}
```

## 🏢 사업자 API

### 사업체 정보 조회
```http
GET /businesses/me
Authorization: Bearer {access_token}
```

### 사업체 등록
```http
POST /businesses
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "name": "홍길동 헤어샵",
  "category": "BEAUTY",
  "address": "서울시 강남구...",
  "phone": "02-1234-5678",
  "business_hours": {
    "monday": {"open": "09:00", "close": "18:00"},
    "tuesday": {"open": "09:00", "close": "18:00"}
  }
}
```

### 예약 가능 시간 설정
```http
POST /businesses/{businessId}/availability
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "date": "2024-01-15",
  "slots": [
    {"start_time": "09:00", "end_time": "10:00", "available": true},
    {"start_time": "10:00", "end_time": "11:00", "available": false}
  ]
}
```

## 📅 예약 API

### 예약 생성
```http
POST /reservations
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "business_id": "business_uuid",
  "datetime": "2024-01-15T14:00:00Z",
  "service_type": "컷+펌",
  "notes": "앞머리 짧게 해주세요"
}
```

### 예약 목록 조회
```http
GET /reservations?status=CONFIRMED&page=0&size=10
Authorization: Bearer {access_token}
```

**쿼리 파라미터**
- `status`: PENDING, CONFIRMED, CANCELLED, COMPLETED
- `page`: 페이지 번호 (0부터 시작)
- `size`: 페이지 크기 (기본값: 10)

### 예약 상세 조회
```http
GET /reservations/{reservationId}
Authorization: Bearer {access_token}
```

### 예약 수정
```http
PUT /reservations/{reservationId}
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "datetime": "2024-01-16T14:00:00Z",
  "notes": "시간 변경 요청"
}
```

### 예약 취소
```http
DELETE /reservations/{reservationId}
Authorization: Bearer {access_token}
```

## 💬 채팅 API

### 채팅방 목록
```http
GET /chats
Authorization: Bearer {access_token}
```

### 채팅방 생성
```http
POST /chats
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "business_id": "business_uuid",
  "reservation_id": "reservation_uuid"  // 선택적
}
```

### 메시지 전송
```http
POST /chats/{chatId}/messages
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "content": "안녕하세요, 예약 관련 문의드립니다.",
  "type": "TEXT"
}
```

### WebSocket 연결
실시간 채팅을 위한 WebSocket 엔드포인트입니다.

```javascript
const ws = new WebSocket('ws://api.timefit.example.com/ws/chat/{chatId}');

// 연결 시 인증
ws.onopen = () => {
  ws.send(JSON.stringify({
    type: 'auth',
    token: 'your_jwt_token'
  }));
};

// 메시지 수신
ws.onmessage = (event) => {
  const message = JSON.parse(event.data);
  console.log('새 메시지:', message);
};
```

## 📊 응답 형식

### 성공 응답
```json
{
  "data": { /* 실제 데이터 */ },
  "message": "성공",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 에러 응답
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "입력값이 올바르지 않습니다.",
    "details": {
      "field": "email",
      "reason": "이메일 형식이 올바르지 않습니다."
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 공통 에러 코드
- `UNAUTHORIZED` (401): 인증 실패
- `FORBIDDEN` (403): 권한 없음
- `NOT_FOUND` (404): 리소스 없음
- `VALIDATION_ERROR` (400): 입력값 오류
- `INTERNAL_ERROR` (500): 서버 내부 오류

## 🛠 개발 가이드

### 날짜/시간 형식
모든 날짜와 시간은 **ISO 8601 형식**을 사용합니다.
```
2024-01-15T14:30:00Z (UTC)
2024-01-15T23:30:00+09:00 (KST)
```

### 페이지네이션
목록 API는 다음 형식으로 페이지네이션을 지원합니다.
```json
{
  "data": [/* 목록 아이템들 */],
  "pagination": {
    "page": 0,
    "size": 10,
    "total_elements": 50,
    "total_pages": 5
  }
}
```

### Rate Limiting
API 사용량 제한이 있습니다.
- **일반 사용자**: 시간당 1000회
- **사업자**: 시간당 5000회

::: tip 💡 개발 팁
- JWT 토큰은 HTTP-only 쿠키로 저장하는 것을 권장합니다.
- 모든 API 호출에는 적절한 에러 처리를 구현해주세요.
- WebSocket 연결은 네트워크 끊김에 대비한 재연결 로직이 필요합니다.
::: 