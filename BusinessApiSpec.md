# Business API 명세서

이 문서는 `BusinessController`가 제공하는 API 엔드포인트를 설명합니다.

**기본 경로**: `/api/business`

## 인증 및 인가

- **OWNER**: 비즈니스에 대한 모든 권한을 가집니다.
- **MANAGER**: 비즈니스 정보 조회/수정, 구성원 조회/초대 권한을 가집니다.
- **MEMBER**: 비즈니스 정보 조회만 가능합니다.
- **Public**: 인증이 필요하지 않습니다.

---

## 공개 API (인증 불필요)

### 1. 비즈니스 상세 정보 조회 (공개)

- **설명**: 특정 비즈니스의 공개용 상세 정보를 조회합니다.
- **엔드포인트**: `GET /{businessId}`
- **권한**: Public (누구나 조회 가능)
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **응답**: `ResponseData<PublicBusinessResponse>`

```json
{
  "businessId": "UUID",
  "businessName": "String",
  "businessTypes": ["Set<BusinessTypeCode>"],
  "ownerName": "String",
  "address": "String",
  "contactPhone": "String",
  "description": "String",
  "logoUrl": "String",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### 2. 비즈니스 검색 (페이징)

- **설명**: 선택적 필터링 및 페이지네이션을 사용하여 비즈니스를 검색합니다.
- **엔드포인트**: `GET /search`
- **권한**: Public (누구나 검색 가능)
- **쿼리 파라미터**:
  - `keyword` (String, optional): 비즈니스 이름이나 설명에서 검색할 키워드.
  - `businessType` (BusinessTypeCode, optional): 필터링할 비즈니스 유형.
  - `region` (String, optional): 필터링할 지역.
  - `page` (int, default: 0): 페이지네이션을 위한 페이지 번호.
  - `size` (int, default: 20): 페이지당 항목 수.
- **응답**: `ResponseData<BusinessListResponse>`

```json
{
  "businesses": [
    {
      "businessId": "UUID",
      "businessName": "String",
      "businessTypes": ["Set<BusinessTypeCode>"],
      "logoUrl": "String",
      "myRole": "BusinessRole",
      "joinedAt": "LocalDateTime",
      "isActive": "Boolean"
    }
  ],
  "totalCount": "Integer"
}
```

---

## 인증이 필요한 API

### 3. 내 비즈니스 목록 조회

- **설명**: 인증된 사용자가 속한 비즈니스 목록을 조회합니다.
- **엔드포인트**: `GET /my-businesses`
- **권한**: 인증된 사용자
- **응답**: `ResponseData<BusinessListResponse>` (응답 형식은 **2. 비즈니스 검색**과 동일)

### 4. 비즈니스 프로필 조회 (사업자용)

- **설명**: 비즈니스 소유자/관리자/구성원을 위해 민감한 데이터를 포함한 상세 비즈니스 정보를 조회합니다.
- **엔드포인트**: `GET /{businessId}/profile`
- **권한**: OWNER, MANAGER, MEMBER (해당 비즈니스 소속)
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **응답**: `ResponseData<BusinessResponse>`

```json
{
  "businessId": "UUID",
  "businessName": "String",
  "businessTypes": ["Set<BusinessTypeCode>"],
  "businessNumber": "String",
  "ownerName": "String",
  "address": "String",
  "contactPhone": "String",
  "description": "String",
  "logoUrl": "String",
  "businessNotice": "String",
  "isActive": "Boolean",
  "myRole": "BusinessRole",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

### 5. 비즈니스 생성

- **설명**: 새로운 비즈니스를 생성합니다. 생성자는 자동으로 OWNER가 됩니다.
- **엔드포인트**: `POST /`
- **권한**: 인증된 모든 사용자
- **요청 본문**: `CreateBusinessRequest`

| 필드명          | 타입                  | 제약 조건                    | 설명           |
|-----------------|-----------------------|------------------------------|----------------|
| `businessName`  | String                | NotBlank, 2~100자            | 업체명         |
| `businessTypes` | Set<BusinessTypeCode> | NotNull, 1~3개               | 업종           |
| `businessNumber`| String                | NotBlank, `\d{3}-\d{2}-\d{5}` 형식 | 사업자번호 |
| `ownerName`     | String                | 최대 50자                    | 대표자명       |
| `address`       | String                | NotBlank, 최대 200자         | 주소           |
| `contactPhone`  | String                | NotBlank, 최대 20자          | 연락처         |
| `description`   | String                | 최대 1000자                  | 설명           |
| `logoUrl`       | String                | -                            | 로고 이미지 URL|
| `businessNotice`| String                | 최대 500자                   | 공지사항       |

- **응답**: `ResponseData<BusinessResponse>` (응답 형식은 **4. 비즈니스 프로필 조회**와 동일)
- **성공 상태 코드**: `201 Created`

### 6. 비즈니스 정보 수정

- **설명**: 기존 비즈니스 정보를 수정합니다.
- **엔드포인트**: `PUT /{businessId}`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 수정할 비즈니스의 고유 식별자.
- **요청 본문**: `UpdateBusinessRequest` (모든 필드는 Optional)

| 필드명          | 타입                  | 제약 조건                    | 설명           |
|-----------------|-----------------------|------------------------------|----------------|
| `businessName`  | String                | 2~100자                      | 업체명         |
| `businessTypes` | Set<BusinessTypeCode> | 1~3개                        | 업종           |
| `businessNumber`| String                | `\d{3}-\d{2}-\d{5}` 형식     | 사업자번호     |
| `ownerName`     | String                | 최대 50자                    | 대표자명       |
| `address`       | String                | 최대 200자                   | 주소           |
| `contactPhone`  | String                | 최대 20자                    | 연락처         |
| `description`   | String                | 최대 1000자                  | 설명           |
| `logoUrl`       | String                | -                            | 로고 이미지 URL|
| `businessNotice`| String                | 최대 500자                   | 공지사항       |

- **응답**: `ResponseData<BusinessResponse>` (응답 형식은 **4. 비즈니스 프로필 조회**와 동일)

### 7. 비즈니스 삭제 (비활성화)

- **설명**: 비즈니스를 비활성화(소프트 삭제)합니다.
- **엔드포인트**: `DELETE /{businessId}`
- **권한**: OWNER
- **경로 변수**:
  - `businessId` (UUID): 삭제할 비즈니스의 고유 식별자.
- **요청 본문**: `DeleteBusinessRequest`

| 필드명        | 타입   | 제약 조건          | 설명     |
|---------------|--------|--------------------|----------|
| `deleteReason`| String | NotBlank, 최대 500자 | 삭제 사유|

- **응답**: `ResponseData<DeleteBusinessResponse>`

```json
{
  "businessId": "UUID",
  "businessName": "String",
  "deletedAt": "LocalDateTime",
  "deleteReason": "String"
}
```

---

## 구성원 관리 API

### 8. 비즈니스 구성원 목록 조회

- **설명**: 특정 비즈니스에 속한 구성원 목록을 조회합니다.
- **엔드포인트**: `GET /{businessId}/members`
- **권한**: OWNER, MANAGER, MEMBER (해당 비즈니스 소속)
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **응답**: `ResponseData<MemberListResponse>`

```json
{
  "businessId": "UUID",
  "businessName": "String",
  "members": [
    {
      "userId": "UUID",
      "email": "String",
      "name": "String",
      "role": "BusinessRole",
      "joinedAt": "LocalDateTime",
      "isActive": "Boolean",
      "invitedByName": "String",
      "lastLoginAt": "LocalDateTime"
    }
  ],
  "totalCount": "Integer"
}
```

### 9. 구성원 초대

- **설명**: 비즈니스에 새로운 구성원을 초대합니다. 신규 초대는 항상 MEMBER 역할로 생성됩니다.
- **엔드포인트**: `POST /{businessId}/member`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **요청 본문**: `InviteMemberRequest`

| 필드명             | 타입   | 제약 조건          | 설명                 |
|--------------------|--------|--------------------|----------------------|
| `email`            | String | NotBlank, Email 형식| 초대할 사용자의 이메일|
| `invitationMessage`| String | 최대 500자         | 초대 메시지          |

- **응답**: `ResponseData<MemberResponse>` (응답 형식은 **8. 구성원 목록 조회**의 `members` 배열 내 객체와 동일)
- **성공 상태 코드**: `201 Created`

### 10. 구성원 역할 변경

- **설명**: 비즈니스 내 기존 구성원의 역할을 변경합니다.
- **엔드포인트**: `PATCH /{businessId}/member/{userId}/role`
- **권한**: OWNER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `userId` (UUID): 역할을 변경할 구성원의 고유 식별자.
- **요청 본문**: `ChangeMemberRoleRequest`

| 필드명  | 타입         | 제약 조건 | 설명                              |
|---------|--------------|-----------|-----------------------------------|
| `newRole`| BusinessRole | NotNull   | 새로운 역할 (OWNER, MANAGER, MEMBER)|

- **응답**: `ResponseData<Void>`

### 11. 구성원 제거

- **설명**: 비즈니스에서 구성원을 제거합니다. OWNER는 모든 구성원을, MANAGER는 MEMBER만 제거할 수 있습니다.
- **엔드포인트**: `DELETE /{businessId}/member/{userId}`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `userId` (UUID): 제거할 구성원의 고유 식별자.
- **응답**: `ResponseData<Void>`
- **성공 상태 코드**: `204 No Content`

### 12. 구성원 활성화

- **설명**: 비활성화된 구성원을 비즈니스 내에서 활성화합니다.
- **엔드포인트**: `PATCH /{businessId}/member/{userId}/activate`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `userId` (UUID): 활성화할 구성원의 고유 식별자.
- **응답**: `ResponseData<Void>`

### 13. 구성원 비활성화

- **설명**: 비즈니스 내 구성원을 비활성화합니다.
- **엔드포인트**: `PATCH /{businessId}/member/{userId}/deactivate`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `userId` (UUID): 비활성화할 구성원의 고유 식별자.
- **응답**: `ResponseData<Void>`
