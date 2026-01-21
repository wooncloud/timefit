/**
 * 주문 타입 (예약 기반 / 당일 접수 기반)
 */
export type OrderType = 'RESERVATION_BASED' | 'ONDEMAND_BASED';

/**
 * 업종 코드
 * - BD000: 음식점
 * - BD001: 카페
 * - BD002: 숙박
 * - BD003: 공연/전시
 * - BD004: 스포츠/오락
 * - BD005: 레저/체험
 * - BD006: 여행/명소
 * - BD007: 건강/의료
 * - BD008: 뷰티
 * - BD009: 생활/편의
 * - BD010: 쇼핑/유통
 * - BD011: 장소 대여
 * - BD012: 자연
 * - BD013: 기타
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
 * 메뉴(서비스) 정보
 * 백엔드 MenuResponseDto.Menu에 대응
 */
export interface Menu {
  menuId: string;
  businessId: string;
  serviceName: string;
  businessCategoryId: string;
  businessType: BusinessTypeCode;
  categoryName: string;
  price: number;
  description?: string;
  orderType: OrderType;
  durationMinutes?: number;
  imageUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * 메뉴 목록 조회 응답
 * 백엔드 MenuResponseDto.MenuList에 대응
 */
export interface MenuListResponse {
  menus: Menu[];
  totalCount: number;
}

/**
 * 예약 슬롯 시간 범위
 */
interface TimeRange {
  startTime: string;
  endTime: string;
}

/**
 * 예약 슬롯 생성 설정
 */
export interface BookingSlotSettings {
  startDate: string;
  endDate: string;
  slotIntervalMinutes: number;
  specificTimeRanges?: TimeRange[];
}

/**
 * 메뉴 생성/수정 요청
 * 백엔드 MenuRequestDto.CreateUpdateMenu에 대응
 */
export interface CreateUpdateMenuRequest {
  businessType: BusinessTypeCode;
  categoryName: string;
  serviceName: string;
  price: number;
  description?: string;
  imageUrl?: string;
  orderType: OrderType;
  durationMinutes?: number;
  autoGenerateSlots?: boolean;
  slotSettings?: BookingSlotSettings;
}

/**
 * 메뉴 목록 조회 Next.js 핸들러 응답
 */
export interface GetMenuListHandlerResponse {
  success: boolean;
  data?: MenuListResponse;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * 메뉴 상세 조회 Next.js 핸들러 응답
 */
export interface GetMenuDetailHandlerResponse {
  success: boolean;
  data?: Menu;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * 메뉴 생성 Next.js 핸들러 응답
 */
export interface CreateMenuHandlerResponse {
  success: boolean;
  data?: Menu;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * 메뉴 수정 Next.js 핸들러 응답
 */
export interface UpdateMenuHandlerResponse {
  success: boolean;
  data?: Menu;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}

/**
 * 메뉴 삭제 Next.js 핸들러 응답
 */
export interface DeleteMenuHandlerResponse {
  success: boolean;
  message?: string;
  requiresLogout?: boolean;
  redirectTo?: string;
}
