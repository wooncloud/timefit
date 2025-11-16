# API 클라이언트 사용 가이드

## 개요

이 API 클라이언트는 백엔드 API 호출 시 **Access Token 자동 관리** 및 **Refresh Token 갱신**을 처리합니다.

## 주요 기능

- ✅ Access Token 자동 첨부
- ✅ Access Token 만료 시 Refresh Token으로 자동 갱신
- ✅ Refresh Token 만료 시 자동 로그아웃
- ✅ 동시 요청 시 토큰 갱신 중복 방지
- ✅ 클라이언트/서버 컴포넌트 모두 지원

## 파일 구조

```
src/lib/api/
├── auth-middleware.ts    # 통합 export (권장 사용)
├── client.ts            # 클라이언트 측 API 클라이언트
├── server-client.ts     # 서버 측 API 클라이언트
└── README.md           # 이 파일
```

## 사용 방법

### 1. 클라이언트 컴포넌트 (Client Components)

브라우저에서 실행되는 컴포넌트에서 사용:

```typescript
'use client';

import { apiFetch, apiFetchJson } from '@/lib/api/auth-middleware';

export default function UserList() {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    async function fetchUsers() {
      try {
        // JSON 응답을 자동으로 파싱
        const data = await apiFetchJson('/api/users', {
          method: 'GET',
        });
        setUsers(data);
      } catch (error) {
        console.error('Failed to fetch users:', error);
      }
    }
    fetchUsers();
  }, []);

  return <div>{/* 사용자 목록 렌더링 */}</div>;
}
```

### 2. 서버 컴포넌트 (Server Components)

서버에서 실행되는 컴포넌트에서 사용:

```typescript
import { serverApiFetchJson } from '@/lib/api/auth-middleware';

export default async function UsersPage() {
  try {
    const users = await serverApiFetchJson('/api/users');

    return (
      <div>
        {users.map((user) => (
          <div key={user.id}>{user.name}</div>
        ))}
      </div>
    );
  } catch (error) {
    return <div>Failed to load users</div>;
  }
}
```

### 3. POST 요청 예시

```typescript
import { apiFetchJson } from '@/lib/api/auth-middleware';

async function createUser(userData) {
  try {
    const newUser = await apiFetchJson('/api/users', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
    console.log('User created:', newUser);
  } catch (error) {
    console.error('Failed to create user:', error);
  }
}
```

### 4. 인증이 필요 없는 요청

```typescript
import { apiFetchJson } from '@/lib/api/auth-middleware';

// 공개 API 호출
const publicData = await apiFetchJson('/api/public/info', {
  requiresAuth: false, // 인증 불필요
});
```

## API 함수

### `apiFetch(endpoint, options)`

클라이언트 측에서 사용하는 fetch wrapper입니다.

**Parameters:**
- `endpoint` (string): API 엔드포인트 (예: '/api/users')
- `options` (object): fetch 옵션
  - `requiresAuth` (boolean): 인증 필요 여부 (기본값: true)
  - 기타 fetch API 옵션들

**Returns:** `Promise<Response>`

### `apiFetchJson<T>(endpoint, options)`

JSON 응답을 자동으로 파싱하는 fetch wrapper입니다.

**Returns:** `Promise<T>`

### `serverApiFetch(endpoint, options)`

서버 컴포넌트에서 사용하는 fetch wrapper입니다.

**Parameters:** `apiFetch`와 동일

**Returns:** `Promise<Response>`

### `serverApiFetchJson<T>(endpoint, options)`

서버 측 JSON 응답 파싱 wrapper입니다.

**Returns:** `Promise<T>`

## 토큰 갱신 흐름

1. API 요청 시 Access Token이 자동으로 포함됩니다
2. 서버에서 401 Unauthorized 응답을 받으면:
   - Refresh Token으로 새로운 Access Token 발급 요청
   - 성공 시: 새 토큰으로 원래 요청 재시도
   - 실패 시: 자동 로그아웃 및 로그인 페이지로 리다이렉트

## 동시 요청 처리

여러 API 요청이 동시에 401 에러를 받더라도, 토큰 갱신은 한 번만 수행됩니다:

```typescript
// 여러 요청이 동시에 실행되어도 안전
const [users, products, orders] = await Promise.all([
  apiFetchJson('/api/users'),
  apiFetchJson('/api/products'),
  apiFetchJson('/api/orders'),
]);
```

## 주의사항

1. **서버 컴포넌트에서는 토큰 갱신이 자동으로 이루어지지 않습니다**
   - 서버 컴포넌트에서 401 에러 발생 시, 클라이언트 측에서 페이지를 새로고침하거나 재로그인해야 합니다

2. **환경 변수 설정 필요**
   ```env
   NEXT_PUBLIC_BACKEND_URL=http://localhost:8080
   IRON_SESSION_PASSWORD=your-secret-password-min-32-chars
   ```

3. **CORS 설정**
   - 백엔드에서 프론트엔드 도메인을 CORS에 추가해야 합니다

## 에러 처리

```typescript
try {
  const data = await apiFetchJson('/api/users');
} catch (error) {
  if (error.message.includes('Authentication failed')) {
    // 인증 실패 (자동으로 로그아웃 처리됨)
  } else {
    // 기타 에러
    console.error('API Error:', error);
  }
}
```

## 로그아웃

수동으로 로그아웃하려면:

```typescript
import { logout } from '@/lib/api/auth-middleware';

async function handleLogout() {
  await logout(); // 세션 종료 및 로그인 페이지로 리다이렉트
}
```
