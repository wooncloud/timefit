# Menu API 명세서

이 문서는 `MenuController`가 제공하는 메뉴 관련 API 엔드포인트를 설명합니다.

**기본 경로**: `/api/business/{businessId}/menu`

## 인증 및 인가

- **OWNER/MANAGER**: 비즈니스 소유자 또는 관리자. 메뉴 생성, 수정, 삭제 등 모든 관리 권한을 가집니다.
- **Public**: 인증되지 않은 사용자. 공개된 메뉴 정보를 조회할 수 있습니다.

---

## 공개 API (인증 불필요)

### 1. 메뉴 목록 조회 (검색 및 필터링)

- **설명**: 특정 비즈니스의 메뉴 목록을 검색 조건과 함께 조회합니다.
- **엔드포인트**: `GET /`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **쿼리 파라미터 (선택 사항)**:
  - `serviceName` (String): 검색할 서비스명 (부분 일치).
  - `businessCategoryId` (UUID): 필터링할 카테고리 ID.
  - `minPrice` (Integer): 최소 가격.
  - `maxPrice` (Integer): 최대 가격.
  - `isActive` (Boolean): 활성 상태인 메뉴만 조회할지 여부.
- **응답**: `ResponseData<MenuListResponse>`

```json
{
  "totalCount": "Integer",
  "menus": [
    {
      "menuId": "UUID",
      "serviceName": "String",
      "description": "String",
      "price": "Integer",
      "duration": "Integer",
      "businessCategoryId": "UUID",
      "categoryName": "String",
      "isActive": "Boolean",
      "createdAt": "LocalDateTime",
      "updatedAt": "LocalDateTime"
    }
  ]
}
```

### 2. 메뉴 상세 조회

- **설명**: 특정 메뉴의 상세 정보를 조회합니다.
- **엔드포인트**: `GET /{menuId}`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `menuId` (UUID): 조회할 메뉴의 고유 식별자.
- **응답**: `ResponseData<MenuResponse>`

```json
{
  "menuId": "UUID",
  "serviceName": "String",
  "description": "String",
  "price": "Integer",
  "duration": "Integer",
  "businessCategoryId": "UUID",
  "categoryName": "String",
  "isActive": "Boolean",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

---

## 메뉴 관리 API (OWNER/MANAGER)

### 3. 메뉴 생성

- **설명**: 새로운 메뉴를 생성합니다.
- **엔드포인트**: `POST /`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 메뉴를 생성할 비즈니스의 고유 식별자.
- **요청 본문**: `CreateMenuRequest`

| 필드명                | 타입    | 제약 조건         | 설명             |
|----------------------|---------|-------------------|------------------|
| `serviceName`        | String  | NotBlank, 2~100자 | 서비스명         |
| `description`        | String  | 최대 500자        | 서비스 설명      |
| `price`              | Integer | NotNull, 0 이상   | 가격             |
| `duration`           | Integer | NotNull, 1 이상   | 소요 시간(분)    |
| `businessCategoryId` | UUID    | NotNull           | 비즈니스 카테고리 ID |

- **응답**: `ResponseData<MenuResponse>` (응답 형식은 **2. 메뉴 상세 조회**와 동일)
- **성공 상태 코드**: `201 Created`

### 4. 메뉴 수정

- **설명**: 기존 메뉴의 정보를 수정합니다.
- **엔드포인트**: `PATCH /{menuId}`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `menuId` (UUID): 수정할 메뉴의 고유 식별자.
- **요청 본문**: `UpdateMenuRequest` (모든 필드는 Optional)

| 필드명                | 타입    | 제약 조건       | 설명             |
|----------------------|---------|-----------------|------------------|
| `serviceName`        | String  | 2~100자         | 서비스명         |
| `description`        | String  | 최대 500자      | 서비스 설명      |
| `price`              | Integer | 0 이상          | 가격             |
| `duration`           | Integer | 1 이상          | 소요 시간(분)    |
| `businessCategoryId` | UUID    | -               | 비즈니스 카테고리 ID |

- **응답**: `ResponseData<MenuResponse>` (응답 형식은 **2. 메뉴 상세 조회**와 동일)

### 5. 메뉴 활성/비활성 토글

- **설명**: 메뉴의 활성 상태를 토글합니다 (활성 ↔ 비활성).
- **엔드포인트**: `PATCH /{menuId}/toggle`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `menuId` (UUID): 상태를 변경할 메뉴의 고유 식별자.
- **응답**: `ResponseData<MenuResponse>` (상태가 변경된 메뉴 정보 반환)

### 6. 메뉴 삭제

- **설명**: 메뉴를 논리적으로 삭제합니다 (비활성화 처리).
- **엔드포인트**: `DELETE /{menuId}`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `menuId` (UUID): 삭제할 메뉴의 고유 식별자.
- **응답**: `Void`
- **성공 상태 코드**: `204 No Content`
