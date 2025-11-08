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
 *
 * 변경 이력:
 * - BusinessTypeCode 확장(14개)에 따른 전면 재구성
 */
public enum ServiceCategoryCode {

    // ========================================
    // BD000: 음식점
    // ========================================
    RESTAURANT_KOREAN("BD000", "한식", "한국 전통 음식"),
    RESTAURANT_CHINESE("BD000", "중식", "중국 음식"),
    RESTAURANT_JAPANESE("BD000", "일식", "일본 음식"),
    RESTAURANT_WESTERN("BD000", "양식", "서양 음식"),
    RESTAURANT_ASIAN("BD000", "아시안", "동남아/인도 등 아시아 음식"),
    RESTAURANT_FUSION("BD000", "퓨전", "퓨전 요리"),
    RESTAURANT_BUFFET("BD000", "뷔페", "뷔페 레스토랑"),
    RESTAURANT_FAST_FOOD("BD000", "패스트푸드", "패스트푸드"),
    RESTAURANT_SNACK("BD000", "분식", "분식류"),

    // ========================================
    // BD001: 카페
    // ========================================
    CAFE_COFFEE("BD001", "커피전문점", "커피 전문 카페"),
    CAFE_DESSERT("BD001", "디저트카페", "디저트 중심 카페"),
    CAFE_BAKERY("BD001", "베이커리", "베이커리 카페"),
    CAFE_TEA("BD001", "티카페", "차 전문 카페"),
    CAFE_BRUNCH("BD001", "브런치카페", "브런치 전문 카페"),
    CAFE_THEME("BD001", "테마카페", "특정 테마 카페"),

    // ========================================
    // BD002: 숙박
    // ========================================
    ACCOMMODATION_HOTEL("BD002", "호텔", "일반 호텔"),
    ACCOMMODATION_RESORT("BD002", "리조트", "리조트"),
    ACCOMMODATION_PENSION("BD002", "펜션", "펜션"),
    ACCOMMODATION_MOTEL("BD002", "모텔", "모텔"),
    ACCOMMODATION_GUESTHOUSE("BD002", "게스트하우스", "게스트하우스"),
    ACCOMMODATION_HANOK("BD002", "한옥", "한옥 스테이"),
    ACCOMMODATION_CAMPING("BD002", "캠핑/글램핑", "캠핑장/글램핑"),

    // ========================================
    // BD003: 공연/전시
    // ========================================
    CULTURE_CONCERT("BD003", "콘서트", "음악 콘서트"),
    CULTURE_MUSICAL("BD003", "뮤지컬", "뮤지컬 공연"),
    CULTURE_PLAY("BD003", "연극", "연극 공연"),
    CULTURE_EXHIBITION("BD003", "전시회", "전시회"),
    CULTURE_GALLERY("BD003", "갤러리", "갤러리"),
    CULTURE_FESTIVAL("BD003", "페스티벌", "페스티벌/축제"),
    CULTURE_SHOW("BD003", "쇼/공연", "각종 쇼 및 공연"),

    // ========================================
    // BD004: 스포츠/오락
    // ========================================
    SPORTS_FITNESS("BD004", "헬스장", "헬스 트레이닝"),
    SPORTS_PILATES("BD004", "필라테스", "필라테스"),
    SPORTS_YOGA("BD004", "요가", "요가"),
    SPORTS_PT("BD004", "PT", "개인 트레이닝"),
    SPORTS_SWIMMING("BD004", "수영장", "수영"),
    SPORTS_GOLF("BD004", "골프", "골프 연습장/필드"),
    SPORTS_CLIMBING("BD004", "클라이밍", "클라이밍"),
    SPORTS_BOWLING("BD004", "볼링", "볼링장"),
    SPORTS_BILLIARDS("BD004", "당구", "당구장"),
    SPORTS_SCREEN_GOLF("BD004", "스크린골프", "스크린골프"),
    SPORTS_BASEBALL("BD004", "야구", "야구장/배팅센터"),
    SPORTS_TENNIS("BD004", "테니스", "테니스장"),
    SPORTS_BADMINTON("BD004", "배드민턴", "배드민턴장"),
    SPORTS_FOOTBALL("BD004", "축구", "축구장"),

