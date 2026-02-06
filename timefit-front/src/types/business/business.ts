/**
 * 업종 코드
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
 * 업체 목록 아이템 (검색 결과용)
 */
export interface BusinessItem {
    businessId: string;
    businessName: string;
    businessTypes: BusinessTypeCode[];
    logoUrl: string | null;
    myRole: string | null;
    joinedAt: string | null;
    isActive: boolean;
}

/**
 * 업체 목록 응답
 */
export interface BusinessListResponse {
    businesses: BusinessItem[];
    totalCount: number;
}

/**
 * 업체 상세 정보 (공개용)
 */
export interface PublicBusinessDetail {
    businessId: string;
    businessName: string;
    businessTypes: BusinessTypeCode[];
    ownerName: string;
    address: string;
    contactPhone: string;
    description: string;
    logoUrl: string | null;
    createdAt: string;
    updatedAt: string;
    averageRating: number | null;
    reviewCount: number;
    latitude: number | null;
    longitude: number | null;
}

/**
 * 업체 검색 파라미터
 */
export interface BusinessSearchParams {
    keyword?: string;
    businessType?: BusinessTypeCode;
    region?: string;
    page?: number;
    size?: number;
}

/**
 * API 응답 래퍼
 */
export interface ApiResponse<T> {
    data?: T;
    errorResponse?: {
        code: string;
        message: string;
        field?: string;
    };
}
