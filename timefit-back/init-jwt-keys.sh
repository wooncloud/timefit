#!/bin/bash
# JWT RSA 키 자동 생성 스크립트
# docker-compose 시작 시 자동 실행

set -e

KEYS_DIR="./keys/jwt"
ACCESS_PRIVATE="$KEYS_DIR/access_private_key.pem"
ACCESS_PUBLIC="$KEYS_DIR/access_public_key.pem"
REFRESH_PRIVATE="$KEYS_DIR/refresh_private_key.pem"
REFRESH_PUBLIC="$KEYS_DIR/refresh_public_key.pem"

# 키 유효 기간 (일)
KEY_VALIDITY_DAYS=30

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "JWT RSA 키 초기화"
echo "Profile: ${SPRING_PROFILES_ACTIVE:-dev}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# 키 디렉토리 생성
mkdir -p "$KEYS_DIR"

# OpenSSL 설치 확인
if ! command -v openssl > /dev/null 2>&1; then
    echo "❌ OpenSSL이 설치되어 있지 않습니다"
    echo "설치: apk add openssl (Alpine)"
    exit 1
fi

# 기존 키 확인 및 유효성 검사
if [ -f "$ACCESS_PRIVATE" ] && [ -f "$REFRESH_PRIVATE" ]; then
    echo ""
    echo "✓ JWT RSA 키 발견"

    # 키 생성 시간 확인 (파일 수정 시간 기준)
    if [ "$(uname)" = "Linux" ]; then
        # Linux (Alpine)
        KEY_AGE_SECONDS=$(( $(date +%s) - $(stat -c %Y "$ACCESS_PRIVATE" 2>/dev/null || echo 0) ))
    else
        # macOS
        KEY_AGE_SECONDS=$(( $(date +%s) - $(stat -f %m "$ACCESS_PRIVATE" 2>/dev/null || echo 0) ))
    fi

    KEY_AGE_DAYS=$(( KEY_AGE_SECONDS / 86400 ))

    echo "  📅 키 생성 후 경과 시간: ${KEY_AGE_DAYS}일"
    echo "  📍 키 위치:"
    echo "    - $ACCESS_PRIVATE"
    echo "    - $ACCESS_PUBLIC"
    echo "    - $REFRESH_PRIVATE"
    echo "    - $REFRESH_PUBLIC"

    # 유효기간 체크
    if [ $KEY_AGE_DAYS -lt $KEY_VALIDITY_DAYS ]; then
        echo ""
        echo "✅ 키가 유효합니다 (${KEY_VALIDITY_DAYS}일 이내)"
        echo "   → 기존 키 재사용"
        exit 0
    else
        echo ""
        echo "⚠️  키가 ${KEY_VALIDITY_DAYS}일을 초과했습니다"
        echo "   하지만 기존 키를 계속 사용합니다"
        echo "   (자동 갱신 시 모든 사용자 로그아웃 발생)"
        echo ""
        echo "💡 키를 수동으로 갱신하려면:"
        echo "   1. 기존 키 삭제: rm -rf ./keys/jwt/*"
        echo "   2. 컨테이너 재시작: docker-compose restart"
        echo ""
        echo "⚠️  키 갱신 시 주의사항:"
        echo "   - 모든 발급된 JWT 토큰이 무효화됩니다"
        echo "   - 모든 사용자가 강제 로그아웃됩니다"
        echo "   - 키 갱신은 점검 시간에 수행하는 것을 권장합니다"
        exit 0
    fi
fi

# 새 키 생성
echo ""
echo "🔑 새로운 JWT RSA 키 생성 시작..."
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "1. Access Token용 RSA 키 생성 (2048 bits)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Access Token Private Key (2048 bits for RS256)
openssl genrsa -out "$ACCESS_PRIVATE" 2048 2>/dev/null

# Access Token Public Key
openssl rsa -in "$ACCESS_PRIVATE" -pubout -out "$ACCESS_PUBLIC" 2>/dev/null

echo "✅ Access Token 키 생성 완료"
echo "  Private: $ACCESS_PRIVATE"
echo "  Public:  $ACCESS_PUBLIC"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "2. Refresh Token용 RSA 키 생성 (4096 bits)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Refresh Token Private Key (4096 bits for RS512)
openssl genrsa -out "$REFRESH_PRIVATE" 4096 2>/dev/null

# Refresh Token Public Key
openssl rsa -in "$REFRESH_PRIVATE" -pubout -out "$REFRESH_PUBLIC" 2>/dev/null

echo "✅ Refresh Token 키 생성 완료"
echo "  Private: $REFRESH_PRIVATE"
echo "  Public:  $REFRESH_PUBLIC"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ JWT RSA 키 생성 완료!"
echo ""
echo "📌 보안 권장사항:"
echo "  1. keys/jwt/ 폴더를 .gitignore에 추가"
echo "  2. 백업: keys/jwt/ 폴더를 안전한 곳에 백업"
echo "  3. Private Key는 절대 노출하지 마세요"
echo "  4. ${KEY_VALIDITY_DAYS}일마다 키 로테이션 계획 수립"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"