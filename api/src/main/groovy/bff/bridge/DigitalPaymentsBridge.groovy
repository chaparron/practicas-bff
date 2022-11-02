package bff.bridge

import digitalpayments.sdk.model.PaymentOption

interface DigitalPaymentsBridge {
    List<PaymentOption> getPaymentMethods(String supplierId, String accessToken)
}