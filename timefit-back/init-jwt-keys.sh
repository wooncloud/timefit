#!/bin/sh
# JWT RSA 키 자동 생성 스크립트
# docker-compose 시작 시 자동 실행

set -e

KEYS_DIR="./keys/jwt"
ACCESS_PRIVATE="$KEYS_DIR/access_private_key.pem"
ACCESS_PUBLIC="$KEYS_DIR/access_public_key.pem"
REFRESH_PRIVATE="$KEYS_DIR/refresh_private_key.pem"
REFRESH_PUBLIC="$KEYS_DIR/refresh_public_key.pem"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "JWT RSA 키 초기화"
echo "Profile: ${SPRING_PROFILES_ACTIVE:-dev}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 키 디렉토리 생성
mkdir -p "$KEYS_DIR"

# OpenSSL 설치 확인
if ! command -v openssl > /dev/null 2>&1; then
    echo "❌ OpenSSL이 설치되어 있지 않습니다"
    echo "설치: sudo apt-get install openssl (Ubuntu/Debian)"
    exit 1
fi

# 기존 키 확인
if [ -f "$ACCESS_PRIVATE" ] && [ -f "$REFRESH_PRIVATE" ]; then
    echo ""
    echo "✓ JWT RSA 키가 이미 존재합니다"
    echo "  Access Token 키:"
    echo "    - $ACCESS_PRIVATE"
    echo "    - $ACCESS_PUBLIC"
    echo "  Refresh Token 키:"
    echo "    - $REFRESH_PRIVATE"
    echo "    - $REFRESH_PUBLIC"
    echo ""
    echo "💡 키를 새로 생성하려면:"
    echo "  1. 기존 키 삭제: rm -rf ./keys/jwt/*"
    echo "  2. 스크립트 재실행: ./init-jwt-keys.sh"
    exit 0
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. Access Token용 RSA 키 생성 (2048 bits)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Access Token Private Key (2048 bits for RS256)
openssl genrsa -out "$ACCESS_PRIVATE" 2048

# Access Token Public Key
openssl rsa -in "$ACCESS_PRIVATE" -pubout -out "$ACCESS_PUBLIC"

echo "✅ Access Token 키 생성 완료"
echo "  Private: $ACCESS_PRIVATE"
echo "  Public:  $ACCESS_PUBLIC"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "2. Refresh Token용 RSA 키 생성 (4096 bits)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Refresh Token Private Key (4096 bits for RS512)
openssl genrsa -out "$REFRESH_PRIVATE" 4096

# Refresh Token Public Key
openssl rsa -in "$REFRESH_PRIVATE" -pubout -out "$REFRESH_PUBLIC"

echo "✅ Refresh Token 키 생성 완료"
echo "  Private: $REFRESH_PRIVATE"
echo "  Public:  $REFRESH_PUBLIC"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ JWT RSA 키 생성 완료!"
echo ""
echo "📌 다음 단계:"
echo "  1. keys/jwt/ 폴더를 .gitignore에 추가"
echo "  2. 백업: keys/jwt/ 폴더를 안전한 곳에 백업"
echo "  3. 프로덕션: 환경 변수 또는 Secrets Manager 사용 권장"
echo ""
echo "⚠️  주의사항:"
echo "  - Private Key는 절대 노출하지 마세요"
echo "  - Git에 커밋하지 마세요"
echo "  - 프로덕션에서는 키 로테이션 계획 수립"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"