    // ========================================
    // BD005: 레저/체험
    // ========================================
    LEISURE_THEME_PARK("BD005", "테마파크", "테마파크"),
    LEISURE_WATER_PARK("BD005", "워터파크", "워터파크"),
    LEISURE_AMUSEMENT("BD005", "놀이공원", "놀이공원"),
    LEISURE_ESCAPE_ROOM("BD005", "방탈출", "방탈출 카페"),
    LEISURE_VR("BD005", "VR체험", "VR 체험"),
    LEISURE_KARAOKE("BD005", "노래방", "노래방"),
    LEISURE_PC_CAFE("BD005", "PC방", "PC방"),
    LEISURE_BOARD_GAME("BD005", "보드게임", "보드게임 카페"),
    LEISURE_COOKING("BD005", "쿠킹클래스", "요리 체험"),
    LEISURE_CRAFT("BD005", "공예체험", "공예/만들기 체험"),
    LEISURE_FARM("BD005", "농장체험", "농장 체험"),

    // ========================================
    // BD006: 여행/명소
    // ========================================
    TRAVEL_TOURIST_SPOT("BD006", "관광명소", "관광 명소"),
    TRAVEL_MUSEUM("BD006", "박물관", "박물관"),
    TRAVEL_HISTORIC_SITE("BD006", "유적지", "역사 유적지"),
    TRAVEL_TEMPLE("BD006", "사찰", "사찰"),
    TRAVEL_PALACE("BD006", "궁궐", "궁궐"),
    TRAVEL_OBSERVATORY("BD006", "전망대", "전망대"),
    TRAVEL_ZOO("BD006", "동물원", "동물원"),
    TRAVEL_AQUARIUM("BD006", "아쿠아리움", "아쿠아리움"),
    TRAVEL_BOTANICAL_GARDEN("BD006", "식물원", "식물원"),

    // ========================================
    // BD007: 건강/의료
    // ========================================
    MEDICAL_HOSPITAL("BD007", "병원", "일반 병원"),
    MEDICAL_CLINIC("BD007", "의원", "의원"),
    MEDICAL_DENTAL("BD007", "치과", "치과"),
    MEDICAL_ORIENTAL("BD007", "한의원", "한의원"),
    MEDICAL_PHARMACY("BD007", "약국", "약국"),
    MEDICAL_HEALTH_CHECK("BD007", "건강검진", "건강검진센터"),
    MEDICAL_THERAPY("BD007", "물리치료", "물리치료"),
    MEDICAL_MENTAL("BD007", "정신건강", "정신건강의학과/상담센터"),

    // ========================================
    // BD008: 뷰티
    // ========================================
    BEAUTY_HAIR_CUT("BD008", "커트", "헤어 커트"),
    BEAUTY_HAIR_PERM("BD008", "펌", "펌 시술"),
    BEAUTY_HAIR_COLORING("BD008", "염색", "염색 서비스"),
    BEAUTY_HAIR_STYLING("BD008", "스타일링", "헤어 스타일링"),
    BEAUTY_HAIR_TREATMENT("BD008", "트리트먼트", "모발 케어"),
    BEAUTY_SCALP_CARE("BD008", "두피케어", "두피 관리"),
    BEAUTY_NAIL("BD008", "네일", "네일아트"),
    BEAUTY_SKIN_CARE("BD008", "피부관리", "피부관리샵"),
    BEAUTY_MAKEUP("BD008", "메이크업", "메이크업"),
    BEAUTY_SPA("BD008", "스파", "스파/마사지"),
    BEAUTY_WAXING("BD008", "왁싱", "제모/왁싱"),
    BEAUTY_TATTOO("BD008", "타투/반영구", "타투/반영구화장"),

