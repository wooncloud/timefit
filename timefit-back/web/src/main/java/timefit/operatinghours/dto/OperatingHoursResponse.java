package timefit.operatinghours.dto;

import lombok.Getter;
import timefit.business.entity.OperatingHours;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OperatingHoursResponse {

    // 영업시간 조회 결과
    @Getter
    public static class OperatingHoursResult {
        private final UUID businessId;
        private final String businessName;
        private final List<HourDetail> businessHours;
        private final LocalDateTime updatedAt;

        private OperatingHoursResult(UUID businessId, String businessName,
                                     List<HourDetail> businessHours,
                                     LocalDateTime updatedAt) {
            this.businessId = businessId;
            this.businessName = businessName;
            this.businessHours = businessHours;
            this.updatedAt = updatedAt;
        }

        public static OperatingHoursResult of(UUID businessId, String businessName,
                                              List<OperatingHours> hours) {
            return new OperatingHoursResult(
                    businessId,
                    businessName,
                    hours.stream()
                            .map(HourDetail::of)
                            .collect(Collectors.toList()),
                    hours.isEmpty() ? null : hours.get(0).getUpdatedAt()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OperatingHoursResult that = (OperatingHoursResult) o;
            return Objects.equals(businessId, that.businessId) &&
                    Objects.equals(businessName, that.businessName) &&
                    Objects.equals(businessHours, that.businessHours) &&
                    Objects.equals(updatedAt, that.updatedAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessId, businessName, businessHours, updatedAt);
        }
    }

    // 영업시간 상세
    @Getter
    public static class HourDetail {
        private final UUID hourId;
        private final Integer dayOfWeek;      // 0~6 그대로 전달
        private final String openTime;        // "HH:mm" 그대로 전달
        private final String closeTime;       // "HH:mm" 그대로 전달
        private final Boolean isClosed;
        private final Integer sequence;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        private HourDetail(UUID hourId, Integer dayOfWeek, String openTime,
                           String closeTime, Boolean isClosed, Integer sequence,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.hourId = hourId;
            this.dayOfWeek = dayOfWeek;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.isClosed = isClosed;
            this.sequence = sequence;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public static HourDetail of(OperatingHours hours) {
            return new HourDetail(
                    hours.getId(),
                    hours.getDayOfWeek().getValue(),  // 0~6 값만
                    hours.getOpenTime() != null ? hours.getOpenTime().toString() : null,
                    hours.getCloseTime() != null ? hours.getCloseTime().toString() : null,
                    hours.getIsClosed(),
                    hours.getSequence(),
                    hours.getCreatedAt(),
                    hours.getUpdatedAt()
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HourDetail that = (HourDetail) o;
            return Objects.equals(hourId, that.hourId) &&
                    Objects.equals(dayOfWeek, that.dayOfWeek) &&
                    Objects.equals(openTime, that.openTime) &&
                    Objects.equals(closeTime, that.closeTime) &&
                    Objects.equals(isClosed, that.isClosed) &&
                    Objects.equals(sequence, that.sequence);
        }

        @Override
        public int hashCode() {
            return Objects.hash(hourId, dayOfWeek, openTime, closeTime, isClosed, sequence);
        }
    }
}