#!/bin/bash

# 색상 정의
BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 스피너 문자
SPINNER='⠋⠙⠹⠸⠼⠴⠦⠧⠇⠏'

# 스피너 함수
spin() {
    local pid=$1
    local message=$2
    local i=0
    
    while kill -0 $pid 2>/dev/null; do
        i=$(( (i+1) % 10 ))
        printf "\r${BLUE}${SPINNER:$i:1}${NC} ${message}..."
        sleep 0.1
    done
    
    wait $pid
    return $?
}

echo ""
echo "🎨 코드 포맷팅 및 린트 시작..."
echo ""

# Prettier 실행
printf "${BLUE}⠋${NC} Prettier 실행 중..."
prettier --write . --log-level warn > /tmp/prettier.log 2>&1 &
PRETTIER_PID=$!
spin $PRETTIER_PID "Prettier 실행 중"
PRETTIER_EXIT=$?

if [ $PRETTIER_EXIT -eq 0 ]; then
    printf "\r${GREEN}✓${NC} Prettier 완료          \n"
else
    printf "\r${RED}✗${NC} Prettier 실패          \n"
    cat /tmp/prettier.log
    exit 1
fi

# ESLint 실행
printf "${BLUE}⠋${NC} ESLint 실행 중..."
next lint --fix > /tmp/eslint.log 2>&1 &
ESLINT_PID=$!
spin $ESLINT_PID "ESLint 실행 중"
ESLINT_EXIT=$?

if [ $ESLINT_EXIT -eq 0 ]; then
    printf "\r${GREEN}✓${NC} ESLint 완료           \n"
else
    printf "\r${RED}✗${NC} ESLint 실패           \n"
    cat /tmp/eslint.log
    exit 1
fi

# TypeScript 타입 체크
printf "${BLUE}⠋${NC} TypeScript 타입 체크 중..."
tsc --noEmit > /tmp/typecheck.log 2>&1 &
TYPECHECK_PID=$!
spin $TYPECHECK_PID "TypeScript 타입 체크 중"
TYPECHECK_EXIT=$?

if [ $TYPECHECK_EXIT -eq 0 ]; then
    printf "\r${GREEN}✓${NC} TypeScript 타입 체크 완료\n"
    printf "\n"
    printf "${GREEN}✨ 모든 작업이 완료되었습니다!${NC}\n"
    printf "\n"
else
    printf "\r${RED}✗${NC} TypeScript 타입 에러 발견\n"
    printf "\n"
    cat /tmp/typecheck.log
    exit 1
fi

