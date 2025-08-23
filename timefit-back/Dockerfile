FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
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

# gradlew 실행 권한 다시 부여 (COPY . . 로 인해 덮어써짐)
RUN chmod +x ./gradlew

# 애플리케이션 빌드
RUN ./gradlew :web:bootJar --no-daemon

# 포트 노출
EXPOSE 8080

# 애플리케이션 실행
CMD ["java", "-jar", "web/build/libs/web-0.0.1-SNAPSHOT.jar"]