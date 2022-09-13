package bff.bridge


import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse

interface PaymentsBridge {
    GetSupplierOrderPaymentResponse getSupplierOrderPayments(GetSupplierOrderPaymentRequest getSupplierOrderPaymentRequest, String apiClientToken)
}