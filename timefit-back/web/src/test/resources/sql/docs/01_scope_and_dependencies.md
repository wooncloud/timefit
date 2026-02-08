# EXPLAIN 테스트 대상 파악

## 1. 스코프

| 구분 | 대상 |
|------|------|
| **커버** | Auth, User, BusinessCategory, Menu, OperatingHours, BookingSlot, Reservation, Review, Wishlist |
| **제외** | Invitation, BusinessHours |

> **참고:** `BusinessHours`는 영업 공지 시간(UI 노출용)으로, EXPLAIN 측정 대상이 아닙니다.  
> 실제 예약 시간 제어는 `OperatingHours`가 담당하며, 이것만 측정합니다.

---

## 2. 의존성 그래프

EXPLAIN 테스트를 위한 데이터 준비 관점에서 본 의존성입니다.
각 기능을 실행하려면 **왼쪽 → 오른쪽 순서대로** 데이터가 존재해야 합니다.

```
[seed: User, Business]          ← API 없음, 루트 조건
        │
        ├─→ OperatingHours      ← Business만 필요 (독립)
        │
        ├─→ BusinessCategory    ← Business만 필요
        │       │
        │       └─→ Menu        ← BusinessCategory 필요
        │               │
        │               ├─→ BookingSlot   ← Menu 필요
        │               │       │
        │               │       └─→ Reservation  ← BookingSlot 필요
        │               │                   │
        │               │                   └─→ Review  ← Reservation COMPLETED 필요
        │               │
        │               └─→ Wishlist       ← Menu만 필요
        │
        └─→ Auth/User           ← 사용자 로그인 관련 (독립)
```

**핵심 체인:** `Business → BusinessCategory → Menu → BookingSlot → Reservation → Review`

이 체인이 가장 깊고, Review까지 테스트하려면 전체 체인이 완성되어야 합니다.

---

## 3. 도메인별 기능 목록

각 행은 EXPLAIN 테스트의 최소 단위입니다.
"사전조건"은 해당 기능을 실행하기 위해 **이미 존재해야 하는 데이터**를 의미합니다.

---

### Auth

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 회원가입 | POST /api/auth/signup | 없음 |
| 2 | 로그인 | POST /api/auth/signin | 회원가입된 User |
| 3 | OAuth 로그인 | POST /api/auth/oauth | 없음 |
| 4 | 토큰 갱신 | POST /api/auth/refresh | 유효한 refresh_token |
| 5 | 로그아웃 | POST /api/auth/logout | 유효한 access_token |

---

### User

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 현재 사용자 조회 | GET /api/user/me | 로그인된 User |
| 2 | 고객 프로필 조회 | GET /api/customer/profile | 로그인된 User |
| 3 | 고객 프로필 수정 | PUT /api/customer/profile | 로그인된 User |
| 4 | 비밀번호 변경 | PUT /api/customer/profile/password | 로그인된 User |

---

### OperatingHours (영업시간)

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 영업시간 조회 | GET /api/business/{id}/operating-hours | Business |
| 2 | 영업시간 설정 | PUT /api/business/{id}/operating-hours | Business |
| 3 | 요일별 휴무 토글 | PATCH /api/business/{id}/operating-hours/{day}/toggle | Business |
| 4 | 영업시간 리셋 | PATCH /api/business/{id}/operating-hours/reset | Business |

---

### BusinessCategory (카테고리)

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 카테고리 목록 조회 | GET /api/business/{id}/categories | Business |
| 2 | 카테고리 상세 조회 | GET /api/business/{id}/category/{cid} | Business + 해당 카테고리 |
| 3 | 카테고리 생성 | POST /api/business/{id}/category | Business |
| 4 | 카테고리 수정 | PATCH /api/business/{id}/category/{cid} | Business + 해당 카테고리 |
| 5 | 카테고리 삭제 | DELETE /api/business/{id}/category/{cid} | Business + 해당 카테고리 + **활성 Menu 없음** |

> **삭제 제약:** 카테고리에 활성 Menu가 있으면 삭제 불가. Menu를 먼저 비활성화해야 함.

---

### Menu

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 메뉴 목록 조회 | GET /api/business/{id}/menu | Business |
| 2 | 메뉴 상세 조회 | GET /api/business/{id}/menu/{mid} | Business + 해당 Menu |
| 3 | 메뉴 생성 | POST /api/business/{id}/menu | Business + BusinessCategory |
| 4 | 메뉴 수정 | PATCH /api/business/{id}/menu/{mid} | Business + 해당 Menu |
| 5 | 메뉴 활성 토글 | PATCH /api/business/{id}/menu/{mid}/toggle | Business + 해당 Menu |
| 6 | 메뉴 삭제 | DELETE /api/business/{id}/menu/{mid} | Business + 해당 Menu |

> **메뉴 생성의 부작용:** `autoGenerateSlots=true`이면 Menu 생성 시 **BookingSlot이 자동 생성됨**.  
> 즉, Menu 생성 1건이 BookingSlot 생성까지 포함.

---

