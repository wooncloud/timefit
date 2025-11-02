package timefit.operatinghours.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.repository.BusinessRepository;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BusinessFinder {

    private final BusinessRepository businessRepository;

    public Business getBusinessEntity(UUID businessId) {
        return businessRepository.findById(businessId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.BUSINESS_NOT_FOUND));
    }
}