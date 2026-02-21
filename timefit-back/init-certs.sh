#!/bin/sh
# SSL 인증서 자동 생성 스크립트
# docker-compose 시작 시 자동 실행

set -e

CERT_DIR="/etc/nginx/ssl"
CERT_FILE="$CERT_DIR/fullchain.pem"
KEY_FILE="$CERT_DIR/privkey.pem"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "SSL 인증서 초기화"
echo "Profile: ${SPRING_PROFILES_ACTIVE}"
echo "Domain: ${NGINX_DOMAIN_NAME}"
echo "HTTPS: ${NGINX_HTTPS_ENABLED}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# HTTPS가 비활성화되어 있으면 종료
if [ "${NGINX_HTTPS_ENABLED}" != "true" ]; then
    echo "✓ HTTPS 비활성화 - 인증서 생성 건너뜀"
    exit 0
fi

# 인증서 디렉토리 생성
mkdir -p "$CERT_DIR"

# dev 프로필: Self-Signed 인증서 자동 생성 (항상 재생성!)
if [ "${SPRING_PROFILES_ACTIVE}" = "dev" ]; then
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    # 기존 인증서 확인
    if [ -f "$CERT_FILE" ] && [ -f "$KEY_FILE" ]; then
        echo "Self-Signed 인증서 재생성 중... (기존 파일 덮어쓰기)"
    else
        echo "Self-Signed 인증서 생성 중..."
    fi

    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    # OpenSSL 설치 확인
    if ! command -v openssl > /dev/null 2>&1; then
        echo "OpenSSL 설치 중..."
        apk add --no-cache openssl
    fi

    # Self-Signed 인증서 생성 (덮어쓰기)
    openssl req -x509 -nodes -days 365 \
        -newkey rsa:2048 \
        -keyout "$KEY_FILE" \
        -out "$CERT_FILE" \
        -subj "/C=KR/ST=Seoul/L=Seoul/O=Timefit/OU=Dev/CN=${NGINX_DOMAIN_NAME}"

    echo ""
    echo "✅ Self-Signed 인증서 생성 완료!"
    echo "  $CERT_FILE"
    echo "  $KEY_FILE"
    echo ""
    echo "⚠️  브라우저 경고 (정상):"
    echo "  '안전하지 않음' → '고급' → '계속 진행'"

# prod 프로필: 인증서 확인 후 없으면 생성
elif [ "${SPRING_PROFILES_ACTIVE}" = "prod" ]; then

    # 기존 인증서가 있으면 그대로 사용
    if [ -f "$CERT_FILE" ] && [ -f "$KEY_FILE" ]; then
        echo "✓ 인증서가 이미 존재합니다"
        echo "  $CERT_FILE"
        echo "  $KEY_FILE"
        exit 0
    fi

    # localhost 또는 도메인에 점이 없으면 Self-Signed 인증서 생성
    case "${NGINX_DOMAIN_NAME}" in
        localhost|*.localhost|*[!.]*)
            # 점이 포함된 실제 도메인인지 확인
            if echo "${NGINX_DOMAIN_NAME}" | grep -q '\.'; then
                IS_REAL_DOMAIN=true
            else
                IS_REAL_DOMAIN=false
            fi
            ;;
        *)
            IS_REAL_DOMAIN=true
            ;;
    esac

    if [ "$IS_REAL_DOMAIN" = "false" ]; then
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "Self-Signed 인증서 생성 중... (localhost)"
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

        if ! command -v openssl > /dev/null 2>&1; then
            echo "OpenSSL 설치 중..."
            apk add --no-cache openssl
        fi

        openssl req -x509 -nodes -days 365 \
            -newkey rsa:2048 \
            -keyout "$KEY_FILE" \
            -out "$CERT_FILE" \
            -subj "/C=KR/ST=Seoul/L=Seoul/O=Timefit/OU=Prod/CN=${NGINX_DOMAIN_NAME}"

        echo ""
        echo "✅ Self-Signed 인증서 생성 완료!"
        echo "  $CERT_FILE"
        echo "  $KEY_FILE"
    else
        echo ""
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        echo "Let's Encrypt 인증서 발급 중..."
        echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    # Certbot 설치 확인
        if ! command -v certbot > /dev/null 2>&1; then
            echo "Certbot 설치 중..."
            apk add --no-cache certbot
        fi

    # 임시로 Nginx 중지 (포트 80 사용을 위해)
        echo "포트 80을 확보하기 위해 잠시 대기 중..."
        sleep 2

    # Let's Encrypt 인증서 발급
        certbot certonly --standalone \
            -d "${NGINX_DOMAIN_NAME}" \
            --email "${LETSENCRYPT_EMAIL}" \
            --agree-tos \
            --no-eff-email \
            --non-interactive \
            --preferred-challenges http

    # 인증서 복사
        LETSENCRYPT_DIR="/etc/letsencrypt/live/${NGINX_DOMAIN_NAME}"
        if [ -d "$LETSENCRYPT_DIR" ]; then
            cp "$LETSENCRYPT_DIR/fullchain.pem" "$CERT_FILE"
            cp "$LETSENCRYPT_DIR/privkey.pem" "$KEY_FILE"

            echo ""
            echo "✅ Let's Encrypt 인증서 발급 완료!"
            echo "  $CERT_FILE"
            echo "  $KEY_FILE"
        echo ""
        echo "🔒 브라우저에서 신뢰됨 (경고 없음)"
        else
            echo ""
            echo "❌ 인증서 발급 실패!"
        echo ""
            echo "가능한 원인:"
            echo "1. 도메인 DNS 설정 확인: ${NGINX_DOMAIN_NAME}"
            echo "2. 포트 80이 외부에서 접근 가능한지 확인"
            echo "3. 방화벽 설정 확인"
        echo ""
            exit 1
        fi
    fi
else
    echo "❌ 알 수 없는 프로필: ${SPRING_PROFILES_ACTIVE}"
    echo "  .env에서 SPRING_PROFILES_ACTIVE를 'dev' 또는 'prod'로 설정하세요"
    exit 1
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"