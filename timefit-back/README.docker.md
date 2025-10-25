# Docker 사용 가이드

## 🚀 빠른 시작

### 가장 간단한 방법
```bash
# 처음 시작할 때
./scripts/dev.sh start

# 코드 수정 후
./scripts/dev.sh restart
```

그게 전부입니다! 🎉

---

## 📝 주요 명령어

### 스크립트 명령어 (권장)

| 명령어 | 설명 |
|--------|------|
| `./scripts/dev.sh start` | 개발 환경 시작 (최초 실행) |
| `./scripts/dev.sh restart` | 백엔드 재시작 (코드 변경 후) |
| `./scripts/dev.sh logs` | 로그 확인 |
| `./scripts/dev.sh status` | 컨테이너 상태 확인 |
| `./scripts/dev.sh stop` | 모든 컨테이너 정지 |
| `./scripts/dev.sh db` | PostgreSQL 접속 |
| `./scripts/dev.sh clean` | 완전히 초기화 |
| `./scripts/dev.sh rebuild` | 캐시 없이 재빌드 |

### Docker Compose 직접 사용

```bash
# 개발 모드 시작
docker-compose --profile dev up -d --build

# 백엔드 재시작
docker-compose restart timefit-back

# 로그 확인
docker-compose logs -f timefit-back

# 전체 중지
docker-compose down

# DB 데이터까지 삭제하고 중지
docker-compose down -v

# 컨테이너 상태 확인
docker-compose ps
```

## 🔄 개발 워크플로우

1. **처음 시작**: `./scripts/dev.sh start`
2. **코드 수정**: Java 파일 수정
3. **재시작**: `./scripts/dev.sh restart` (10-15초 소요)
4. **로그 확인**: `./scripts/dev.sh logs`
5. **작업 종료**: `./scripts/dev.sh stop`

## 🏗️ 개발 모드 vs 프로덕션 모드

| 항목 | 개발 모드 | 프로덕션 모드 |
|------|----------|--------------|
| Dockerfile | `Dockerfile.dev` | `Dockerfile` |
| 빌드 방식 | 매번 재빌드 | Multi-stage build |
| 이미지 크기 | 큼 (JDK 포함) | 작음 (JRE만) |
| 재시작 속도 | 빠름 (10-15초) | 느림 (재빌드 필요) |
| 코드 변경 | restart만 필요 | rebuild 필요 |
| 용도 | 로컬 개발 | 배포 환경 |

### 프로덕션 모드 사용

```bash
# 프로덕션 모드로 실행
docker-compose --profile prod up -d --build

# 코드 수정 후에는 반드시 재빌드 필요
docker-compose --profile prod up -d --build
```

## 🏥 헬스 체크

```bash
# API 서버 헬스체크
curl http://localhost:8080/actuator/health

# 서비스 상태 확인
./scripts/dev.sh status
```

### 접속 정보
- **API Server**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **Database**: PostgreSQL on localhost:5432

## 🗄️ 데이터베이스 접속

```bash
# 스크립트 사용 (권장)
./scripts/dev.sh db

# 또는 직접 접속
docker exec -it timefit-postgres psql -U root -d postgres

# 로컬에서 접속
psql -h localhost -p 5432 -U root -d postgres
```

### PostgreSQL 명령어
```sql
\l              -- 데이터베이스 목록
\dt             -- 테이블 목록
\d table_name   -- 테이블 구조 확인
\q              -- 종료
```

## ⚙️ 환경 변수 설정

`.env` 파일을 수정하여 환경 변수 변경:

```env
POSTGRES_USER=root
POSTGRES_PASSWORD=usa1234
POSTGRES_DB=postgres
JWT_SECRET=your-secret-key
```

변경 후 재시작:
```bash
./scripts/dev.sh restart
```

## 🐛 트러블슈팅

### 포트 충돌
```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080

# 해당 프로세스 종료 후 재시작
kill -9 <PID>
./scripts/dev.sh restart
```

### 빌드 캐시 문제
```bash
# 완전히 새로 빌드
./scripts/dev.sh rebuild

# 또는
docker-compose --profile dev build --no-cache
docker-compose --profile dev up -d
```

### DB 연결 실패
```bash
# DB 컨테이너 상태 확인
docker-compose ps

# DB 로그 확인
docker-compose logs db

# DB 헬스체크 확인
docker inspect timefit-postgres | grep -A 10 Health
```

### 의존성 업데이트
```bash
# Gradle 캐시 볼륨 삭제 후 재빌드
docker-compose down
docker volume rm timefit-back_gradle_cache
./scripts/dev.sh start
```

### 이상하게 동작할 때
```bash
# 완전히 초기화 후 재시작
./scripts/dev.sh clean
./scripts/dev.sh start
```

## 📊 로그 확인

```bash
# 실시간 로그 (Ctrl+C로 종료)
./scripts/dev.sh logs

# 또는 직접 docker 명령어 사용
docker logs timefit-back -f --tail 100

# DB 로그
docker logs timefit-postgres -f --tail 50
```

## 🎯 핵심 포인트

✅ **코드 수정 시**: `./scripts/dev.sh restart`만 하면 됩니다
✅ **빠른 재시작**: 약 10-15초 소요
✅ **자동 빌드**: Gradle이 변경사항을 자동으로 감지하고 빌드
✅ **DB 유지**: 데이터는 volume에 저장되어 재시작해도 유지
✅ **Volume mount**: 코드가 실시간으로 컨테이너에 반영됨

## ⚡ 성능 최적화 팁

1. **Gradle 캐시 활용**: volume mount로 gradle_cache 유지
2. **레이어 캐싱**: build.gradle 파일들을 먼저 복사하여 의존성 캐시
3. **개발 모드 사용**: 코드 변경 시 restart만으로 빠른 반영
4. **멀티 스테이지 빌드**: 프로덕션 모드에서 최적화된 이미지 생성

## 📚 구조

```
timefit-back/
├── Dockerfile              # 프로덕션용 (Multi-stage build)
├── Dockerfile.dev          # 개발용 (빠른 재시작)
├── docker-compose.yml      # Docker Compose 설정
├── scripts/
│   └── dev.sh             # 개발 환경 관리 스크립트
└── README.docker.md       # 이 파일
```
