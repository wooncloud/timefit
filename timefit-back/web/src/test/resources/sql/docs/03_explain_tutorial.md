# EXPLAIN 튜토리얼

참조: [PostgreSQL 공식 문서 — Using EXPLAIN](https://www.postgresql.org/docs/current/using-explain.html)

이 튜토리얼은 공식 문서와 동일한 구조로 작성되었습니다.
예제 테이블은 `business_category`로 통일하여, 조건을 점점 좁혀가며
Planner의 선택이 왜 달라지는지를 원리적으로 보여줍니다.

> 출력의 숫자는 테이블 크기와 통계에 따라 달라집니다.
> 여기의 숫자는 illustrative 용도입니다. 실제 실행 시 값은 다를 수 있습니다.

---

## 1. SQL이 실행되는 과정

```
SQL 문자열
  ↓  Parsing      — 문법 분석
  ↓  Rewriting    — 내부 규칙 적용
  ↓  Planning     — 실행 계획 생성    ← EXPLAIN이 보여주는 것
  ↓  Execution    — 실제 실행
결과 반환
```

Planning 단계에서 Query Planner가 결정합니다:
"이 테이블을 처음부터 끝까지 읽어야 할까, 아니면 인덱스를 쓰는 게 빠를까?"

같은 SQL이라도 테이블 크기와 인덱스 유무에 따라 다른 계획이 나옵니다.
EXPLAIN은 이 Planning의 결과를 출력합니다.

---

## 2. EXPLAIN의 두 모드

```sql
-- 모드 A: 계획만 생성 (실제 실행 안 함)
EXPLAIN SELECT * FROM business_category;

-- 모드 B: 실제 실행 후, 계획과 실제를 비교 출력
EXPLAIN (ANALYZE, BUFFERS) SELECT * FROM business_category;
```

`ANALYZE`는 쿼리를 실제 실행합니다.
INSERT/UPDATE/DELETE의 경우 변경이 실제로 적용되므로,
항상 `BEGIN ~ ROLLBACK` 안에서 실행합니다.

```sql
BEGIN;
EXPLAIN (ANALYZE, BUFFERS) /* 측정할 쿼리 */;
ROLLBACK;
```

우리는 항상 `EXPLAIN (ANALYZE, BUFFERS)`를 사용합니다.

---

## 3. 출력의 구조 — 트리를 읽는 방향

결과는 트리 형태입니다. **아래에서 위로** 읽습니다.
가장 아래의 노드가 가장 먼저 실행되고, 그 결과가 위로 올라갑니다.

```
 Sort                                              ← ③ 마지막
   ->  Bitmap Heap Scan on business_category       ← ②
         ->  Bitmap Index Scan on idx_...          ← ① 먼저 실행
```

들여쓰기가 깊을수록 먼저 실행됩니다.

---

## 4. cost가 어떻게 계산되는가

공식 문서에서 가장 강조하는 것입니다.

Seq Scan의 cost는 이런 공식으로 계산됩니다:

```
cost = (디스크 페이지 수 × seq_page_cost) + (행 수 × cpu_tuple_cost)
       기본값: 1.0                           기본값: 0.01
```

예를 들어 테이블이 8페이지, 1000행이면:

```
cost = (8 × 1.0) + (1000 × 0.01) = 8.00 + 10.00 = 18.00
```

이 숫자를 실제로 확인할 수 있습니다:

```sql
SELECT relname, relpages, reltuples
FROM pg_class
WHERE relname = 'business_category';
```

Planner는 각 방법(Seq Scan, Index Scan, Bitmap Scan 등)의 cost를 이런 식으로
계산하여 **가장 낮은 것을 선택**합니다.

cost의 단위는 임의입니다. 절대값보다는 **노드 간 상대적 비교**로 읽습니다.
상위 노드의 cost는 아래 모든 노드의 cost를 포함합니다.

---

## 5. 가장 단순한 경우 — Seq Scan

WHERE가 없는 가장 단순한 쿼리부터 봅니다.

```sql
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM business_category;

--                                                            QUERY PLAN
-- -----------------------------------------------------------------------------------------------------------------------
--  Seq Scan on business_category  (cost=0.00..18.50 rows=1000 width=120) (actual time=0.03..0.42 rows=1000 loops=1)
--    Buffers: shared hit=8 read=0
--  Planning Time: 0.08 ms
--  Execution Time: 0.51 ms

ROLLBACK;
```

조건이 없으므로 모든 행을 읽습니다.
Planner는 Seq Scan을 선택하였습니다.

각 숫자의 의미:

```
Seq Scan on business_category
  (cost=0.00..18.50    →  시작비용..총비용 (임의 단위, 위의 공식으로 계산됨)
   rows=1000           →  출력할 행 수 (예측)
   width=120)          →  출력 행의 평균 크기 (바이트)

  (actual time=0.03..0.42  →  실제 실행 시간 (ms)
   rows=1000               →  실제 출력 행 수
   loops=1)                →  반복 실행 횟수

  Buffers: shared hit=8 read=0
    →  hit: 캐시에서 읽은 페이지
    →  read: 디스크에서 읽은 페이지 (이건 느림)
```

---

## 6. WHERE를 추가하면 — Filter의 등장

여기에서 첫 번째 중요한 것이 보입니다.

```sql
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM business_category
WHERE business_id = '99999999-0000-0000-0000-000000000100';

--                                                            QUERY PLAN
-- -----------------------------------------------------------------------------------------------------------------------
--  Seq Scan on business_category  (cost=0.00..19.00 rows=50 width=120) (actual time=0.04..0.48 rows=50 loops=1)
--    Filter: (business_id = '99999999-0000-0000-0000-000000000100'::uuid)
--    Rows Removed by Filter: 950
--    Buffers: shared hit=8 read=0
--  Planning Time: 0.12 ms
--  Execution Time: 0.56 ms

ROLLBACK;
```

여기서 확인할 세 가지가 있습니다.

**첫째, `Filter`가 붙었습니다.**
WHERE 조건이 `Filter`로 처리된 것입니다.
이는 "1000행 전체를 읽은 후에, 조건에 맞는 50행만 출력한 것"을 의미합니다.

**둘째, cost가 오히려 올라갔습니다 (18.50 → 19.00).**
행을 줄이었는데 왜 cost가 올라간 것일까요?
읽는 행의 수는 변하지 않기 때문입니다.
여전히 1000행 전체를 읽고, 그 위에 조건 확인이라는 CPU 비용이 추가되었습니다.

**셋째, `Rows Removed by Filter: 950`.**
1000행을 읽었지만 950행은 조건 불만족으로 버린 것입니다.
이 숫자가 클수록 낭비가 큽니다.

"왜 인덱스를 안 쓰냐?"는 질문이 생깁니다. → Step 8에서 답됩니다.

---

## 7. 테이블이 작으면 항상 Seq Scan — 이건 정상

공식 문서에서 이를 명시한 부분입니다:

> "테이블이 한 페이지만 차지하면, 인덱스가 있어도
> 거의 항상 Seq Scan을 선택한다.
> Planner는 어차피 한 페이지를 읽는 것은 피할 수 없으므로,
> 인덱스를 추가로 읽는 비용을 감당할 가치가 없다고 판단한다."

이건 Planner가 잘못된 것이 아닙니다. 합리적인 선택입니다.

```
테이블 크기별 Planner의 행동:

  1~10페이지    →  항상 Seq Scan (인덱스가 있어도)
                   "전체 읽기의 cost가 낮아서"

  ~400페이지 이상 →  조건에 따라 Bitmap Index Scan 또는 Index Scan
                   "전체 읽기의 cost가 높아져서"
```

따라서 인덱스의 효과를 실제로 보려면 테이블에 충분한 양의 데이터가 있어야 합니다.
테스트 시 픽스처에 대량 데이터를 넣는 배경은 이것입니다.

### PK 조건은 예외입니다

**중요한 예외:** `WHERE id = ?` (PK 조건)는 **테이블 크기와 무관**하게 항상 Index Scan을 선택합니다.

```sql
-- 테이블이 1건이든 100만건이든, 결과는 동일
WHERE id = '...'                    → Index Scan using xxx_pkey
WHERE business_id = '...'           → 테이블 크기에 따라 Seq/Bitmap/Index
```

이는 PK 조건이 "정확히 1건만 반환"한다는 것을 Planner가 알기 때문입니다.

### 대량 데이터가 필요한 파일 vs 불필요한 파일

```
필요한 경우:  WHERE 조건이 PK가 아닌 경우
              → business_id, customer_id, status 등

불필요한 경우: WHERE 조건이 PK인 경우
              → id = ?
              → INSERT/UPDATE/DELETE (내부적으로 PK 조건)
```

| 파일 예시 | WHERE 조건 | 대량 데이터 필요 여부 |
|-----------|-----------|---------------------|
| `카테고리_목록_조회.sql` | business_id | ✅ 필요 (100~500건) |
| `카테고리_상세_조회.sql` | id (PK) | ❌ 불필요 (1건) |
| `카테고리_수정.sql` | id (PK) | ❌ 불필요 (1건) |
| `예약_목록.sql` | customer_id | ✅ 필요 (100~500건) |
| `예약_상세_조회.sql` | id (PK) | ❌ 불필요 (1건) |

이 구분이 명확해지면, 각 파일의 픽스처 크기를 결정할 수 있습니다.

---

## 8. Scan 타입 3종류

Planner가 테이블에서 행을 가져오는 방법은 세 가지입니다.
테이블 크기와 조건의 선택성에 따라 자동으로 선택됩니다.

### Seq Scan

테이블 전체를 읽는 것.

```
언제 선택되는가:
  - 조건(WHERE)이 없는 경우
  - 테이블이 작아서 전체 읽기가 빠를 때
  - 조건에 맞는 행의 비율이 높아서 어차피 대부분을 읽어야 할 때
```

### Index Scan

인덱스를 따라 행에 직접 접근.

```sql
-- PK로 단일 행을 조회하면 Index Scan이 선택됩니다.
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM business_category
WHERE id = '99999999-0000-0000-0000-000000000300';

--  Index Scan using business_category_pkey on business_category
--    (cost=0.29..8.30 rows=1 width=120) (actual time=0.05..0.06 rows=1 loops=1)
--    Index Cond: (id = '99999999-0000-0000-0000-000000000300'::uuid)
--    Buffers: shared hit=3 read=0

ROLLBACK;
```

```
언제 선택되는가:
  - 조건에 맞는 행이 극단적으로 적은 경우 (특히 1건)
  - 해당 컬럼에 인덱스가 있는 경우
```

행을 인덱스 순서대로 읽으므로 접근 비용이 높을 수 있지만,
행이 극단적으로 적어서 위치 수집 단계 자체가 낭비가 되는 경우입니다.

### Bitmap Index Scan → Bitmap Heap Scan

중간 규모일 때 사용되는 두 단계 과정입니다.

```sql
-- 충분히 큰 테이블에서, 중간 수준의 선택성을 가진 조건일 때 등장합니다.
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM business_category
WHERE business_id = '99999999-0000-0000-0000-000000000100';

--  Bitmap Heap Scan on business_category
--    (cost=5.12..42.80 rows=100 width=120) (actual time=0.08..0.35 rows=100 loops=1)
--    Recheck Cond: (business_id = '99999999-...'::uuid)
--    Heap Blocks: exact=4
--    Buffers: shared hit=6 read=0
--    ->  Bitmap Index Scan on idx_business_category_business_id
--          (cost=0.00..5.09 rows=100 width=0) (actual time=0.06..0.06 rows=100 loops=1)
--          Index Cond: (business_id = '99999999-...'::uuid)
--          Buffers: shared hit=2 read=0

ROLLBACK;
```

과정:

```
① Bitmap Index Scan  — 인덱스에서 조건에 맞는 행의 "위치(페이지 번호)"를 수집
② Bitmap Heap Scan   — 수집된 위치를 "물리적 순서대로" 테이블에서 읽기
```

"물리적 순서대로"가 핵심입니다.
인덱스에서 찾은 위치들을 디스크 저장 순서로 정렬하여 읽으므로,
랜덤 접근이 줄어들어 효율적입니다.

`Recheck Cond`는 Bitmap이 페이지 단위로 작동하기 때문에 등장합니다.
해당 페이지의 모든 행이 조건을 만족하는 것은 아닐 수 있으므로,
읽은 행마다 조건을 다시 확인합니다.

```
언제 선택되는가:
  - 행이 "적지 않아서 Index Scan보다" 효율적이지만
    "많지 않아서 Seq Scan보다" 효율적인 중간 규모일 때
```

---

## 9. Index Cond vs Filter — 가장 중요한 구분

EXPLAIN 결과를 분석할 때 **가장 먼저 확인해야 하는 것**입니다.

### Index Cond — 인덱스가 직접 처리

```
Index Scan using idx_... on business_category
  Index Cond: (business_id = '...')     ← 인덱스가 조건 처리
                                           필요한 행만 읽음
```

조건에 맞는 행만 읽습니다. 낭비 없음.

### Filter — 읽은 후에 거르기

```
Seq Scan on business_category
  Filter: (business_id = '...')         ← 읽은 후에 확인
  Rows Removed by Filter: 950          ← 950행 낭비
```

행을 먼저 읽고, 조건을 후에 확인합니다.

### 둘의 차이 요약

```
Index Cond  →  조건에 맞는 행만 읽음        → 낭비 없음
Filter      →  전체 읽고 후에 거르기         → Rows Removed가 클수록 낭비 큼
```

`Rows Removed by Filter`가 크면 → 해당 컬럼에 인덱스가 필요한 신호입니다.
이것이 "아, 여기가 느린 원인이구나"를 파악하는 가장 직관적인 방법입니다.

---

## 10. INSERT / UPDATE / DELETE도 EXPLAIN 가능

EXPLAIN은 SELECT만 해당이 아닙니다.
CUD 작업도 내부적으로 먼저 "행을 찾는 과정"이 있습니다.

```sql
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
DELETE FROM business_category
WHERE id = '99999999-0000-0000-0000-000000000300';

--  Delete on business_category  (cost=0.29..8.30 rows=0 width=0)
--                                (actual time=0.15..0.16 rows=0 loops=1)
--    ->  Index Scan using business_category_pkey on business_category
--          (cost=0.29..8.30 rows=1 width=120) (actual time=0.08..0.09 rows=1 loops=1)
--          Index Cond: (id = '99999999-0000-0000-0000-000000000300'::uuid)
--          Buffers: shared hit=3 read=0
--  Planning Time: 0.10 ms
--  Execution Time: 0.22 ms

ROLLBACK;
```

구조를 읽는 방법:

```
Delete on business_category          ← "찾은 행에 변경 적용"
  ->  Index Scan using ...           ← "행을 어떻게 찾는가"  (여기가 핵심)
```

Delete/Update/Insert 노드는 "변경 적용"입니다.
아래의 Scan 노드가 "행을 어떻게 찾는가"를 보여줍니다.

CUD 작업의 성능 문제도 이 Scan 부분에서 발생합니다.
이것이 우리 파일 구조에서 생성/삭제 파일도 EXPLAIN 대상이 되는 이유입니다.

---

## 11. JOIN — Nested Loop과 loops

JOIN이 있는 쿼리에서는 Join 노드가 추가됩니다.

```sql
BEGIN;

EXPLAIN (ANALYZE, BUFFERS)
SELECT bc.category_name, m.service_name
FROM business_category bc
INNER JOIN menu m ON m.business_category_id = bc.id
WHERE bc.business_id = '99999999-0000-0000-0000-000000000100'
  AND bc.is_active = true;

--  Nested Loop  (cost=0.58..25.40 rows=20 width=80)
--               (actual time=0.06..0.38 rows=20 loops=1)
--    ->  Index Scan using idx_... on business_category bc       ← outer
--          (cost=0.29..8.30 rows=10 width=52)
--          (actual time=0.05..0.12 rows=10 loops=1)
--          Index Cond: (business_id = '99999999-...')
--          Filter: (is_active = true)
--    ->  Index Scan using idx_menu_category on menu m           ← inner
--          (cost=0.29..1.60 rows=2 width=40)
--          (actual time=0.02..0.03 rows=2 loops=10)             ← loops=10
--          Index Cond: (business_category_id = bc.id)
--  Planning Time: 0.15 ms
--  Execution Time: 0.45 ms

ROLLBACK;
```

Nested Loop의 실행 흐름:

```
outer에서 1행 가져옴  →  inner에서 매칭 행 찾음
outer에서 다음 1행    →  inner에서 매칭 행 찾음
outer에서 다음 1행    →  inner에서 매칭 행 찾음
... (outer 행 수만큼 반복)
```

여기서 outer가 10행을 반환하므로, inner는 **10번 실행**됩니다.
inner 노드의 `loops=10`이 그것을 보여줍니다.

`actual time`과 `rows`는 **루프당 평균값**입니다.
총 실행 시간 = `actual time × loops`로 계산합니다.

inner 테이블에 적절한 인덱스가 있어야 반복 실행이 빠릅니다.
`loops`가 높은 inner 노드에 인덱스가 없거나 적절하지 않으면 → 성능 문제의 원인이 됩니다.

---

## 12. 결과를 읽는 체크리스트

파일을 실행한 후, 다음 순서대로 확인합니다.

```
① Scan 타입 확인
   → Seq Scan? Index Scan? Bitmap?
   → Seq Scan에 Filter가 있고 Rows Removed가 크면 → 인덱스 필요 신호

② cost의 원인 추적
   → 아래에서 위로 올라가며, 어떤 노드에서 cost가 높아지는지 추적

③ Buffers 확인
   → shared read가 높으면 → 디스크 I/O 발생 중

④ 예측 vs 실제 비교
   → estimated rows와 actual rows가 크게 다르면 → VACUUM ANALYZE 실행 필요

⑤ loops 확인 (JOIN이 있는 경우)
   → loops가 높은 inner 노드의 인덱스 상태 확인
```

각 파일의 `💡 확인 포인트` 주석은 위 체크리스트와 동일한 관점으로 작성됩니다.
