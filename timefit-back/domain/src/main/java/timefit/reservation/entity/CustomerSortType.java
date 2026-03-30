package timefit.reservation.entity;

/**
 * 고객 목록 정렬 기준
 */
public enum CustomerSortType {
    LAST_VISIT,    // 최근 방문일 내림차순 (기본값)
    FIRST_VISIT,   // 첫 방문일 오름차순
    TOTAL_VISITS,  // 총 방문 횟수 내림차순
    NAME           // 이름 오름차순
}