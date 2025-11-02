package timefit.business.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 서비스 카테고리 코드 (중분류)
 * 각 BusinessTypeCode(대분류)에 속하는 서비스 카테고리를 정의
 * 설계 의도:
 * - BusinessCategory.name을 자유 입력이 아닌 enum 으로 관리
 * - 통계 및 사업 확장성을 위해 코드 체계화
 * - 각 업종(BusinessTypeCode)마다 유효한 카테고리 제한
 * - TODO: 일단 동작하는거 빨리 봐야해서 AI 생성된 것 그대로 배치함. 차후 반드시 변경된 구조로 적용.
 */
public enum ServiceCategoryCode {

    // ========================================
    // BD003: 미용/뷰티업
    // ========================================
    HAIR_CUT("BD003", "컷", "커트 서비스"),
    HAIR_PERM("BD003", "펌", "펌 시술"),
    HAIR_STYLING("BD003", "스타일링", "헤어 스타일링"),
    HAIR_COLORING("BD003", "염색", "염색 서비스"),
    HAIR_TREATMENT("BD003", "트리트먼트", "두피/모발 케어"),
    HAIR_SCALP_CARE("BD003", "두피케어", "두피 관리"),

    // ========================================
    // BD000: 음식점업
    // ========================================
    FOOD_KOREAN("BD000", "한식", "한국 전통 음식"),
    FOOD_CHINESE("BD000", "중식", "중국 음식"),
    FOOD_JAPANESE("BD000", "일식", "일본 음식"),
    FOOD_WESTERN("BD000", "양식", "서양 음식"),
    FOOD_FUSION("BD000", "퓨전", "퓨전 요리"),
    FOOD_CAFE("BD000", "카페/디저트", "카페 및 디저트"),

    // ========================================
    // BD001: 숙박업
    // ========================================
    HOTEL_STANDARD_ROOM("BD001", "일반실", "스탠다드룸"),
    HOTEL_DELUXE_ROOM("BD001", "디럭스룸", "디럭스룸"),
    HOTEL_SUITE_ROOM("BD001", "스위트룸", "스위트룸"),
    HOTEL_FAMILY_ROOM("BD001", "가족실", "가족룸"),

    // ========================================
    // BD002: 소매/유통업
    // ========================================
    RETAIL_FASHION("BD002", "패션/의류", "의류 판매"),
    RETAIL_COSMETICS("BD002", "화장품", "화장품 판매"),
    RETAIL_FOOD("BD002", "식품", "식품 판매"),
    RETAIL_ELECTRONICS("BD002", "전자제품", "전자제품 판매"),
    RETAIL_BOOKS("BD002", "서적/문구", "서적 및 문구"),

    // ========================================
    // BD004: 의료업
    // ========================================
    MEDICAL_CONSULTATION("BD004", "진료", "의료 상담 및 진료"),
    MEDICAL_TREATMENT("BD004", "치료", "치료 서비스"),
    MEDICAL_CHECK_UP("BD004", "검진", "건강검진"),
    MEDICAL_SURGERY("BD004", "수술", "수술 서비스"),

    // ========================================
    // BD005: 피트니스/스포츠업
    // ========================================
    FITNESS_GYM("BD005", "헬스", "헬스 트레이닝"),
    FITNESS_PILATES("BD005", "필라테스", "필라테스"),
    FITNESS_YOGA("BD005", "요가", "요가"),
    FITNESS_PT("BD005", "PT", "개인 트레이닝"),
    FITNESS_SPINNING("BD005", "스피닝", "스피닝"),
    FITNESS_SWIMMING("BD005", "수영", "수영 레슨"),

    // ========================================
    // BD006: 교육/문화업
    // ========================================
    EDUCATION_LANGUAGE("BD006", "어학", "외국어 교육"),
    EDUCATION_MUSIC("BD006", "음악", "음악 레슨"),
    EDUCATION_ART("BD006", "미술", "미술 교육"),
    EDUCATION_DANCE("BD006", "무용", "댄스 레슨"),
    EDUCATION_ACADEMIC("BD006", "학습", "학습 교육"),

    // ========================================
    // BD007: 전문서비스업
    // ========================================
    PROFESSIONAL_LEGAL("BD007", "법률", "법률 상담"),
    PROFESSIONAL_ACCOUNTING("BD007", "회계", "회계/세무"),
    PROFESSIONAL_CONSULTING("BD007", "컨설팅", "경영 컨설팅"),
    PROFESSIONAL_DESIGN("BD007", "디자인", "디자인 서비스"),

    // ========================================
    // BD008: 생활서비스업
    // ========================================
    LIFE_CLEANING("BD008", "청소", "청소 서비스"),
    LIFE_LAUNDRY("BD008", "세탁", "세탁 서비스"),
    LIFE_REPAIR("BD008", "수리", "수리 서비스"),
    LIFE_MOVING("BD008", "이사", "이사 서비스"),
    LIFE_PET_CARE("BD008", "반려동물", "반려동물 케어"),

    // ========================================
    // BD009: 제조/생산업
    // ========================================
    MANUFACTURING_CUSTOM("BD009", "맞춤제작", "주문 제작"),
    MANUFACTURING_WHOLESALE("BD009", "도매", "도매 판매"),
    MANUFACTURING_OEM("BD009", "OEM", "OEM 생산");

    private final String businessTypeCode;  // BusinessTypeCode의 코드값
    private final String displayName;       // 화면 표시용 이름
    private final String description;       // 상세 설명

    ServiceCategoryCode(String businessTypeCode, String displayName, String description) {
        this.businessTypeCode = businessTypeCode;
        this.displayName = displayName;
        this.description = description;
    }

    public String getBusinessTypeCode() {
        return businessTypeCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 특정 BusinessTypeCode에 해당하는 카테고리 목록 조회
     *
     * @param businessType 대분류 업종
     * @return 해당 업종의 서비스 카테고리 목록
     */
    public static List<ServiceCategoryCode> getByBusinessType(BusinessTypeCode businessType) {
        return Arrays.stream(values())
                .filter(category -> category.businessTypeCode.equals(businessType.name()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 BusinessTypeCode에 해당하는 카테고리인지 확인
     *
     * @param businessType 대분류 업종
     * @return 유효한 카테고리 여부
     */
    public boolean belongsTo(BusinessTypeCode businessType) {
        return this.businessTypeCode.equals(businessType.name());
    }

    /**
     * 코드값으로 ServiceCategoryCode 조회
     *
     * @param code enum 코드값
     * @return ServiceCategoryCode
     * @throws IllegalArgumentException 유효하지 않은 코드일 경우
     */
    public static ServiceCategoryCode fromCode(String code) {
        try {
            return ServiceCategoryCode.valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 서비스 카테고리 코드: " + code);
        }
    }
}