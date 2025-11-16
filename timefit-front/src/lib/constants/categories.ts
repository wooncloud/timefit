import type { BusinessTypeCode, ServiceCategoryCode } from '@/types/product/product';

/**
 * 서비스 카테고리 정보
 */
export interface CategoryInfo {
  code: ServiceCategoryCode;
  businessType: BusinessTypeCode;
  displayName: string;
  description: string;
}

/**
 * BusinessTypeCode별 ServiceCategoryCode 매핑
 * 백엔드 ServiceCategoryCode.java와 동일한 구조
 */
export const CATEGORY_BY_BUSINESS_TYPE: Record<
  BusinessTypeCode,
  CategoryInfo[]
> = {
  // BD000: 음식점
  BD000: [
    { code: 'RESTAURANT_KOREAN', businessType: 'BD000', displayName: '한식', description: '한국 전통 음식' },
    { code: 'RESTAURANT_CHINESE', businessType: 'BD000', displayName: '중식', description: '중국 음식' },
    { code: 'RESTAURANT_JAPANESE', businessType: 'BD000', displayName: '일식', description: '일본 음식' },
    { code: 'RESTAURANT_WESTERN', businessType: 'BD000', displayName: '양식', description: '서양 음식' },
    { code: 'RESTAURANT_ASIAN', businessType: 'BD000', displayName: '아시안', description: '동남아/인도 등 아시아 음식' },
    { code: 'RESTAURANT_FUSION', businessType: 'BD000', displayName: '퓨전', description: '퓨전 요리' },
    { code: 'RESTAURANT_BUFFET', businessType: 'BD000', displayName: '뷔페', description: '뷔페 레스토랑' },
    { code: 'RESTAURANT_FAST_FOOD', businessType: 'BD000', displayName: '패스트푸드', description: '패스트푸드' },
    { code: 'RESTAURANT_SNACK', businessType: 'BD000', displayName: '분식', description: '분식류' },
  ],

  // BD001: 카페
  BD001: [
    { code: 'CAFE_COFFEE', businessType: 'BD001', displayName: '커피전문점', description: '커피 전문 카페' },
    { code: 'CAFE_DESSERT', businessType: 'BD001', displayName: '디저트카페', description: '디저트 중심 카페' },
    { code: 'CAFE_BAKERY', businessType: 'BD001', displayName: '베이커리', description: '베이커리 카페' },
    { code: 'CAFE_TEA', businessType: 'BD001', displayName: '티카페', description: '차 전문 카페' },
    { code: 'CAFE_BRUNCH', businessType: 'BD001', displayName: '브런치카페', description: '브런치 전문 카페' },
    { code: 'CAFE_THEME', businessType: 'BD001', displayName: '테마카페', description: '특정 테마 카페' },
  ],

  // BD002: 숙박
  BD002: [
    { code: 'ACCOMMODATION_HOTEL', businessType: 'BD002', displayName: '호텔', description: '일반 호텔' },
    { code: 'ACCOMMODATION_RESORT', businessType: 'BD002', displayName: '리조트', description: '리조트' },
    { code: 'ACCOMMODATION_PENSION', businessType: 'BD002', displayName: '펜션', description: '펜션' },
    { code: 'ACCOMMODATION_MOTEL', businessType: 'BD002', displayName: '모텔', description: '모텔' },
    { code: 'ACCOMMODATION_GUESTHOUSE', businessType: 'BD002', displayName: '게스트하우스', description: '게스트하우스' },
    { code: 'ACCOMMODATION_HANOK', businessType: 'BD002', displayName: '한옥', description: '한옥 스테이' },
    { code: 'ACCOMMODATION_CAMPING', businessType: 'BD002', displayName: '캠핑/글램핑', description: '캠핑장/글램핑' },
  ],

  // BD003: 공연/전시
  BD003: [
    { code: 'CULTURE_CONCERT', businessType: 'BD003', displayName: '콘서트', description: '음악 콘서트' },
    { code: 'CULTURE_MUSICAL', businessType: 'BD003', displayName: '뮤지컬', description: '뮤지컬 공연' },
    { code: 'CULTURE_PLAY', businessType: 'BD003', displayName: '연극', description: '연극 공연' },
    { code: 'CULTURE_EXHIBITION', businessType: 'BD003', displayName: '전시회', description: '전시회' },
    { code: 'CULTURE_GALLERY', businessType: 'BD003', displayName: '갤러리', description: '갤러리' },
    { code: 'CULTURE_FESTIVAL', businessType: 'BD003', displayName: '페스티벌', description: '페스티벌/축제' },
    { code: 'CULTURE_SHOW', businessType: 'BD003', displayName: '쇼/공연', description: '각종 쇼 및 공연' },
  ],

  // BD004: 스포츠/오락
  BD004: [
    { code: 'SPORTS_FITNESS', businessType: 'BD004', displayName: '헬스장', description: '헬스 트레이닝' },
    { code: 'SPORTS_PILATES', businessType: 'BD004', displayName: '필라테스', description: '필라테스' },
    { code: 'SPORTS_YOGA', businessType: 'BD004', displayName: '요가', description: '요가' },
    { code: 'SPORTS_PT', businessType: 'BD004', displayName: 'PT', description: '개인 트레이닝' },
    { code: 'SPORTS_SWIMMING', businessType: 'BD004', displayName: '수영장', description: '수영' },
    { code: 'SPORTS_GOLF', businessType: 'BD004', displayName: '골프', description: '골프 연습장/필드' },
    { code: 'SPORTS_CLIMBING', businessType: 'BD004', displayName: '클라이밍', description: '클라이밍' },
    { code: 'SPORTS_BOWLING', businessType: 'BD004', displayName: '볼링', description: '볼링장' },
    { code: 'SPORTS_BILLIARDS', businessType: 'BD004', displayName: '당구', description: '당구장' },
    { code: 'SPORTS_SCREEN_GOLF', businessType: 'BD004', displayName: '스크린골프', description: '스크린골프' },
    { code: 'SPORTS_BASEBALL', businessType: 'BD004', displayName: '야구', description: '야구장/배팅센터' },
    { code: 'SPORTS_TENNIS', businessType: 'BD004', displayName: '테니스', description: '테니스장' },
    { code: 'SPORTS_BADMINTON', businessType: 'BD004', displayName: '배드민턴', description: '배드민턴장' },
    { code: 'SPORTS_FOOTBALL', businessType: 'BD004', displayName: '축구', description: '축구장' },
  ],

  // BD005: 레저/체험
  BD005: [
    { code: 'LEISURE_THEME_PARK', businessType: 'BD005', displayName: '테마파크', description: '테마파크' },
    { code: 'LEISURE_WATER_PARK', businessType: 'BD005', displayName: '워터파크', description: '워터파크' },
    { code: 'LEISURE_AMUSEMENT', businessType: 'BD005', displayName: '놀이공원', description: '놀이공원' },
    { code: 'LEISURE_ESCAPE_ROOM', businessType: 'BD005', displayName: '방탈출', description: '방탈출 카페' },
    { code: 'LEISURE_VR', businessType: 'BD005', displayName: 'VR체험', description: 'VR 체험' },
    { code: 'LEISURE_KARAOKE', businessType: 'BD005', displayName: '노래방', description: '노래방' },
    { code: 'LEISURE_PC_CAFE', businessType: 'BD005', displayName: 'PC방', description: 'PC방' },
    { code: 'LEISURE_BOARD_GAME', businessType: 'BD005', displayName: '보드게임', description: '보드게임 카페' },
    { code: 'LEISURE_COOKING', businessType: 'BD005', displayName: '쿠킹클래스', description: '요리 체험' },
    { code: 'LEISURE_CRAFT', businessType: 'BD005', displayName: '공예체험', description: '공예/만들기 체험' },
    { code: 'LEISURE_FARM', businessType: 'BD005', displayName: '농장체험', description: '농장 체험' },
  ],

  // BD006: 여행/명소
  BD006: [
    { code: 'TRAVEL_TOURIST_SPOT', businessType: 'BD006', displayName: '관광명소', description: '관광 명소' },
    { code: 'TRAVEL_MUSEUM', businessType: 'BD006', displayName: '박물관', description: '박물관' },
    { code: 'TRAVEL_HISTORIC_SITE', businessType: 'BD006', displayName: '유적지', description: '역사 유적지' },
    { code: 'TRAVEL_TEMPLE', businessType: 'BD006', displayName: '사찰', description: '사찰' },
    { code: 'TRAVEL_PALACE', businessType: 'BD006', displayName: '궁궐', description: '궁궐' },
    { code: 'TRAVEL_OBSERVATORY', businessType: 'BD006', displayName: '전망대', description: '전망대' },
    { code: 'TRAVEL_ZOO', businessType: 'BD006', displayName: '동물원', description: '동물원' },
    { code: 'TRAVEL_AQUARIUM', businessType: 'BD006', displayName: '아쿠아리움', description: '아쿠아리움' },
    { code: 'TRAVEL_BOTANICAL_GARDEN', businessType: 'BD006', displayName: '식물원', description: '식물원' },
  ],

  // BD007: 건강/의료
  BD007: [
    { code: 'MEDICAL_HOSPITAL', businessType: 'BD007', displayName: '병원', description: '일반 병원' },
    { code: 'MEDICAL_CLINIC', businessType: 'BD007', displayName: '의원', description: '의원' },
    { code: 'MEDICAL_DENTAL', businessType: 'BD007', displayName: '치과', description: '치과' },
    { code: 'MEDICAL_ORIENTAL', businessType: 'BD007', displayName: '한의원', description: '한의원' },
    { code: 'MEDICAL_PHARMACY', businessType: 'BD007', displayName: '약국', description: '약국' },
    { code: 'MEDICAL_HEALTH_CHECK', businessType: 'BD007', displayName: '건강검진', description: '건강검진센터' },
    { code: 'MEDICAL_THERAPY', businessType: 'BD007', displayName: '물리치료', description: '물리치료' },
    { code: 'MEDICAL_MENTAL', businessType: 'BD007', displayName: '정신건강', description: '정신건강의학과/상담센터' },
  ],

  // BD008: 뷰티
  BD008: [
    { code: 'BEAUTY_HAIR_CUT', businessType: 'BD008', displayName: '커트', description: '헤어 커트' },
    { code: 'BEAUTY_HAIR_PERM', businessType: 'BD008', displayName: '펌', description: '펌 시술' },
    { code: 'BEAUTY_HAIR_COLORING', businessType: 'BD008', displayName: '염색', description: '염색 서비스' },
    { code: 'BEAUTY_HAIR_STYLING', businessType: 'BD008', displayName: '스타일링', description: '헤어 스타일링' },
    { code: 'BEAUTY_HAIR_TREATMENT', businessType: 'BD008', displayName: '트리트먼트', description: '모발 케어' },
    { code: 'BEAUTY_SCALP_CARE', businessType: 'BD008', displayName: '두피케어', description: '두피 관리' },
    { code: 'BEAUTY_NAIL', businessType: 'BD008', displayName: '네일', description: '네일아트' },
    { code: 'BEAUTY_SKIN_CARE', businessType: 'BD008', displayName: '피부관리', description: '피부관리샵' },
    { code: 'BEAUTY_MAKEUP', businessType: 'BD008', displayName: '메이크업', description: '메이크업' },
    { code: 'BEAUTY_SPA', businessType: 'BD008', displayName: '스파', description: '스파/마사지' },
    { code: 'BEAUTY_WAXING', businessType: 'BD008', displayName: '왁싱', description: '제모/왁싱' },
    { code: 'BEAUTY_TATTOO', businessType: 'BD008', displayName: '타투/반영구', description: '타투/반영구화장' },
  ],

  // BD009: 생활/편의
  BD009: [
    { code: 'LIFE_LAUNDRY', businessType: 'BD009', displayName: '세탁', description: '세탁소' },
    { code: 'LIFE_CLEANING', businessType: 'BD009', displayName: '청소', description: '청소 서비스' },
    { code: 'LIFE_REPAIR', businessType: 'BD009', displayName: '수리', description: '수리 서비스' },
    { code: 'LIFE_MOVING', businessType: 'BD009', displayName: '이사', description: '이사 서비스' },
    { code: 'LIFE_PET_CARE', businessType: 'BD009', displayName: '반려동물', description: '반려동물 케어' },
    { code: 'LIFE_PET_HOTEL', businessType: 'BD009', displayName: '애견호텔', description: '애견 호텔' },
    { code: 'LIFE_PHOTO_STUDIO', businessType: 'BD009', displayName: '사진관', description: '사진 스튜디오' },
    { code: 'LIFE_PRINTING', businessType: 'BD009', displayName: '인쇄', description: '인쇄/복사' },
    { code: 'LIFE_COURIER', businessType: 'BD009', displayName: '택배', description: '택배 서비스' },
    { code: 'LIFE_STORAGE', businessType: 'BD009', displayName: '보관', description: '창고/보관 서비스' },
  ],

  // BD010: 쇼핑/유통
  BD010: [
    { code: 'RETAIL_FASHION', businessType: 'BD010', displayName: '패션/의류', description: '의류 판매' },
    { code: 'RETAIL_COSMETICS', businessType: 'BD010', displayName: '화장품', description: '화장품 판매' },
    { code: 'RETAIL_ELECTRONICS', businessType: 'BD010', displayName: '전자제품', description: '전자제품 판매' },
    { code: 'RETAIL_FOOD', businessType: 'BD010', displayName: '식품', description: '식품 판매' },
    { code: 'RETAIL_BOOKS', businessType: 'BD010', displayName: '서적/문구', description: '서적 및 문구' },
    { code: 'RETAIL_FURNITURE', businessType: 'BD010', displayName: '가구/인테리어', description: '가구 및 인테리어' },
    { code: 'RETAIL_SPORTS_GOODS', businessType: 'BD010', displayName: '스포츠용품', description: '스포츠 용품' },
    { code: 'RETAIL_ACCESSORIES', businessType: 'BD010', displayName: '액세서리', description: '액세서리/잡화' },
    { code: 'RETAIL_GIFT', businessType: 'BD010', displayName: '선물/기념품', description: '선물 및 기념품' },
  ],

  // BD011: 장소 대여
  BD011: [
    { code: 'RENTAL_PARTY_ROOM', businessType: 'BD011', displayName: '파티룸', description: '파티룸 대여' },
    { code: 'RENTAL_STUDY_ROOM', businessType: 'BD011', displayName: '스터디룸', description: '스터디룸 대여' },
    { code: 'RENTAL_MEETING_ROOM', businessType: 'BD011', displayName: '회의실', description: '회의실 대여' },
    { code: 'RENTAL_STUDIO', businessType: 'BD011', displayName: '스튜디오', description: '촬영/연습 스튜디오' },
    { code: 'RENTAL_PRACTICE_ROOM', businessType: 'BD011', displayName: '연습실', description: '음악/댄스 연습실' },
    { code: 'RENTAL_KITCHEN', businessType: 'BD011', displayName: '공유주방', description: '공유 주방' },
    { code: 'RENTAL_SPACE', businessType: 'BD011', displayName: '다목적공간', description: '다목적 공간 대여' },
    { code: 'RENTAL_OUTDOOR', businessType: 'BD011', displayName: '야외공간', description: '야외 공간 대여' },
  ],

  // BD012: 자연
  BD012: [
    { code: 'NATURE_PARK', businessType: 'BD012', displayName: '공원', description: '공원' },
    { code: 'NATURE_MOUNTAIN', businessType: 'BD012', displayName: '산/등산로', description: '등산로' },
    { code: 'NATURE_BEACH', businessType: 'BD012', displayName: '해변', description: '해변/해수욕장' },
    { code: 'NATURE_LAKE', businessType: 'BD012', displayName: '호수', description: '호수' },
    { code: 'NATURE_RIVER', businessType: 'BD012', displayName: '강/계곡', description: '강/계곡' },
    { code: 'NATURE_FOREST', businessType: 'BD012', displayName: '숲', description: '숲/수목원' },
    { code: 'NATURE_ISLAND', businessType: 'BD012', displayName: '섬', description: '섬' },
    { code: 'NATURE_CAVE', businessType: 'BD012', displayName: '동굴', description: '동굴' },
    { code: 'NATURE_WATERFALL', businessType: 'BD012', displayName: '폭포', description: '폭포' },
  ],

  // BD013: 기타
  BD013: [
    { code: 'ETC_EDUCATION', businessType: 'BD013', displayName: '교육', description: '학원/교육기관' },
    { code: 'ETC_CONSULTING', businessType: 'BD013', displayName: '컨설팅', description: '컨설팅 서비스' },
    { code: 'ETC_LEGAL', businessType: 'BD013', displayName: '법률', description: '법률 상담' },
    { code: 'ETC_ACCOUNTING', businessType: 'BD013', displayName: '회계', description: '회계/세무' },
    { code: 'ETC_DESIGN', businessType: 'BD013', displayName: '디자인', description: '디자인 서비스' },
    { code: 'ETC_IT', businessType: 'BD013', displayName: 'IT서비스', description: 'IT 서비스' },
    { code: 'ETC_MANUFACTURING', businessType: 'BD013', displayName: '제조', description: '제조/생산' },
    { code: 'ETC_LOGISTICS', businessType: 'BD013', displayName: '물류', description: '물류/유통' },
    { code: 'ETC_REAL_ESTATE', businessType: 'BD013', displayName: '부동산', description: '부동산 중개' },
    { code: 'ETC_FINANCE', businessType: 'BD013', displayName: '금융', description: '금융 서비스' },
    { code: 'ETC_INSURANCE', businessType: 'BD013', displayName: '보험', description: '보험 서비스' },
    { code: 'ETC_OTHER', businessType: 'BD013', displayName: '기타', description: '기타 서비스' },
  ],
};

/**
 * BusinessTypeCode 표시명
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
 * ServiceCategoryCode로 CategoryInfo 조회
 */
export function getCategoryInfo(
  categoryCode: ServiceCategoryCode
): CategoryInfo | undefined {
  for (const categories of Object.values(CATEGORY_BY_BUSINESS_TYPE)) {
    const found = categories.find((cat) => cat.code === categoryCode);
    if (found) return found;
  }
  return undefined;
}

/**
 * BusinessTypeCode에 해당하는 카테고리 목록 조회
 */
export function getCategoriesByBusinessType(
  businessType: BusinessTypeCode
): CategoryInfo[] {
  return CATEGORY_BY_BUSINESS_TYPE[businessType] || [];
}