    // ========================================
    // BD009: 생활/편의
    // ========================================
    LIFE_LAUNDRY("BD009", "세탁", "세탁소"),
    LIFE_CLEANING("BD009", "청소", "청소 서비스"),
    LIFE_REPAIR("BD009", "수리", "수리 서비스"),
    LIFE_MOVING("BD009", "이사", "이사 서비스"),
    LIFE_PET_CARE("BD009", "반려동물", "반려동물 케어"),
    LIFE_PET_HOTEL("BD009", "애견호텔", "애견 호텔"),
    LIFE_PHOTO_STUDIO("BD009", "사진관", "사진 스튜디오"),
    LIFE_PRINTING("BD009", "인쇄", "인쇄/복사"),
    LIFE_COURIER("BD009", "택배", "택배 서비스"),
    LIFE_STORAGE("BD009", "보관", "창고/보관 서비스"),

    // ========================================
    // BD010: 쇼핑/유통
    // ========================================
    RETAIL_FASHION("BD010", "패션/의류", "의류 판매"),
    RETAIL_COSMETICS("BD010", "화장품", "화장품 판매"),
    RETAIL_ELECTRONICS("BD010", "전자제품", "전자제품 판매"),
    RETAIL_FOOD("BD010", "식품", "식품 판매"),
    RETAIL_BOOKS("BD010", "서적/문구", "서적 및 문구"),
    RETAIL_FURNITURE("BD010", "가구/인테리어", "가구 및 인테리어"),
    RETAIL_SPORTS_GOODS("BD010", "스포츠용품", "스포츠 용품"),
    RETAIL_ACCESSORIES("BD010", "액세서리", "액세서리/잡화"),
    RETAIL_GIFT("BD010", "선물/기념품", "선물 및 기념품"),

    // ========================================
    // BD011: 장소 대여
    // ========================================
    RENTAL_PARTY_ROOM("BD011", "파티룸", "파티룸 대여"),
    RENTAL_STUDY_ROOM("BD011", "스터디룸", "스터디룸 대여"),
    RENTAL_MEETING_ROOM("BD011", "회의실", "회의실 대여"),
    RENTAL_STUDIO("BD011", "스튜디오", "촬영/연습 스튜디오"),
    RENTAL_PRACTICE_ROOM("BD011", "연습실", "음악/댄스 연습실"),
    RENTAL_KITCHEN("BD011", "공유주방", "공유 주방"),
    RENTAL_SPACE("BD011", "다목적공간", "다목적 공간 대여"),
    RENTAL_OUTDOOR("BD011", "야외공간", "야외 공간 대여"),

    // ========================================
    // BD012: 자연
    // ========================================
    NATURE_PARK("BD012", "공원", "공원"),
    NATURE_MOUNTAIN("BD012", "산/등산로", "등산로"),
    NATURE_BEACH("BD012", "해변", "해변/해수욕장"),
    NATURE_LAKE("BD012", "호수", "호수"),
    NATURE_RIVER("BD012", "강/계곡", "강/계곡"),
    NATURE_FOREST("BD012", "숲", "숲/수목원"),
    NATURE_ISLAND("BD012", "섬", "섬"),
    NATURE_CAVE("BD012", "동굴", "동굴"),
    NATURE_WATERFALL("BD012", "폭포", "폭포"),

    // ========================================
    // BD013: 기타
    // ========================================
    ETC_EDUCATION("BD013", "교육", "학원/교육기관"),
    ETC_CONSULTING("BD013", "컨설팅", "컨설팅 서비스"),
    ETC_LEGAL("BD013", "법률", "법률 상담"),
    ETC_ACCOUNTING("BD013", "회계", "회계/세무"),
    ETC_DESIGN("BD013", "디자인", "디자인 서비스"),
    ETC_IT("BD013", "IT서비스", "IT 서비스"),
    ETC_MANUFACTURING("BD013", "제조", "제조/생산"),
    ETC_LOGISTICS("BD013", "물류", "물류/유통"),
    ETC_REAL_ESTATE("BD013", "부동산", "부동산 중개"),
    ETC_FINANCE("BD013", "금융", "금융 서비스"),
    ETC_INSURANCE("BD013", "보험", "보험 서비스"),
    ETC_OTHER("BD013", "기타", "기타 서비스");

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