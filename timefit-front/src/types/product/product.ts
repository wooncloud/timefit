// Business Type Codes (대분류)
export type BusinessTypeCode =
  | 'BD000' // 음식점
  | 'BD001' // 카페
  | 'BD002' // 숙박
  | 'BD003' // 공연/전시
  | 'BD004' // 스포츠/오락
  | 'BD005' // 레저/체험
  | 'BD006' // 여행/명소
  | 'BD007' // 건강/의료
  | 'BD008' // 뷰티
  | 'BD009' // 생활/편의
  | 'BD010' // 쇼핑/유통
  | 'BD011' // 장소 대여
  | 'BD012' // 자연
  | 'BD013'; // 기타

// Service Category Codes (중분류)
export type ServiceCategoryCode =
  // BD000: 음식점
  | 'RESTAURANT_KOREAN'
  | 'RESTAURANT_CHINESE'
  | 'RESTAURANT_JAPANESE'
  | 'RESTAURANT_WESTERN'
  | 'RESTAURANT_ASIAN'
  | 'RESTAURANT_FUSION'
  | 'RESTAURANT_BUFFET'
  | 'RESTAURANT_FAST_FOOD'
  | 'RESTAURANT_SNACK'
  // BD001: 카페
  | 'CAFE_COFFEE'
  | 'CAFE_DESSERT'
  | 'CAFE_BAKERY'
  | 'CAFE_TEA'
  | 'CAFE_BRUNCH'
  | 'CAFE_THEME'
  // BD002: 숙박
  | 'ACCOMMODATION_HOTEL'
  | 'ACCOMMODATION_RESORT'
  | 'ACCOMMODATION_PENSION'
  | 'ACCOMMODATION_MOTEL'
  | 'ACCOMMODATION_GUESTHOUSE'
  | 'ACCOMMODATION_HANOK'
  | 'ACCOMMODATION_CAMPING'
  // BD003: 공연/전시
  | 'CULTURE_CONCERT'
  | 'CULTURE_MUSICAL'
  | 'CULTURE_PLAY'
  | 'CULTURE_EXHIBITION'
  | 'CULTURE_GALLERY'
  | 'CULTURE_FESTIVAL'
  | 'CULTURE_SHOW'
  // BD004: 스포츠/오락
  | 'SPORTS_FITNESS'
  | 'SPORTS_PILATES'
  | 'SPORTS_YOGA'
  | 'SPORTS_PT'
  | 'SPORTS_SWIMMING'
  | 'SPORTS_GOLF'
  | 'SPORTS_CLIMBING'
  | 'SPORTS_BOWLING'
  | 'SPORTS_BILLIARDS'
  | 'SPORTS_SCREEN_GOLF'
  | 'SPORTS_BASEBALL'
  | 'SPORTS_TENNIS'
  | 'SPORTS_BADMINTON'
  | 'SPORTS_FOOTBALL'
  // BD005: 레저/체험
  | 'LEISURE_THEME_PARK'
  | 'LEISURE_WATER_PARK'
  | 'LEISURE_AMUSEMENT'
  | 'LEISURE_ESCAPE_ROOM'
  | 'LEISURE_VR'
  | 'LEISURE_KARAOKE'
  | 'LEISURE_PC_CAFE'
  | 'LEISURE_BOARD_GAME'
  | 'LEISURE_COOKING'
  | 'LEISURE_CRAFT'
  | 'LEISURE_FARM'
  // BD006: 여행/명소
  | 'TRAVEL_TOURIST_SPOT'
  | 'TRAVEL_MUSEUM'
  | 'TRAVEL_HISTORIC_SITE'
  | 'TRAVEL_TEMPLE'
  | 'TRAVEL_PALACE'
  | 'TRAVEL_OBSERVATORY'
  | 'TRAVEL_ZOO'
  | 'TRAVEL_AQUARIUM'
  | 'TRAVEL_BOTANICAL_GARDEN'
  // BD007: 건강/의료
  | 'MEDICAL_HOSPITAL'
  | 'MEDICAL_CLINIC'
  | 'MEDICAL_DENTAL'
  | 'MEDICAL_ORIENTAL'
  | 'MEDICAL_PHARMACY'
  | 'MEDICAL_HEALTH_CHECK'
  | 'MEDICAL_THERAPY'
  | 'MEDICAL_MENTAL'
  // BD008: 뷰티
  | 'BEAUTY_HAIR_CUT'
  | 'BEAUTY_HAIR_PERM'
  | 'BEAUTY_HAIR_COLORING'
  | 'BEAUTY_HAIR_STYLING'
  | 'BEAUTY_HAIR_TREATMENT'
  | 'BEAUTY_SCALP_CARE'
  | 'BEAUTY_NAIL'
  | 'BEAUTY_SKIN_CARE'
  | 'BEAUTY_MAKEUP'
  | 'BEAUTY_SPA'
  | 'BEAUTY_WAXING'
  | 'BEAUTY_TATTOO'
  // BD009: 생활/편의
  | 'LIFE_LAUNDRY'
  | 'LIFE_CLEANING'
  | 'LIFE_REPAIR'
  | 'LIFE_MOVING'
  | 'LIFE_PET_CARE'
  | 'LIFE_PET_HOTEL'
  | 'LIFE_PHOTO_STUDIO'
  | 'LIFE_PRINTING'
  | 'LIFE_COURIER'
  | 'LIFE_STORAGE'
  // BD010: 쇼핑/유통
  | 'RETAIL_FASHION'
  | 'RETAIL_COSMETICS'
  | 'RETAIL_ELECTRONICS'
  | 'RETAIL_FOOD'
  | 'RETAIL_BOOKS'
  | 'RETAIL_FURNITURE'
  | 'RETAIL_SPORTS_GOODS'
  | 'RETAIL_ACCESSORIES'
  | 'RETAIL_GIFT'
  // BD011: 장소 대여
  | 'RENTAL_PARTY_ROOM'
  | 'RENTAL_STUDY_ROOM'
  | 'RENTAL_MEETING_ROOM'
  | 'RENTAL_STUDIO'
  | 'RENTAL_PRACTICE_ROOM'
  | 'RENTAL_KITCHEN'
  | 'RENTAL_SPACE'
  | 'RENTAL_OUTDOOR'
  // BD012: 자연
  | 'NATURE_PARK'
  | 'NATURE_MOUNTAIN'
  | 'NATURE_BEACH'
  | 'NATURE_LAKE'
  | 'NATURE_RIVER'
  | 'NATURE_FOREST'
  | 'NATURE_ISLAND'
  | 'NATURE_CAVE'
  | 'NATURE_WATERFALL'
  // BD013: 기타
  | 'ETC_EDUCATION'
  | 'ETC_CONSULTING'
  | 'ETC_LEGAL'
  | 'ETC_ACCOUNTING'
  | 'ETC_DESIGN'
  | 'ETC_IT'
  | 'ETC_MANUFACTURING'
  | 'ETC_LOGISTICS'
  | 'ETC_REAL_ESTATE'
  | 'ETC_FINANCE'
  | 'ETC_INSURANCE'
  | 'ETC_OTHER';

