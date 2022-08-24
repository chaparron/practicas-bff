package bff.bridge

import reactor.core.publisher.Mono
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse

interface PaymentsBridge {
    Mono<GetSupplierOrderPaymentResponse> getSupplierOrderPayments(GetSupplierOrderPaymentRequest getSupplierOrderPaymentRequest, String apiClientToken)
}