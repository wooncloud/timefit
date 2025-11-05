package timefit.business.entity;

/**
 * 업종 타입 코드
 * BD000 ~ BD009까지 총 10개 카테고리
 */
public enum BusinessTypeCode {
    BD000("음식점"),
    BD001("카페"),
    BD002("숙박"),
    BD003("공연/전시"),
    BD004("스포츠/오락"),
    BD005("레저/체험"),
    BD006("여행/명소"),
    BD007("건강/의료"),
    BD008("뷰티"),
    BD009("생활/편의"),
    BD010("쇼핑/유통"),
    BD011("장소 대여"),
    BD012("자연"),
    BD013("기타");

    private final String description;

    BusinessTypeCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}