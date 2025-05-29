---
layout: home

hero:
  name: "TimeFit"
  text: "개발 문서"
  tagline: 예약 시스템 개발을 위한 기술 문서와 가이드
  actions:
    - theme: brand
      text: 기술 스택 보기
      link: /tech-stack
    - theme: alt
      text: 아키텍처
      link: /architecture

features:
  - title: 🛠 개발자 중심
    details: 기술적 의사결정, 정책, 트러블슈팅 등 개발 과정의 모든 것을 문서화
  - title: 📊 체계적 관리
    details: Spring Boot + SvelteKit + Supabase로 구성된 모던 예약 시스템
  - title: 🚀 빠른 개발
    details: MVP 접근으로 핵심 기능부터 개발하여 빠른 검증과 개선
---

## 🎯 프로젝트 개요

TimeFit은 사업자와 고객을 연결하는 예약 관리 SaaS 플랫폼입니다.

### 핵심 가치
- **효율성**: 사업자의 예약 관리 업무 최적화
- **편의성**: 고객의 직관적인 예약 경험
- **소통**: 실시간 채팅을 통한 원활한 커뮤니케이션

### 주요 기능
- **예약 시스템**: 생성, 수정, 취소, 상태 관리
- **사업자 도구**: 예약 시간 설정, 메뉴 관리, 대시보드
- **실시간 채팅**: 사업자-고객 간 1:1 커뮤니케이션
- **OAuth 인증**: Google, Kakao, Apple 소셜 로그인

## 📚 문서 구성

이 문서는 TimeFit 개발팀이 사용하는 기술적 지식과 의사결정 과정을 기록합니다:

- **기술 선택의 이유**와 트레이드오프
- **설계 패턴**과 아키텍처 결정사항  
- **API 명세**와 데이터 모델
- **개발 과정의 이슈**와 해결 방법

## 🏃‍♂️ 빠른 시작

```bash
# 문서 로컬 실행
cd page
npm install
npm run docs:dev
```

개발 진행에 따라 지속적으로 업데이트됩니다. 