package bff.bridge

import digitalpayments.sdk.model.Provider
import reactor.core.publisher.Mono

interface DigitalPaymentsBridge {
    List<Provider> getPaymentProviders(String supplierId, String accessToken)
}