# 📦 Timefit 테스트 데이터 가이드
> **이 문서는 Timefit 테스트 데이터 생성 및 관리를 위한 가이드입니다.**

---

## 구성 요소
1. seed-bulk.sql: 대규모 테스트 데이터 (10,000 예약, 성능 테스트용)
2. seed-minimal.sql: 최소 테스트 데이터
3. init-test-data.ps1 / .sh: 데이터 생성 스크립트
4. clear-test-data.ps1 / .sh: 데이터 삭제 스크립트
---

## 🚀 빠른 시작

### Windows (PowerShell)
```powershell
.\scripts\init-test-data.ps1
```

### macOS / Linux (Shell)
```bash
./scripts/init-test-data.sh
```

---

## 📊 테스트 데이터 규모

| 엔티티 | 개수 | 설명 |
|--------|------|------|
| User | 100명 | 사업자 3명 + 고객 97명 |
| Business | 3개 | Hair Salon, Nail Shop, Cafe |
| BusinessHours | 21개 | 3개 업체 × 7일 |
| OperatingHours | 31개 | 예약 가능 시간대 (브레이크타임 포함) |
| BusinessCategory | 6개 | 업체별 2개 카테고리 |
| Menu | 20개 | 예약형 15개 + 주문형 10개 |
| BookingSlot | ~3,000개 | 실제 예약 가능 슬롯 |
| Reservation | 10,000개 | 고객 예약 데이터 |

---

## 🏢 업체별 데이터 구조

### Business 1: Timefit Hair Salon (예약형)

**영업시간:**
- 평일: 09:00-18:00 (점심 12:00-13:00)
- 주말: 09:00-14:00

**주요 메뉴:**
- Basic Haircut (60분) - 15,000원
- Shampoo (30분) - 5,000원
- Treatment (60분) - 20,000원

**예약 슬롯:** 평일 오전 3-6개, 오후 5-10개 / 주말 5-10개

---

### Business 2: Timefit Nail Shop (예약형)

**영업시간:**
- 평일: 10:00-20:00 (브레이크 14:00-15:00)
- 주말: 10:00-18:00

**주요 메뉴:**
- Basic Nail (60분) - 20,000원
- Pedicure (60분) - 25,000원

**예약 슬롯:** 평일 오전 4개, 오후 5개 / 주말 8개

---

### Business 3: Timefit Cafe (주문형)

**영업시간:**
- 매일: 07:00-22:00

**메뉴:** 커피/음료 10개 (ONDEMAND_BASED)

**예약 슬롯:** 없음 (즉시 주문 방식)

---

## 🔑 주요 계정 정보

### 사업자 계정
```
Email: owner1@timefit.test
Password: password123
Business: Timefit Hair Salon

Email: owner2@timefit.test
Password: password123
Business: Timefit Nail Shop

Email: owner3@timefit.test
Password: password123
Business: Timefit Cafe
```

### 고객 계정
```
Email: customer1@timefit.test ~ customer97@timefit.test
Password: password123
```

---

## 🎯 API 테스트용 고정 ID

### Business IDs
```
Hair Salon: 30000000-0000-0000-0000-000000000001
Nail Shop:  30000000-0000-0000-0000-000000000002
Cafe:       30000000-0000-0000-0000-000000000003
```

### User IDs
```
Owner 1:    10000000-0000-0000-0000-000000000001
Owner 2:    10000000-0000-0000-0000-000000000002
Owner 3:    10000000-0000-0000-0000-000000000003

Customer 1: 20000000-0000-0000-0000-000000000001
Customer 2: 20000000-0000-0000-0000-000000000002
...
Customer 97: 20000000-0000-0000-0000-000000000097
```

### Menu IDs (예약형만)
```
Hair Salon:
  Basic Haircut: 60000000-0000-0000-0000-000000000001
  Shampoo:       60000000-0000-0000-0000-000000000009
  Treatment:     60000000-0000-0000-0000-000000000010

Nail Shop:
  Basic Nail:    60000000-0000-0000-0000-000000000011
  Pedicure:      60000000-0000-0000-0000-000000000015
```

---

## 📅 데이터 날짜 범위

```
BookingSlot: CURRENT_DATE ± 30일 (60일 범위)
Reservation: CURRENT_DATE ± 15일 (30일 범위)
```

**예시 (2024-12-19 기준):**
- BookingSlot: 2024-11-19 ~ 2025-01-18
- Reservation: 2024-12-04 ~ 2025-01-03

---

## 🆘 문제 해결

### "permission denied" 오류

**Windows:**
```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

**macOS / Linux:**
```bash
chmod +x scripts/*.sh
```

### Docker 컨테이너 이름 변경

```bash
# 현재 실행 중인 컨테이너 확인
docker ps

# Windows
$env:POSTGRES_CONTAINER="your-container-name"

# macOS/Linux
export POSTGRES_CONTAINER="your-container-name"
```

### 데이터 재생성

**Windows:**
```powershell
.\scripts\clear-test-data.ps1
.\scripts\init-test-data.ps1
```

**macOS / Linux:**
```bash
./scripts/clear-test-data.sh
./scripts/init-test-data.sh
```

---
