#!/bin/bash
# Spring Boot 시작 전 초기화 스크립트
# JWT 키 확인/생성 → Spring Boot 실행

set -e

KEYS_DIR="/app/keys/jwt"
ACCESS_PRIVATE="$KEYS_DIR/access_private_key.pem"
REFRESH_PRIVATE="$KEYS_DIR/refresh_private_key.pem"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Spring Boot 초기화"
echo "Profile: ${SPRING_PROFILES_ACTIVE:-prod}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# JWT 키 디렉토리 생성
mkdir -p "$KEYS_DIR"

# JWT 키 확인
if [ -f "$ACCESS_PRIVATE" ] && [ -f "$REFRESH_PRIVATE" ]; then
    echo "✅ JWT 키 확인 완료"
else
    echo "⚠️  JWT 키가 없습니다. 자동 생성 중..."

    # Access Token 키 생성
    openssl genrsa -out "$ACCESS_PRIVATE" 2048 2>/dev/null
    openssl rsa -in "$ACCESS_PRIVATE" -pubout -out "$KEYS_DIR/access_public_key.pem" 2>/dev/null

    # Refresh Token 키 생성
    openssl genrsa -out "$REFRESH_PRIVATE" 4096 2>/dev/null
    openssl rsa -in "$REFRESH_PRIVATE" -pubout -out "$KEYS_DIR/refresh_public_key.pem" 2>/dev/null

    echo "✅ JWT 키 생성 완료"
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Spring Boot 시작..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Spring Boot 실행
exec java -jar /app/app.jar "$@"