## intro
k6 부하 테스트(Level3: reservation 조회) 과정에서
부하를 증가시켜도 주기적으로 latency spike가 발생하는 문제를 확인함.

Prometheus JVM 메트릭 및 GC 로그 분석 결과,
'end of minor GC G1 Evacuation Pause' 구간에서
GC Time(ms) spike가 반복적으로 발생하는 패턴을 확인하였고,
Young(Eden) 영역의 과도한 객체 생성 및 GC 빈도가
주요 원인 중 하나일 가능성이 있다고 판단함.

이에 따라 본 커밋에서는 즉각적인 최적값을 찾기보다는,
GC 동작을 관측·분석하기 위한 기준선(Baseline)으로서
다음과 같은 G1 GC 튜닝 옵션을 적용함:

- 고정 힙 크기(Xms/Xmx 동일)로 힙 리사이징 제거
- G1 GC 사용을 명시하여 Pause Time 예측 가능성 확보
- Young Generation 크기 범위를 제한하여 Eden 압박 완화
- GC 로그를 상세히 기록하여 사후 검증 가능하도록 설정



# JVM 튜닝 옵션 학습 가이드 (ai gen)

> **목적**: JVM 성능 튜닝 시 사용하는 주요 옵션들을 카테고리별로 정리하고, 각 옵션의 역할·선택 기준·트레이드오프를 이해한다.

---

