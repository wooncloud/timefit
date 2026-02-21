# ============================================================
# Multi-stage build for production
#
# Stage 1: Build - Gradle로 JAR 파일 빌드
# Stage 2: Runtime - 최소한의 JRE만 포함하여 이미지 크기 최소화
# ============================================================

# ============================================================
# Stage 1: Build
# ============================================================
FROM amazoncorretto:21-alpine AS builder

WORKDIR /app

# Gradle Wrapper와 설정 파일들 복사
COPY gradlew .
COPY gradle gradle/
COPY build.gradle .
COPY settings.gradle .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 서브모듈 build.gradle 파일들 복사
COPY domain/build.gradle domain/
COPY jpa-common/build.gradle jpa-common/
COPY user/build.gradle user/
COPY web/build.gradle web/

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY . .

# 애플리케이션 빌드 (QueryDSL Q클래스 생성 포함)
RUN ./gradlew :web:bootJar --no-daemon

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM amazoncorretto:21-alpine

# OpenSSL 설치 (JWT 키 생성용)
RUN apk add --no-cache openssl bash

WORKDIR /app

# JAR 파일만 복사
COPY --from=builder /app/web/build/libs/web-0.0.1-SNAPSHOT.jar app.jar

# Entrypoint 스크립트 복사
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh

# 포트 노출
EXPOSE 8080

# Entrypoint 설정 (JWT 키 확인 → Spring Boot 실행)
ENTRYPOINT ["/app/docker-entrypoint.sh"]