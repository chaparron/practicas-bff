package bff.model

import digitalpayments.sdk.model.CreatePaymentResponse
import digitalpayments.sdk.model.UpdatePaymentResponse
import groovy.transform.EqualsAndHashCode

class CreateDigitalPaymentInput {
    String accessToken
    String supplierOrderId
    String amount
    String totalAmount
    PaymentProviderCode paymentProviderCode
}

interface CreateDigitalPaymentResult {}


@EqualsAndHashCode
class JpMorganCreateDigitalPayment implements CreateDigitalPaymentResult {
    String bankId
    String merchantId
    String terminalId
    String encData

    static JpMorganCreateDigitalPayment fromSdk(CreatePaymentResponse response) {
        new JpMorganCreateDigitalPayment(
                bankId: response.bankId,
                merchantId: response.merchantId,
                terminalId: response.terminalId,
                encData: response.encData
        )
    }
}

@EqualsAndHashCode
class CreateDigitalPaymentFailed implements CreateDigitalPaymentResult {
    CreateDigitalPaymentFailedReason reason
}

enum CreateDigitalPaymentFailedReason {
    GATEWAY_NOT_SUPPORTED,
    SDK_ERROR

    def build() {
        new CreateDigitalPaymentFailed(reason: this)
    }
}

@EqualsAndHashCode
class FinalizeDigitalPaymentInput {
    String encData
    String accessToken
}

interface FinalizeDigitalPaymentResult {}

@EqualsAndHashCode
class DigitalPaymentFailed implements FinalizeDigitalPaymentResult {
    String responseCode
    String message
}

@EqualsAndHashCode
class DigitalPayment  implements FinalizeDigitalPaymentResult{
    String paymentId
    String supplierOrderId
    String amount
    String totalAmount
    String responseCode
    String message

    static DigitalPayment fromSdk(UpdatePaymentResponse response) {
        new DigitalPayment(
                paymentId: response.paymentId,
                supplierOrderId: response.supplierOrderId,
                amount: response.amount,
                totalAmount: response.totalAmount,
                responseCode: response.responseCode,
                message: response.message
        )
    }
}