### BookingSlot (예약 슬롯)

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 슬롯 생성 | POST /api/business/{id}/booking-slot | Business + Menu (RESERVATION_BASED) |
| 2 | 날짜별 슬롯 조회 | GET /api/business/{id}/booking-slot?date={} | Business |
| 3 | 기간별 슬롯 조회 | GET /api/business/{id}/booking-slot/range | Business |
| 4 | 메뉴별 슬롯 조회 | GET /api/business/{id}/booking-slot/menu/{mid} | Business + Menu |
| 5 | 향후 슬롯 조회 | GET /api/business/{id}/booking-slot/upcoming | Business |
| 6 | 슬롯 삭제 | DELETE /api/business/{id}/booking-slot/{sid} | Business + 해당 BookingSlot + **예약 없음** |
| 7 | 슬롯 비활성화 | PATCH /api/business/{id}/booking-slot/{sid}/deactivate | Business + 해당 BookingSlot |
| 8 | 슬롯 재활성화 | PATCH /api/business/{id}/booking-slot/{sid}/activate | Business + 해당 BookingSlot (비활성 상태) |
| 9 | 과거 슬롯 일괄 삭제 | DELETE /api/business/{id}/booking-slot/past | Business |

> **슬롯 삭제 제약:** 예약이 존재하는 슬롯은 삭제 불가. Reservation이 있는 경우 해당 슬롯은 건너뛰어야 함.

---

### Reservation (예약)

**고객 측:**

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 예약 생성 | POST /api/reservation | User + 활성 BookingSlot (잔여 용량 > 0) |
| 2 | 내 예약 목록 | GET /api/reservations | 로그인된 User |
| 3 | 예약 상세 조회 | GET /api/reservation/{rid} | 로그인된 User + 본인 예약 |
| 4 | 예약 수정 | PUT /api/reservation/{rid} | 로그인된 User + 본인 예약 (수정 가능 상태) |
| 5 | 예약 취소 | POST /api/reservation/{rid}/cancel | 로그인된 User + 본인 예약 (취소 가능 상태) |

**업체 측:**

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 6 | 업체 예약 목록 | GET /api/business/{id}/reservations | Business (소속 User) |
| 7 | 업체 예약 상세 | GET /api/business/{id}/reservation/{rid} | Business + 해당 예약 |
| 8 | 예약 승인 | POST /api/business/{id}/reservation/{rid}/approve | Business + PENDING 상태 예약 |
| 9 | 예약 거절 | POST /api/business/{id}/reservation/{rid}/reject | Business + PENDING 상태 예약 |
| 10 | 예약 완료 | POST /api/business/{id}/reservation/{rid}/complete | Business + CONFIRMED 상태 예약 |
| 11 | 노쇼 처리 | POST /api/business/{id}/reservation/{rid}/no-show | Business + CONFIRMED 상태 예약 |

> **상태 전이:** PENDING → CONFIRMED (승인) → COMPLETED (완료) / NO_SHOW  
> Review 작성은 **COMPLETED** 상태 이후에만 가능.

---

### Review (리뷰)

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 리뷰 작성 | POST /api/customer/review | COMPLETED 상태의 Reservation |
| 2 | 내 리뷰 목록 | GET /api/customer/review/my | 로그인된 User |
| 3 | 리뷰 수정 | PUT /api/customer/review/{rid} | 본인 리뷰 (삭제되지 않은 상태) |
| 4 | 리뷰 삭제 | DELETE /api/customer/review/{rid} | 본인 리뷰 (삭제되지 않은 상태) |
| 5 | 업체별 리뷰 목록 | GET /api/public/business/{id}/reviews | Business |
| 6 | 업체 리뷰 통계 | GET /api/public/business/{id}/reviews/statistics | Business |

> **리뷰 작성 제약:** Reservation의 `reservation_id`를 참조. COMPLETED가 아닌 예약에는 리뷰 불가.  
> 리뷰 삭제는 소프트 삭제 (deleted_at 갱신).

---

### Wishlist (찜목록)

| # | 기능명 | API | 사전조건 |
|---|--------|-----|----------|
| 1 | 찜 목록 조회 | GET /api/customer/wishlist | 로그인된 User |
| 2 | 찜 추가 | POST /api/customer/wishlist | 로그인된 User + Menu |
| 3 | 찜 삭제 | DELETE /api/customer/wishlist/{menuId} | 로그인된 User + 본인 찜 항목 |
| 4 | 찜 여부 확인 | GET /api/customer/wishlist/check/{menuId} | 로그인된 User |

---

## 4. EXPLAIN 테스트 우선순위 제안

의존성 체인 깊이와 쿼리 복잡도를 고려한 순서:

| 단계 | 도메인 | 근거 |
|------|--------|------|
| 1 | BusinessCategory | 의존성 얕음, CRUD 패턴 표준 |
| 2 | Menu | BusinessCategory 완료 후 가능, 부작용(BookingSlot 생성) 포함 |
| 3 | BookingSlot | Menu 완료 후 가능, 조회 패턴 다양 |
| 4 | Reservation | 체인 깊음, 상태 전이 복잡 |
| 5 | Review | 체인 끝지점, 통계 쿼리 포함 |
| 6 | Wishlist | Menu만 필요하여 단계 2 이후 가능, 독립 테스트 가능 |
| 7 | OperatingHours | 독립이나 쿼리 복잡도 낮음 |
| 8 | Auth / User | 쿼리 복잡도 낮음, EXPLAIN 학습용보다는 기능 확인용 |
