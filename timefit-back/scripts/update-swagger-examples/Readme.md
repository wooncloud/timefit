# Swagger Example 자동 수정 스크립트

seed-bulk.sql 기반으로 모든 Swagger annotation의 example 값을 실제 동작하는 데이터로 자동 변경

---

## 🚀 실행 방법

### ⚠️ 중요: BOM 오류 발생시

BOM 관련 컴파일 오류가 발생했다면:

```bash
# 1. Git으로 원본 복구 (필수!)
git checkout -- web/src/main/java/timefit/common/swagger

# 2. 수정된 스크립트로 재실행
```

### Windows (PowerShell)

```powershell
cd timefit-back
.\scripts\update-swagger-examples\update-swagger-examples.ps1
```

### Mac/Linux (Bash)

```bash
cd timefit-back
chmod +x scripts/update-swagger-examples/update-swagger-examples.sh
./scripts/update-swagger-examples/update-swagger-examples.sh
```

---

## 📋 사전 준비

### Windows
- PowerShell 5.0 이상 (기본 설치됨)

### Mac/Linux
- Bash (기본 설치됨)
- jq: `brew install jq` (Mac) 또는 `sudo apt-get install jq` (Ubuntu)

---

## 🎯 수정 범위

### 대상 디렉토리
```
web/src/main/java/timefit/common/swagger/
├── requestbody/
└── operation/
```

### 자동 수정 항목
- ✅ 이메일: `user@example.com` → `owner1@timefit.com`
- ✅ 비밀번호: `a12345678` → `password123`
- ✅ UUID: `550e8400-...` → `30000000-...`
- ✅ 전화번호: `01012345678` → `010-1111-1111` (하이픈 추가)
- ✅ 사업자번호: `1234567890` → `123-45-67890` (하이픈 추가)
- ✅ 날짜: `2025-12-01` → `2025-01-10`
- ✅ 시간: `14:00:00` → `09:00:00`
- ✅ 이름: `홍길동` → `Owner Kim`
- ✅ 주소: `서울시 강남구...` → `Seoul Gangnam 123`

---

## ✅ 실행 후 확인

### 1. 변경 사항 확인
```bash
git diff web/src/main/java/timefit/common/swagger
```

### 2. 컴파일 테스트
```bash
./gradlew compileJava
```

### 3. Swagger UI 확인
```
http://localhost:8080/swagger-ui/index.html
```

### 4. 테스트
- POST /api/auth/signin의 example: `owner1@timefit.com` ✅
- Try it out → Execute → 200 OK ✅

---

## 🔧 매핑 수정

`mappings.json` 파일을 편집하여 매핑 추가/수정 가능

```json
{
  "emails": {
    "user@example.com": "owner1@timefit.com",
    "admin@test.com": "admin@timefit.com"
  }
}
```

수정 후 스크립트 재실행

---

## 🔄 되돌리기

### Git으로 복원
```bash
git checkout -- web/src/main/java/timefit/common/swagger
```

### 백업에서 복원
```bash
# 자동 백업 위치: swagger-backup/[timestamp]/
cp -r swagger-backup/[timestamp]/swagger web/src/main/java/timefit/common/
```

---

## 📁 프로젝트 구조

```
timefit-back/
├── scripts/
│   └── update-swagger-examples/
│       ├── update-swagger-examples.ps1
│       ├── update-swagger-examples.sh
│       ├── mappings.json
│       └── README.md
├── web/
│   └── src/main/java/timefit/common/swagger/  (수정 대상)
└── swagger-backup/  (자동 백업)
```

---

## 🐛 문제 해결

### Q1. BOM 관련 컴파일 오류 (illegal character: '\ufeff')
```
A. Git으로 원본 복구 후 재실행

# 1. 원본 복구
git checkout -- web/src/main/java/timefit/common/swagger

# 2. 백업 폴더 삭제 (선택사항)
rm -rf swagger-backup

# 3. 스크립트 재실행
.\scripts\update-swagger-examples\update-swagger-examples.ps1

※ 수정된 스크립트는 UTF-8 without BOM으로 저장합니다
```

### Q2. "mappings.json을 찾을 수 없습니다"
```
A. 실행 위치를 timefit-back로 변경
   cd timefit-back
```

### Q2. "mappings.json을 찾을 수 없습니다"
```
A. 실행 위치를 timefit-back로 변경
   cd timefit-back
```

### Q3. "swagger 디렉토리를 찾을 수 없습니다"
```
A. 프로젝트 구조 확인
   ls web/src/main/java/timefit/common/
```

### Q4. "jq를 찾을 수 없습니다" (Bash만)
```
A. jq 설치
   Mac: brew install jq
   Ubuntu: sudo apt-get install jq
```

### Q5. 매핑이 잘못됨
```
A. mappings.json 확인
   - businessNumbers: "1234567890" → "123-45-67890" ✅
   - phones: "01012345678" → "010-1111-1111" ✅
```

---

**버전**: 1.2  
**최종 업데이트**: 2025-01-08  
**작성자**: 세창