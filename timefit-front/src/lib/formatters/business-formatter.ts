/**
 * 사업자 번호를 123-45-67890 형식으로 포맷.
 */
export function formatBusinessNumber(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 10);

  if (digits.length <= 3) {
    return digits;
  }

  if (digits.length <= 5) {
    return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  }

  return `${digits.slice(0, 3)}-${digits.slice(3, 5)}-${digits.slice(5)}`;
}

/**
 * 전화번호를 지역번호-국번호-가입자번호 형식으로 포맷.
 */
export function formatContactPhone(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 11);

  // 서울 지역번호 (02)
  if (digits.startsWith('02')) {
    if (digits.length <= 2) {
      return digits;
    }

    if (digits.length <= 6) {
      return `${digits.slice(0, 2)}-${digits.slice(2)}`;
    }

    if (digits.length <= 10) {
      return `${digits.slice(0, 2)}-${digits.slice(2, digits.length - 4)}-${digits.slice(-4)}`;
    }

    return `${digits.slice(0, 2)}-${digits.slice(2, 6)}-${digits.slice(6)}`;
  }

  // 기타 지역번호 (031, 032 등)
  if (digits.length <= 3) {
    return digits;
  }

  if (digits.length <= 7) {
    return `${digits.slice(0, 3)}-${digits.slice(3)}`;
  }

  return `${digits.slice(0, 3)}-${digits.slice(3, digits.length - 4)}-${digits.slice(-4)}`;
}

/**
 * 업종 코드 타입
 */
export type BusinessTypeCode =
  | 'BD000'
  | 'BD001'
  | 'BD002'
  | 'BD003'
  | 'BD004'
  | 'BD005'
  | 'BD006'
  | 'BD007'
  | 'BD008'
  | 'BD009'
  | 'BD010'
  | 'BD011'
  | 'BD012'
  | 'BD013';

/**
 * 업종 코드별 한글 설명 매핑
 */
export const BUSINESS_TYPE_NAMES: Record<BusinessTypeCode, string> = {
  BD000: '음식점',
  BD001: '카페',
  BD002: '숙박',
  BD003: '공연/전시',
  BD004: '스포츠/오락',
  BD005: '레저/체험',
  BD006: '여행/명소',
  BD007: '건강/의료',
  BD008: '뷰티',
  BD009: '생활/편의',
  BD010: '쇼핑/유통',
  BD011: '장소 대여',
  BD012: '자연',
  BD013: '기타',
};

/**
 * 업종 코드를 한글 이름으로 변환
 */
export function getBusinessTypeName(code: BusinessTypeCode): string {
  return BUSINESS_TYPE_NAMES[code] || code;
}

/**
 * 업종 코드 배열을 한글 이름 배열로 변환
 */
export function getBusinessTypeNames(codes: BusinessTypeCode[]): string[] {
  return codes.map(code => getBusinessTypeName(code));
}
