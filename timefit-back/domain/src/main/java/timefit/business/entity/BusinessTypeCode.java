package timefit.business.entity;

/**
 * 업종 타입 코드
 * BD000 ~ BD009까지 총 10개 카테고리
 */
public enum BusinessTypeCode {
    BD000("음식점업"),
    BD001("숙박업"),
    BD002("소매/유통업"),
    BD003("미용/뷰티업"),
    BD004("의료업"),
    BD005("피트니스/스포츠업"),
    BD006("교육/문화업"),
    BD007("전문서비스업"),
    BD008("생활서비스업"),
    BD009("제조/생산업");

    private final String description;

    BusinessTypeCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}