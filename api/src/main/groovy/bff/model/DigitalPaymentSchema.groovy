package bff.model

import digitalpayments.sdk.model.CreatePaymentResponse
import digitalpayments.sdk.model.UpdatePaymentResponse
import groovy.transform.EqualsAndHashCode

class CreateDigitalPaymentInput {
    String accessToken
    Long supplierOrderId
    BigDecimal amount
    String invoiceId
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
    DigitalPaymentFailedReason reason
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
    SUPPLIER_ORDER_NOT_FOUND,
    INVALID_AMOUNT,
    INVALID_CARD_NUMBER,
    INVALID_BIN,
    LOST_CARD,
    INSUFFICIENT_FUNDS,
    INVALID_PIN,
    EXCEEDS_WITHDRAWAL_AMOUNT_LIMIT,
    SECURITY_VIOLATION,
    MERCHANT_NOT_ENROLLED,
    DUPLICATE_REQUEST_FOUND,
    MASTERCARD_NOT_ALLOWED,
    RUPAY_NOT_ALLOWED,
    VISA_NOT_ALLOWED,
    CREDIT_CARD_NOT_ALLOWED,
    DEBIT_CARD_NOT_ALLOWED,
    EXPIRED_CARD,
    UNKNOWN_RESPONSE_CODE;

    static DigitalPaymentFailedReason findByName(String name) {
        Optional.ofNullable(values().find { it.name() == name }).orElse(UNKNOWN)
    }

    def build(String message) {
        new DigitalPaymentFailed(reason: this, message: message)
    }
}