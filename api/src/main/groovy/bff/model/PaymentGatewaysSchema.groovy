package bff.model

class FindPaymentGatewayDataInput {
    String accessToken
    Float amount
    PaymentProviderCode paymentProviderCode
}

interface PaymentGatewayDataResult {}

class JpMorganPaymentGatewayData implements PaymentGatewayDataResult {
    String bankId
    String merchantId
    String terminalId
    String encData
}

class PaymentGatewayDataFailed implements PaymentGatewayDataResult {
    PaymentGatewayDataFailedReason reason
}

enum PaymentGatewayDataFailedReason {
    GATEWAY_NOT_SUPPORTED
}