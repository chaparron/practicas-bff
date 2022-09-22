package bff.model

import digitalpayments.sdk.model.CreatePaymentResponse
import digitalpayments.sdk.model.UpdatePaymentResponse
import groovy.transform.EqualsAndHashCode

class CreateDigitalPaymentInput {
    String accessToken
    Long supplierOrderId
    BigDecimal amount
    String invoiceId
    Long supplierId
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
class DigitalPayment implements FinalizeDigitalPaymentResult {
    String paymentId
    String supplierOrderId
    String amount
    String responseCode
    String message

    static DigitalPayment fromSdk(UpdatePaymentResponse response) {
        new DigitalPayment(
                paymentId: response.paymentId,
                supplierOrderId: response.supplierOrderId,
                amount: response.amount,
                responseCode: response.responseCode,
                message: response.message
        )
    }

}


enum DigitalPaymentFailedReason {

    FUNCTIONALITY_NOT_AVAILABLE,
    CLIENT_TOKEN_EXCEPTION,
    MISSING_FIELD_EXCEPTION,
    MISSING_ENC_DATA_EXCEPTION,
    UPDATE_PAYMENT_EXCEPTION,
    PAYMENT_NOT_FOUND_EXCEPTION,
    UNKNOWN,
    TOTAL_AMOUNT_REACHED,
    SUPPLIER_ORDER_NOT_FOUND;

    static DigitalPaymentFailedReason findByName(String name) {
        Optional.ofNullable(values().find { it.name() == name }).orElse(UNKNOWN)
    }

    def build(String message) {
        new DigitalPaymentFailed(responseCode: this, message: message)
    }
}