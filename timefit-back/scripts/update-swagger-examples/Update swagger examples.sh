#!/bin/bash
# ================================================================
# Swagger Example 자동 수정 스크립트 (Bash)
# 작성자: 세창
# 버전: 1.0
# 설명: mappings.json 기반으로 Swagger annotation의 example 값 자동 치환
# ================================================================

set -e  # 오류 발생시 즉시 종료

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
GRAY='\033[0;37m'
NC='\033[0m' # No Color

# 실행 디렉토리 확인
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SCRIPTS_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$SCRIPTS_DIR")"
cd "$PROJECT_ROOT"

echo -e "${CYAN}=====================================${NC}"
echo -e "${CYAN}Swagger Example 자동 수정 시작${NC}"
echo -e "${CYAN}=====================================${NC}"
echo ""

# 1. 매핑 파일 로드
MAPPINGS_PATH="$SCRIPT_DIR/mappings.json"
if [ ! -f "$MAPPINGS_PATH" ]; then
    echo -e "${RED}❌ 오류: mappings.json 파일을 찾을 수 없습니다.${NC}"
    echo -e "${YELLOW}경로: $MAPPINGS_PATH${NC}"
    exit 1
fi

echo -e "${GREEN}📋 매핑 파일 로드 중...${NC}"

# jq 설치 확인
if ! command -v jq &> /dev/null; then
    echo -e "${RED}❌ 오류: jq가 설치되어 있지 않습니다.${NC}"
    echo -e "${YELLOW}설치 방법:${NC}"
    echo -e "  Mac: brew install jq"
    echo -e "  Ubuntu: sudo apt-get install jq"
    exit 1
fi

# 2. 대상 디렉토리 확인 (전체 timefit 패키지)
TARGET_DIR="web/src/main/java/timefit"
if [ ! -d "$TARGET_DIR" ]; then
    echo -e "${RED}❌ 오류: timefit 디렉토리를 찾을 수 없습니다.${NC}"
    echo -e "${YELLOW}경로: $TARGET_DIR${NC}"
    exit 1
fi

# 3. 백업 생성
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="swagger-backup/$TIMESTAMP"
echo -e "${YELLOW}💾 백업 생성 중: $BACKUP_DIR${NC}"

mkdir -p "$BACKUP_DIR"
cp -r "$SWAGGER_DIR" "$BACKUP_DIR/"
echo -e "${GREEN}✅ 백업 완료!${NC}"
echo ""

# 4. Java 파일 검색
echo -e "${GREEN}🔍 Java 파일 검색 중...${NC}"
JAVA_FILES=$(find "$TARGET_DIR" -name "*.java")
TOTAL_FILES=$(echo "$JAVA_FILES" | wc -l)
echo -e "${CYAN}📁 찾은 파일 개수: $TOTAL_FILES${NC}"
echo ""

# 5. 치환 실행
echo -e "${GREEN}🔧 파일 수정 중...${NC}"
MODIFIED_COUNT=0

# 매핑 데이터 추출
EMAILS=$(jq -r '.emails | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
PASSWORDS=$(jq -r '.passwords | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
UUIDS=$(jq -r '.uuids | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
PHONES=$(jq -r '.phones | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
NAMES=$(jq -r '.names | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
ADDRESSES=$(jq -r '.addresses | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
DATES=$(jq -r '.dates | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
TIMES=$(jq -r '.times | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")
BUSINESS_NUMBERS=$(jq -r '.businessNumbers | to_entries[] | "\(.key)||||\(.value)"' "$MAPPINGS_PATH")

# 각 Java 파일 처리
while IFS= read -r file; do
    FILE_MODIFIED=false

    # 임시 파일 생성
    TMP_FILE="${file}.tmp"
    cp "$file" "$TMP_FILE"

    # 5.1 이메일 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$EMAILS"

    # 5.2 비밀번호 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$PASSWORDS"

    # 5.3 UUID 치환 (KEEP_AS_IS 제외)
    while IFS='||||' read -r key value; do
        if [ "$value" != "KEEP_AS_IS" ] && grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$UUIDS"

    # 5.4 전화번호 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$PHONES"

    # 5.5 이름 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$NAMES"

    # 5.6 주소 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$ADDRESSES"

    # 5.7 날짜 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$DATES"

    # 5.8 시간 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$TIMES"

    # 5.9 사업자번호 치환
    while IFS='||||' read -r key value; do
        if grep -q "$key" "$TMP_FILE"; then
            sed -i.bak "s|$key|$value|g" "$TMP_FILE"
            FILE_MODIFIED=true
        fi
    done <<< "$BUSINESS_NUMBERS"

    # 수정된 경우 원본 파일 대체
    if [ "$FILE_MODIFIED" = true ]; then
        mv "$TMP_FILE" "$file"
        MODIFIED_COUNT=$((MODIFIED_COUNT + 1))
        echo -e "  ${GRAY}✓ $(basename "$file")${NC}"
    else
        rm "$TMP_FILE"
    fi

    # .bak 파일 정리
    rm -f "${TMP_FILE}.bak"
    rm -f "${file}.bak"

done <<< "$JAVA_FILES"

# 6. 완료 메시지
echo ""
echo -e "${CYAN}=====================================${NC}"
echo -e "${GREEN}✅ 완료!${NC}"
echo -e "${CYAN}=====================================${NC}"
echo ""
echo -e "${YELLOW}📊 통계:${NC}"
echo -e "  - 전체 파일: $TOTAL_FILES"
echo -e "  ${GREEN}- 수정된 파일: $MODIFIED_COUNT${NC}"
echo -e "  ${CYAN}- 백업 위치: $BACKUP_DIR${NC}"
echo ""
echo -e "${YELLOW}🔍 다음 단계:${NC}"
echo -e "  ${NC}1. git diff로 변경 사항 확인${NC}"
echo -e "     ${GRAY}git diff web/src/main/java/timefit${NC}"
echo ""
echo -e "  ${NC}2. 서버 재시작 후 Swagger UI 확인${NC}"
echo -e "     ${GRAY}http://localhost:8080/swagger-ui/index.html${NC}"
echo ""
echo -e "  ${NC}3. Postman 재 import (필요시)${NC}"
echo -e "     ${GRAY}http://localhost:8080/v3/api-docs${NC}"
echo ""