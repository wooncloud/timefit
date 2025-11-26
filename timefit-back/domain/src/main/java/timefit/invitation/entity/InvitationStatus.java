package timefit.invitation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvitationStatus {
    PENDING("대기중", "초대 이메일이 발송되었으나 아직 수락/거절되지 않은 상태"),
    ACCEPTED("수락됨", "초대를 수락하여 업체 구성원으로 등록된 상태"),
    EXPIRED("만료됨", "초대 유효기간이 지난 상태"),
    CANCELED("취소됨", "초대를 발송한 사람이 초대를 취소한 상태");

    private final String description;
    private final String detail;

    /**
     * 초대가 아직 유효한 상태인지 확인
     * PENDING 상태만 유효함
     */
    public boolean isPending() {
        return this == PENDING;
    }

    /**
     * 초대가 완료된 상태인지 확인
     * ACCEPTED, EXPIRED, CANCELED는 완료된 상태
     */
    public boolean isCompleted() {
        return this == ACCEPTED || this == EXPIRED || this == CANCELED;
    }
}