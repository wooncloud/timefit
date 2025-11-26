# TimeFit Frontend

예약 관리 시스템의 프론트엔드 애플리케이션입니다.

## Tech Stack

### Core

- **[Next.js 15](https://nextjs.org/)** - React 프레임워크 (App Router, Turbopack)
- **[React 19](https://react.dev/)** - UI 라이브러리
- **[TypeScript](https://www.typescriptlang.org/)** - 타입 안정성

### Styling

- **[Tailwind CSS](https://tailwindcss.com/)** - 유틸리티 기반 CSS 프레임워크
- **[shadcn/ui](https://ui.shadcn.com/)** - 재사용 가능한 컴포넌트 시스템
- **[Lucide React](https://lucide.dev/)** - 아이콘 라이브러리
- **[Class Variance Authority](https://cva.style/)** - 컴포넌트 variant 관리

### State & Data

- **[Iron Session](https://github.com/vvo/iron-session)** - 세션 관리
- **[Day.js](https://day.js.org/)** - 날짜/시간 처리
- **[FullCalendar](https://fullcalendar.io/)** - 캘린더 UI

### Development Tools

- **ESLint** - 코드 품질 관리
- **Prettier** - 코드 포맷팅

## Getting Started

### Prerequisites

- Node.js 20.x 이상
- npm 또는 yarn

### Installation

```bash
# 의존성 설치
npm install

# 개발 서버 실행 (Turbopack)
npm run dev

# 프로덕션 빌드
npm run build

# 프로덕션 서버 실행
npm start
```

개발 서버: [http://localhost:3000](http://localhost:3000)

### Available Scripts

```bash
npm run dev          # 개발 서버 시작 (Turbopack)
npm run build        # 프로덕션 빌드
npm start            # 프로덕션 서버 시작
npm run lint         # ESLint 실행
npm run prettier     # Prettier로 코드 포맷팅
npm run format       # Prettier + ESLint 자동 수정
```

## Project Structure

```
src/
├── app/                      # Next.js App Router
│   ├── (auth)/              # 인증 관련 페이지
│   ├── (business)/          # 사업자 페이지
│   ├── (home)/              # 메인 페이지
│   ├── api/                 # API Routes
│   │   ├── auth/            # 인증 API
│   │   └── business/        # 사업자 API
│   └── m/                   # 모바일 전용 페이지
├── components/              # React 컴포넌트
│   ├── ui/                  # shadcn/ui 컴포넌트
│   ├── business/            # 사업자 기능 컴포넌트
│   └── layout/              # 레이아웃 컴포넌트
├── hooks/                   # Custom React Hooks
│   ├── auth/                # 인증 관련 hooks
│   └── business/            # 사업자 관련 hooks
├── services/                # API 서비스 레이어
│   ├── auth/                # 인증 API 서비스
│   └── business/            # 사업자 API 서비스
├── lib/                     # 유틸리티 및 헬퍼
│   ├── validators/          # 폼 유효성 검증
│   ├── formatters/          # 데이터 포맷팅
│   ├── session/             # 세션 관리
│   └── utils.ts             # 공통 유틸리티
└── types/                   # TypeScript 타입 정의
    ├── auth/                # 인증 관련 타입
    └── business/            # 사업자 관련 타입
```

## Development Conventions

### 1. 디렉토리 구조 규칙

#### 레이어별 역할

**`/app`** - Next.js 페이지 및 라우팅

- UI 렌더링만 담당
- 비즈니스 로직은 hooks로 분리
- API Routes는 `/app/api`에 위치

**`/components`** - 재사용 가능한 React 컴포넌트

- `/components/ui` - shadcn/ui 컴포넌트 (자동 생성)
- `/components/business` - 도메인별 컴포넌트
- `/components/layout` - 레이아웃 컴포넌트

**`/hooks`** - Custom React Hooks

- 상태 관리 및 비즈니스 로직
- UI와 로직 분리
- 재사용 가능한 로직 캡슐화

**`/services`** - API 통신 레이어

- 클라이언트 사이드 API 호출
- fetch 로직 캡슐화
- 에러 핸들링

**`/lib`** - 유틸리티 함수

- `/lib/validators` - 폼 유효성 검증
- `/lib/formatters` - 데이터 포맷팅
- `/lib/utils.ts` - 공통 유틸리티

**`/types`** - TypeScript 타입 정의

- 도메인별로 디렉토리 구분
- API 요청/응답 타입
- 폼 데이터 타입

### 2. 코드 작성 규칙

#### 컴포넌트 패턴

```typescript
// ✅ Good: Hook으로 로직 분리
'use client';

import { useSignup } from '@/hooks/auth/useSignup';

export default function SignupPage() {
  const { formData, errors, isLoading, handleSubmit } = useSignup();

  return (
    <form onSubmit={handleSubmit}>
      {/* UI only */}
    </form>
  );
}
```

```typescript
// ❌ Bad: 페이지 컴포넌트에 로직 혼재
export default function SignupPage() {
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});

  const handleSubmit = async (e) => {
    // 많은 비즈니스 로직...
  };

  return <form>...</form>;
}
```

#### Service 레이어 패턴

```typescript
// services/auth/authService.ts
class AuthService {
  private apiUrl = '/api/auth';

  async signup(data: SignupRequestBody): Promise<SignupHandlerResponse> {
    const response = await fetch(`${this.apiUrl}/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    const result = await response.json();
    if (!response.ok) throw new Error(result.message);
    return result;
  }
}

export const authService = new AuthService();
```

#### Hook 패턴

```typescript
// hooks/auth/useSignup.ts
'use client';

import { useState } from 'react';

import { authService } from '@/services/auth/authService';
import { validateSignupForm } from '@/lib/validators/authValidators';

export function useSignup(options = {}) {
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async e => {
    e.preventDefault();

    const { isValid, errors } = validateSignupForm(formData);
    if (!isValid) return setErrors(errors);

    setIsLoading(true);
    try {
      await authService.signup(formData);
      options.onSuccess?.();
    } catch (error) {
      options.onError?.(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  return { formData, errors, isLoading, handleSubmit };
}
```

### 3. 네이밍 규칙

**파일명**

- 컴포넌트: `kebab-case.tsx` (예: `signup-form.tsx`)
- Hook: `use-*.ts` (예: `use-signup.ts`)
- Service: `*Service.ts` (예: `authService.ts`)
- Validator: `*Validators.ts` (예: `authValidators.ts`)

**컴포넌트명**

- PascalCase (예: `SignupForm`, `BusinessCard`)

**함수명**

- camelCase (예: `handleSubmit`, `validateForm`)
- Hook: `use*` prefix (예: `useSignup`, `useBusinessData`)

**상수명**

- UPPER_SNAKE_CASE (예: `API_BASE_URL`)

### 4. Import 순서

```typescript
// 1. React 및 Next.js
import { useState } from 'react';
import { useRouter } from 'next/navigation';
// 2. 외부 라이브러리
import { CalendarClock } from 'lucide-react';

// 6. Types
import type { SignupFormData } from '@/types/auth/signup';
// 5. Services
import { authService } from '@/services/auth/authService';
// 4. Hooks
import { useSignup } from '@/hooks/auth/useSignup';
// 7. Utils
import { cn } from '@/lib/utils';
// 3. 내부 컴포넌트
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
```

### 5. TypeScript 규칙

**타입 정의**

```typescript
// ✅ Good: 명확한 타입 정의
interface SignupFormData {
  email: string;
  password: string;
  name: string;
}

type SignupFormErrors = Partial<Record<keyof SignupFormData, string>>;
```

**타입 import**

```typescript
// ✅ Good: type import 사용
import type { User } from '@/types/user';
// ❌ Bad
import { User } from '@/types/user';
```

### 6. shadcn/ui 사용 규칙

**컴포넌트 추가**

```bash
npx shadcn add button
npx shadcn add input
npx shadcn add dialog
```

**컴포넌트 커스터마이징**

- `components/ui/` 내에서 직접 수정
- CSS 변수를 활용한 테마 커스터마이징 (`globals.css`)

**스타일링**

```typescript
// ✅ Good: cn() 유틸리티 사용
import { cn } from '@/lib/utils';

<Button className={cn('w-full', isLoading && 'opacity-50')} />
```

### 7. API Routes 규칙

**파일 위치**

- `/app/api/[domain]/route.ts`

**응답 형식**

```typescript
// ✅ Good: 일관된 응답 구조
interface SuccessResponse {
  success: true;
  message: string;
  data: T;
}

interface ErrorResponse {
  success: false;
  message: string;
}
```

**에러 핸들링**

```typescript
try {
  const data = await backendAPI();
  return NextResponse.json({ success: true, data });
} catch (error) {
  return NextResponse.json(
    { success: false, message: error.message },
    { status: 500 }
  );
}
```

### 8. 스타일링 규칙

**Tailwind CSS 사용**

```typescript
// ✅ Good: 유틸리티 클래스 사용
<div className="flex items-center justify-between p-4 rounded-lg">

// ❌ Bad: 인라인 스타일
<div style={{ display: 'flex', padding: '16px' }}>
```

**반응형 디자인**

```typescript
// 모바일 우선 (mobile-first)
<div className="w-full md:w-1/2 lg:w-1/3">
```

### 9. 환경 변수

**파일 위치**

- `.env.local` (로컬 개발용, git ignore)
- `.env.production` (프로덕션용)

**네이밍**

```bash
# 클라이언트에서 접근 가능
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_BACKEND_URL=http://localhost:8080

# 서버에서만 접근 가능
SESSION_SECRET=your-secret-key
```

### 10. 코드 품질

**Linting & Formatting**

```bash
# 코드 작성 전
npm run format

# 커밋 전
npm run lint
npm run prettier:check
```

**TypeScript 검사**

```bash
npx tsc --noEmit
```

## Best Practices

### 1. 컴포넌트 분리

- 하나의 컴포넌트는 하나의 책임만
- 200줄 이상이면 분리 고려
- UI와 로직 분리 (Custom Hook 활용)

### 2. 성능 최적화

- 이미지: Next.js Image 컴포넌트 사용
- 코드 스플리팅: dynamic import 활용
- 메모이제이션: React.memo, useMemo, useCallback 적절히 사용

### 3. 접근성

- semantic HTML 사용
- ARIA 속성 적절히 사용
- 키보드 네비게이션 지원

### 4. 보안

- XSS 방지: 사용자 입력 검증
- CSRF 방지: API Routes에서 검증
- 민감 정보: 환경 변수 사용

## Troubleshooting

### 자주 발생하는 문제

**1. Hydration 오류**

```typescript
// ✅ 해결: useEffect 사용
useEffect(() => {
  setMounted(true);
}, []);

if (!mounted) return null;
```

**2. 환경 변수가 undefined**

- `NEXT_PUBLIC_` prefix 확인
- 개발 서버 재시작

**3. Type 에러**

```bash
# node_modules/@types 재설치
rm -rf node_modules
npm install
```
