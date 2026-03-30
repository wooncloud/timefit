#!/bin/bash
# Dev Spring Boot 시작 전 초기화 스크립트
# JWT 키 확인/생성 → bootRun 실행

set -e

KEYS_DIR="/workspace/keys/jwt"
ACCESS_PRIVATE="$KEYS_DIR/access_private_key.pem"
REFRESH_PRIVATE="$KEYS_DIR/refresh_private_key.pem"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Spring Boot 초기화 (dev)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# JWT 키 디렉토리 생성
mkdir -p "$KEYS_DIR"

# JWT 키 확인
if [ -f "$ACCESS_PRIVATE" ] && [ -f "$REFRESH_PRIVATE" ]; then
    echo "✅ JWT 키 확인 완료"
else
    echo "⚠️  JWT 키가 없습니다. 자동 생성 중..."

    # Access Token 키 생성 (PKCS#8 형식)
    openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out "$ACCESS_PRIVATE"
    openssl pkey -in "$ACCESS_PRIVATE" -pubout -out "$KEYS_DIR/access_public_key.pem"

    # Refresh Token 키 생성 (PKCS#8 형식)
    openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:4096 -out "$REFRESH_PRIVATE"
    openssl pkey -in "$REFRESH_PRIVATE" -pubout -out "$KEYS_DIR/refresh_public_key.pem"

    echo "✅ JWT 키 생성 완료"
    echo "생성된 파일 목록:"
    ls -la "$KEYS_DIR"
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "Spring Boot 시작..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Spring Boot 실행
exec ./gradlew :web:bootRun --no-daemon