// Order Type (예약 타입)
export type OrderType = 'RESERVATION_BASED' | 'ONDEMAND_BASED';

// 서비스(Product) - 백엔드 MenuResponse와 매핑
export interface Product {
  id: string; // menuId
  businessId: string;
  serviceName: string;
  businessCategoryId: string;
  businessType: BusinessTypeCode;
  categoryCode: ServiceCategoryCode;
  categoryName: string; // 표시용 카테고리명
  price: number;
  description?: string;
  orderType: OrderType;
  durationMinutes: number;
  imageUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

// API 응답 타입
export interface ProductListResponse {
  menus: Product[];
  totalCount: number;
}

export interface ProductResponse {
  data?: Product;
  message?: string;
}

// Handler 응답 타입 (Next.js API Route)
export interface GetProductListHandlerResponse {
  success: boolean;
  data?: ProductListResponse;
  message?: string;
}

export interface GetProductDetailHandlerResponse {
  success: boolean;
  data?: Product;
  message?: string;
}

export interface DeleteProductHandlerResponse {
  success: boolean;
  message?: string;
}

// TODO: 향후 구현을 위한 타입 정의
export interface CreateProductRequest {
  businessType: BusinessTypeCode;
  categoryCode: ServiceCategoryCode;
  serviceName: string;
  price: number;
  description?: string;
  orderType: OrderType;
  durationMinutes: number;
  imageUrl?: string;
}

export interface UpdateProductRequest {
  serviceName: string;
  businessType: BusinessTypeCode;
  categoryCode: ServiceCategoryCode;
  price: number;
  description?: string;
  durationMinutes: number;
  imageUrl?: string;
  isActive?: boolean;
}
