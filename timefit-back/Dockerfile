# Multi-stage build for production
FROM openjdk:21-jdk-slim AS builder

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
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사
COPY . .

# 애플리케이션 빌드
RUN ./gradlew :web:bootJar --no-daemon

# Production stage
FROM openjdk:21-jre-slim

WORKDIR /app

# JAR 파일만 복사
COPY --from=builder /app/web/build/libs/web-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]