## 목차
1. [JVM 튜닝 옵션 전체 맵](#1-jvm-튜닝-옵션-전체-맵)
2. [Heap 크기 제어](#2-heap-크기-제어)
3. [GC 알고리즘 선택](#3-gc-알고리즘-선택)
4. [G1 GC 상세 파라미터](#4-g1-gc-상세-파라미터)
5. [GC 로깅 및 모니터링](#5-gc-로깅-및-모니터링)
6. [실전 예시: Reservation 조회 API 최적화](#6-실전-예시-reservation-조회-api-최적화)
7. [튜닝 시작 체크리스트](#7-튜닝-시작-체크리스트)

---

## 1. JVM 튜닝 옵션 전체 맵

JVM 튜닝 옵션은 크게 다음 카테고리로 나뉜다:

| 카테고리 | 주요 옵션 예시 | 영향 범위 |
|---------|--------------|---------|
| **Heap 크기** | `-Xms`, `-Xmx` | 메모리 사용량, GC 빈도 |
| **GC 알고리즘** | `-XX:+UseG1GC`, `-XX:+UseZGC` | Throughput vs Latency |
| **GC 동작 파라미터** | `-XX:MaxGCPauseMillis`, `-XX:G1NewSizePercent` | GC 빈도, Pause Time |
| **로깅** | `-Xlog:gc*` | 분석 가능성 |
| **기타 성능** | `-XX:+UseStringDeduplication`, Thread Stack | 메모리 최적화 |

**튜닝 순서 권장**:
1. GC 로그 활성화 → 현재 문제 파악
2. Heap 크기 조정 → GC 빈도 제어
3. GC 알고리즘 선택 → 워크로드 특성 반영
4. GC 파라미터 세부 조정 → 목표 달성

---

## 2. Heap 크기 제어

### 2.1. 주요 옵션

#### `-Xms<size>`
- **역할**: JVM 시작 시 초기 Heap 크기 설정
- **예시**: `-Xms2g` → 2GB로 시작
- **선택 기준**:
  - 애플리케이션의 평균 메모리 사용량에 가깝게 설정
  - 서버 물리 메모리의 25-50% 수준 (다른 프로세스 고려)

#### `-Xmx<size>`
- **역할**: Heap의 최대 크기 제한
- **예시**: `-Xmx4g` → 최대 4GB
- **선택 기준**:
  - OOM 방지를 위한 상한선
  - 컨테이너 환경에서는 컨테이너 메모리 제한의 70-80%

### 2.2. 고정 Heap vs 가변 Heap

| 전략 | 설정 예시 | 장점 | 단점 |
|-----|---------|------|------|
| **고정** | `-Xms2g -Xmx2g` | Heap resize 오버헤드 제거, GC 예측 가능 | 초기 메모리 점유 큰 편 |
| **가변** | `-Xms512m -Xmx2g` | 초기 메모리 절약 | Heap resize 시 성능 변동 |

**권장**:
- 프로덕션 서버에서는 **고정 Heap** 선호 (성능 예측성)
- 개발 환경에서는 가변 Heap으로 리소스 절약

### 2.3. Heap 크기 결정 가이드

```
1. 현재 메모리 사용량 확인 (모니터링 툴 or jstat)
2. Peak 사용량 + 여유분 20-30% → Xmx 후보
3. 평균 사용량 → Xms 후보
4. GC 로그로 검증: 
   - GC 빈도가 과도하면 → Heap 증가
   - Full GC 발생하면 → Xmx 증가 고려
```

---

## 3. GC 알고리즘 선택

### 3.1. 주요 GC 알고리즘 비교

| GC 알고리즘 | 옵션 | 적합한 경우 | 주요 특징 |
|-----------|------|-----------|---------|
| **Serial GC** | `-XX:+UseSerialGC` | 단일 코어, 작은 Heap (<100MB) | 단순, Throughput 낮음 |
| **Parallel GC** | `-XX:+UseParallelGC` | Batch 처리, Throughput 우선 | 멀티 스레드, Pause Time 긴 편 |
| **G1 GC** | `-XX:+UseG1GC` | **대부분의 서버 애플리케이션** (기본값) | Pause Time 예측 가능, 4GB+ Heap |
| **ZGC** | `-XX:+UseZGC` | 초대형 Heap (수백 GB), 극저지연 필요 | Pause Time <10ms, Java 15+ |
| **Shenandoah GC** | `-XX:+UseShenandoahGC` | 저지연 + OpenJDK 환경 | ZGC 유사, Oracle JDK 미지원 |

### 3.2. G1 GC (Garbage First GC)

**언제 사용?**
- Heap 크기 4GB 이상
- 요청 단위 짧은 작업 (API 서버, 웹 애플리케이션)
- Pause Time 안정성이 Throughput보다 중요

**핵심 개념**:
- Heap을 Region(기본 1-32MB)으로 분할
- Young/Old 구분이 동적 (Region 단위로 역할 변경)
- Mixed GC로 Old 영역도 점진적 수집 → Full GC 최소화

**기본 동작**:
```
[Eden 가득 참] 
  → Young GC (Eden + Survivor → Survivor or Old)
  → Pause Time: 수십~200ms

[Old 영역 임계치 초과]
  → Mixed GC (Young + Old 일부)
  → 여러 번 나눠서 수집

[극단적 상황]
  → Full GC (전체 Heap Stop-The-World)
  → 피해야 할 상황
```

### 3.3. GC 선택 의사결정 트리

```
[Heap 크기가 4GB 미만?]
  Yes → Parallel GC 고려 (Throughput 우선)
  No  ↓

[Latency SLA가 100ms 이하로 엄격?]
  Yes → ZGC 고려 (Java 17+)
  No  ↓

[대부분의 일반 서버 애플리케이션]
  → G1 GC (기본 선택)
```

---

## 4. G1 GC 상세 파라미터

### 4.1. Young Generation 크기 제어

#### `-XX:G1NewSizePercent=<n>`
- **역할**: Young 영역의 최소 비율 (전체 Heap 대비 %)
- **기본값**: 5%
- **설정 예시**: `-XX:G1NewSizePercent=30` → 최소 30%
- **효과**:
  - 값 증가 → Young GC 빈도 감소, Pause Time 증가
  - 값 감소 → Young GC 빈도 증가, 각 GC는 빠름

**선택 기준**:
- 객체 생성이 많은 애플리케이션 → 30-40%로 증가
- 장수 객체 비중이 높은 경우 → 10-20%로 감소

#### `-XX:G1MaxNewSizePercent=<n>`
- **역할**: Young 영역의 최대 비율
- **기본값**: 60%
- **설정 예시**: `-XX:G1MaxNewSizePercent=40` → 최대 40%
- **효과**:
  - Old 영역 최소 보장 → Promotion 압박 완화
  - Mixed GC 발생 빈도에 영향

**트레이드오프**:
```
Young 크기 증가:
  (+) Young GC 빈도 감소
  (-) 각 Young GC Pause Time 증가
  (-) Old 영역 압박 → Mixed GC 증가 가능

Young 크기 감소:
  (+) 각 Young GC 빠름
  (-) GC 빈도 증가
  (+) Old 영역 여유 → Mixed GC 감소
```

### 4.2. Pause Time 목표

#### `-XX:MaxGCPauseMillis=<n>`
- **역할**: GC Pause Time 목표값 (ms)
- **기본값**: 200ms
- **설정 예시**: `-XX:MaxGCPauseMillis=100` → 100ms 목표
- **중요**: 이는 **Hard Limit이 아닌 Hint**

**G1의 동작**:
- 이 값을 기준으로 수집할 Region 수 조절
- 목표를 달성하기 위해 Young 크기도 동적 조정
- 실제 Pause Time은 초과 가능 (메모리 압박 시)

**선택 기준**:
```
[Latency 민감]
  → 50-100ms로 낮춤
  → 단, Throughput 희생 가능
  → GC 로그로 실제 달성 여부 검증 필수

[Throughput 우선]
  → 200-500ms로 유지
  → GC 빈도 감소
```

### 4.3. 기타 G1 옵션

#### `-XX:InitiatingHeapOccupancyPercent=<n>` (IHOP)
- **역할**: Old 영역이 전체 Heap의 n%를 넘으면 Mixed GC 시작
- **기본값**: 45%
- **조정**:
  - 값 감소 (30%) → Mixed GC 조기 시작, Full GC 예방
  - 값 증가 (60%) → Mixed GC 지연, Throughput 향상

#### `-XX:G1HeapRegionSize=<n>`
- **역할**: Region 크기 설정 (1MB, 2MB, 4MB, ..., 32MB)
- **기본값**: Heap 크기 기반 자동 계산
- **조정**: 일반적으로 기본값 사용, 대형 객체 많으면 증가 고려

---

## 5. GC 로깅 및 모니터링

### 5.1. GC 로그 활성화 (Java 9+)

#### `-Xlog:gc*:file=<path>:time,uptime,level,tags`
- **역할**: GC 이벤트를 파일로 기록
- **예시**:
  ```bash
  -Xlog:gc*:file=logs/gc.log:time,uptime,level,tags
  ```

**로그 포맷 옵션**:
- `time`: 타임스탬프
- `uptime`: JVM 시작 후 경과 시간
- `level`: 로그 레벨 (info, debug 등)
- `tags`: GC 이벤트 태그 (gc, gc+heap 등)

### 5.2. GC 로그 분석 포인트

#### 분석해야 할 주요 지표

```
1. Young GC
   - 빈도: 초당 몇 회?
   - Pause Time: 평균/Max
   - 패턴: 규칙적인가, 불규칙한가

2. Mixed GC
   - 발생 빈도
   - Old 영역 수집 효율

3. Full GC
   - 발생 여부 (발생 시 즉시 조사)
   - 원인: Heap 부족? Promotion 실패?

4. Allocation Rate
   - Eden 영역 소진 속도
   - 객체 생성 패턴 분석
```

#### 로그 예시 해석

```
[2025-02-10T10:15:30.123+0900][0.456s][info][gc] GC(10) Pause Young (Normal) (G1 Evacuation Pause) 512M->128M(2048M) 45.678ms

해석:
- Pause Young: Young GC 발생
- 512M->128M: GC 전/후 Heap 사용량
- (2048M): 전체 Heap 크기
- 45.678ms: Pause Time
```

### 5.3. 모니터링 도구

| 도구 | 용도 | 장점 |
|-----|------|------|
| **GCViewer** | GC 로그 시각화 | 오프라인 분석, Pause Time 그래프 |
| **Prometheus + Grafana** | 실시간 모니터링 | JVM 메트릭 대시보드 |
| **VisualVM** | 실시간 프로파일링 | Heap Dump 분석 |
| **jstat** | CLI 기반 실시간 통계 | 간단한 확인용 |

**명령어 예시**:
```bash
# GC 통계 1초마다 출력
jstat -gc <pid> 1000

# Heap 사용량 확인
jstat -gccapacity <pid>
```

---

## 6. 실전 예시: Reservation 조회 API 최적화

### 6.1. 문제 상황

**증상**:
- k6 부하 테스트에서 주기적 Latency Spike 발생
- Prometheus 메트릭 분석 결과, Young GC 중 Pause Time Spike 확인

**원인 가설**:
- Eden 영역에서 과도한 객체 생성
- Young GC 빈도 높음 → GC Time(ms) spike 반복

### 6.2. 적용한 JVM 옵션

```bash
-Xms2g                              # 1. 고정 Heap 시작
-Xmx2g                              # 2. 고정 Heap 최대
-XX:+UseG1GC                        # 3. G1 GC 명시
-XX:G1NewSizePercent=30             # 4. Young 최소 30%
-XX:G1MaxNewSizePercent=60          # 5. Young 최대 60%
-XX:MaxGCPauseMillis=200            # 6. Pause Time 목표 200ms
-Xlog:gc*:file=logs/gc.log:time,uptime,level,tags  # 7. GC 로그
```

### 6.3. 각 옵션의 선택 근거

| 옵션 | 선택 이유 | 기대 효과 |
|-----|---------|---------|
| `-Xms2g -Xmx2g` | Heap resize 제거, GC 안정화 | 성능 변동 최소화 |
| `-XX:+UseG1GC` | API 서버 특성, Latency 우선 | Pause Time 예측 가능 |
| `G1NewSizePercent=30` | Eden 압박 완화 | Young GC 빈도 감소 |
| `G1MaxNewSizePercent=60` | Old 영역 최소 보장 | Promotion 실패 방지 |
| `MaxGCPauseMillis=200` | API Latency SLA 고려 | GC Pause 제한 |
| `Xlog:gc*` | 검증 가능성 확보 | 사후 분석 근거 |

### 6.4. 검증 계획

```
1. GC 로그 수집 (최소 24시간)
2. 분석 포인트:
   ✓ Young GC Pause Time 분포
   ✓ 200ms 초과 빈도
   ✓ Mixed GC 발생 패턴
   ✓ Full GC 발생 여부

3. 조정 후보:
   - Pause Time 초과 빈번 → MaxGCPauseMillis 증가 or Young 크기 감소
   - Mixed GC 과다 → G1MaxNewSizePercent 감소
   - Young GC 여전히 빈번 → Heap 크기 증가
```

### 6.5. 현재 설정의 위치

```
[성능 튜닝 단계]

Phase 1: 문제 식별 ✓
  → Latency Spike 원인 파악 완료

Phase 2: Baseline 설정 ← 현재 위치
  → GC 관측 가능한 기준값 적용
  → 최적값이 아닌 "분석 가능한 상태" 구축

Phase 3: 반복 최적화 (To Be)
  → GC 로그 기반 파라미터 조정
  → A/B 테스트로 개선 효과 검증
```

---

## 7. 튜닝 시작 체크리스트

### 7.1. 사전 준비

- [ ] 현재 메모리 사용량 확인 (jstat, 모니터링 툴)
- [ ] 주요 성능 지표 기록 (Baseline)
  - 평균 응답 시간
  - P99 Latency
  - GC 빈도/Pause Time
- [ ] 서버 물리 메모리 확인 (컨테이너 제한 포함)

### 7.2. 초기 설정 단계

```
1. GC 로그 활성화 (-Xlog:gc*)
   → 반드시 첫 번째 단계

2. Heap 크기 설정 (-Xms, -Xmx)
   → 물리 메모리의 50-70%
   → 고정 Heap 권장

3. GC 알고리즘 선택
   → 4GB+ → G1 GC
   → 극저지연 필요 → ZGC

4. 기본 파라미터 적용
   → MaxGCPauseMillis는 보수적으로 (200-300ms)
   → Young 크기는 기본값으로 시작
```

### 7.3. 검증 및 반복

```
[24시간 모니터링]
  ↓
[GC 로그 분석]
  ↓
[문제 발견?]
  Yes → 파라미터 조정 → 다시 모니터링
  No  → Baseline 확정
```

### 7.4. 주의사항

**하지 말아야 할 것**:
- ❌ 여러 파라미터를 동시에 변경 (원인 파악 불가)
- ❌ GC 로그 없이 파라미터 조정 (근거 없는 튜닝)
- ❌ 프로덕션에서 바로 적용 (테스트 환경 검증 필수)

**해야 할 것**:
- ✅ 한 번에 하나씩 변경
- ✅ 변경 전후 성능 지표 비교
- ✅ GC 로그로 실제 동작 확인
- ✅ 부하 테스트로 검증

---

## 참고 자료

- Oracle Java GC Tuning Guide: https://docs.oracle.com/en/java/javase/17/gctuning/
- G1 GC Paper: https://www.oracle.com/technetwork/tutorials/tutorials-1876574.html
- GCViewer: https://github.com/chewiebug/GCViewer
- Prometheus JVM Exporter: https://github.com/prometheus/jvm_exporter