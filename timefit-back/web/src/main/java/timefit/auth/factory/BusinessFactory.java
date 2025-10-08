//package timefit.auth.factory;
//
//import timefit.business.entity.Business;
//
///**
// * Business Entity 생성을 담당하는 Factory 클래스
// */
//public class BusinessFactory {
//
//    private BusinessFactory() {
//        // utility class
//    }
//
//    /**
//     * 비즈니스 생성
//     */
//    public static Business createBusiness(String businessName, String businessType, String businessNumber,
//                                          String address, String contactPhone, String description) {
//        return Business.createBusiness(businessName, businessType, businessNumber, address, contactPhone, description);
//    }
//
//    /**
//     * 비즈니스 정보 업데이트
//     */
//    public static void updateBusinessInfo(Business business, String businessName, String businessType,
//                                          String address, String contactPhone, String description, String logoUrl) {
//        business.updateBusinessInfo(businessName, businessType, address, contactPhone, description, logoUrl);
//    }
//
//    /**
//     * 비즈니스 로고 업데이트
//     */
//    public static void updateLogo(Business business, String logoUrl) {
//        business.updateLogo(logoUrl);
//    }
//
//    /**
//     * 비즈니스 기본 정보만 업데이트 (상호명, 업종, 주소)
//     */
//    public static void updateBasicInfo(Business business, String businessName, String businessType, String address) {
//        business.updateBasicInfo(businessName, businessType, address);
//    }
//
//    /**
//     * 연락처 정보만 업데이트
//     */
//    public static void updateContactInfo(Business business, String contactPhone, String description) {
//        business.updateContactInfo(contactPhone, description);
//    }
//}