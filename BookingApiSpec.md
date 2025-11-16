# Booking Slot API 명세서

이 문서는 `BookingSlotController`가 제공하는 예약 슬롯 관련 API 엔드포인트를 설명합니다.

**기본 경로**: `/api/business/{businessId}/booking-slots`

## 인증 및 인가

- **OWNER/MANAGER**: 비즈니스 소유자 또는 관리자. 슬롯 생성, 수정, 삭제 등 모든 관리 기능을 수행할 수 있습니다.
- **MEMBER**: 비즈니스 구성원. 슬롯 조회 권한을 가집니다.
- **Public**: 인증되지 않은 사용자. 공개된 슬롯 정보를 조회할 수 있습니다.

---

## 슬롯 조회 API (주로 Public)

### 1. 특정 날짜의 슬롯 조회

- **설명**: 지정된 날짜에 예약 가능한 모든 슬롯을 조회합니다.
- **엔드포인트**: `GET /`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **쿼리 파라미터**:
  - `date` (LocalDate): 조회할 날짜 (형식: `YYYY-MM-DD`).
- **응답**: `ResponseData<SlotListResponse>`

```json
{
  "totalCount": "Integer",
  "slots": [
    {
      "slotId": "UUID",
      "startTime": "LocalDateTime",
      "endTime": "LocalDateTime",
      "capacity": "Integer",
      "reservedCount": "Integer",
      "status": "SlotStatus (e.g., AVAILABLE, FULL, DEACTIVATED)",
      "menuId": "UUID",
      "menuName": "String"
    }
  ]
}
```

### 2. 기간별 슬롯 조회

- **설명**: 지정된 시작일과 종료일 사이의 모든 예약 슬롯을 조회합니다.
- **엔드포인트**: `GET /range`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **쿼리 파라미터**:
  - `startDate` (LocalDate): 조회 시작 날짜 (형식: `YYYY-MM-DD`).
  - `endDate` (LocalDate): 조회 종료 날짜 (형식: `YYYY-MM-DD`).
- **응답**: `ResponseData<SlotListResponse>` (응답 형식은 **1. 특정 날짜의 슬롯 조회**와 동일)

### 3. 특정 메뉴의 슬롯 조회

- **설명**: 특정 메뉴에 대해 지정된 기간 동안 예약 가능한 슬롯을 조회합니다.
- **엔드포인트**: `GET /menu/{menuId}`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `menuId` (UUID): 조회할 메뉴의 고유 식별자.
- **쿼리 파라미터**:
  - `startDate` (LocalDate): 조회 시작 날짜 (형식: `YYYY-MM-DD`).
  - `endDate` (LocalDate): 조회 종료 날짜 (형식: `YYYY-MM-DD`).
- **응답**: `ResponseData<SlotListResponse>` (응답 형식은 **1. 특정 날짜의 슬롯 조회**와 동일)

### 4. 오늘 이후 활성 슬롯 조회

- **설명**: 오늘을 포함하여 미래의 모든 활성 예약 슬롯을 조회합니다.
- **엔드포인트**: `GET /upcoming`
- **권한**: Public
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **응답**: `ResponseData<SlotListResponse>` (응답 형식은 **1. 특정 날짜의 슬롯 조회**와 동일)

---

## 슬롯 관리 API (OWNER/MANAGER)

### 5. 예약 슬롯 생성

- **설명**: 운영 시간을 바탕으로 새로운 예약 슬롯을 생성합니다.
- **엔드포인트**: `POST /`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **요청 본문**: `CreateSlotsRequest`

| 필드명      | 타입                | 제약 조건              | 설명                                   |
|--------------|---------------------|------------------------|----------------------------------------|
| `menuId`     | UUID                | NotNull                | 슬롯을 생성할 메뉴의 ID                |
| `slotTimes`  | List<LocalDateTime> | NotEmpty               | 생성할 슬롯의 시작 시간 목록           |
| `capacity`   | Integer             | NotNull, 1 이상        | 각 슬롯의 최대 예약 가능 인원          |

- **응답**: `ResponseData<SlotCreationResult>`

```json
{
  "createdCount": "Integer",
  "failedCount": "Integer",
  "createdSlotIds": ["List<UUID>"],
  "failures": [
    {
      "slotTime": "LocalDateTime",
      "reason": "String"
    }
  ]
}
```

### 6. 예약 슬롯 삭제

- **설명**: 특정 예약 슬롯을 영구적으로 삭제합니다.
- **엔드포인트**: `DELETE /{slotId}`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `slotId` (UUID): 삭제할 슬롯의 고유 식별자.
- **응답**: `ResponseData<Void>`
- **성공 상태 코드**: `200 OK`

### 7. 슬롯 비활성화

- **설명**: 특정 슬롯을 예약 불가능하도록 비활성화합니다.
- **엔드포인트**: `PATCH /{slotId}/deactivate`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `slotId` (UUID): 비활성화할 슬롯의 고유 식별자.
- **응답**: `ResponseData<SlotDetail>`

```json
{
  "slotId": "UUID",
  "startTime": "LocalDateTime",
  "endTime": "LocalDateTime",
  "capacity": "Integer",
  "reservedCount": "Integer",
  "status": "SlotStatus (DEACTIVATED)",
  "menuId": "UUID",
  "menuName": "String"
}
```

### 8. 슬롯 재활성화

- **설명**: 비활성화된 슬롯을 다시 예약 가능하도록 활성화합니다.
- **엔드포인트**: `PATCH /{slotId}/activate`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
  - `slotId` (UUID): 활성화할 슬롯의 고유 식별자.
- **응답**: `ResponseData<SlotDetail>` (응답 형식은 **7. 슬롯 비활성화**와 동일하며, `status`는 `AVAILABLE` 등으로 변경)

### 9. 과거 슬롯 일괄 삭제

- **설명**: 현재 시간 이전의 모든 예약 슬롯을 정리(삭제)합니다.
- **엔드포인트**: `DELETE /past`
- **권한**: OWNER, MANAGER
- **경로 변수**:
  - `businessId` (UUID): 비즈니스의 고유 식별자.
- **응답**: `ResponseData<Integer>` (삭제된 슬롯의 개수)

```json
{
  "data": 120
}
```
