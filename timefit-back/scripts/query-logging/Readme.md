# SQL Query Extractor

P6spy를 통해 생성되는 서버 로그에서 SQL 쿼리만 추출합니다.

## 파일 구조

```
scripts/query-logging/
├── explain-analyze.ps1   # 실행 스크립트
├── config.json           # 설정 파일
├── extracted-queries.txt # 출력 파일 (생성됨)
└── README.md             # 사용법
```

## 설정 (config.json)

모든 설정이 JSON에 정의되어 있어 PowerShell 코드 수정 없이 동작을 변경할 수 있습니다.


### 주요 설정 항목

**input.logFileRelativePath**: 로그 파일 경로 (프로젝트 루트 기준)  
**extraction.sqlPatterns**: SQL 탐지 패턴 (enabled로 on/off 가능)  
**extraction.cleanupRules**: 불필요한 접두사 제거 규칙  
**processing.removeDuplicates**: 중복 쿼리 제거 여부  
**output.format**: 출력 형식 설정

## 사용 방법

### 1. Integration Test 실행

```bash
.\gradlew.bat test --tests "*IntegrationTest"
```

### 2. 스크립트 실행

```powershell
cd scripts\query-logging
.\explain-analyze.ps1
```

### 3. 결과 확인

`extracted-queries.txt` 파일이 생성됩니다.

## 결과 파일 예시

```
-- ========================================
-- Query #1 (SELECT)
-- ========================================
SELECT m.id, m.service_name FROM menu m WHERE m.business_id = '...';

-- ========================================
-- Query #2 (SELECT)
-- ========================================
SELECT r.*, u.name FROM reservation r JOIN users u ...;
```

## EXPLAIN ANALYZE 실행

### IntelliJ Database Console

1. Database 연결
2. `extracted-queries.txt` 열기
3. 각 쿼리 앞에 `EXPLAIN ANALYZE` 추가
4. 실행 (Ctrl+Enter)

### DBeaver

1. SQL Editor 열기
2. 쿼리 복사
3. `EXPLAIN ANALYZE` 추가
4. 실행 (Ctrl+Enter)

---
