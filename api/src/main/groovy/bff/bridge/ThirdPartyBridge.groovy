package bff.bridge

interface ThirdPartyBridge {

    Boolean findCustomerConsent(Long customerId, String accessToken)

}