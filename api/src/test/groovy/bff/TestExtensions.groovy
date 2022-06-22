package bff

import bff.model.LoanPaymentRequestInput
import bnpl.sdk.model.InvoiceResponse
import bnpl.sdk.model.LoanResponse
import bnpl.sdk.model.MoneyResponse
import bnpl.sdk.model.PaymentResponse
import bnpl.sdk.model.requests.PaymentRequest

import java.time.Instant

class TestExtensions {
    static String randomString() {
        UUID.randomUUID().toString()
    }

    static String validAccessToken() {
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmV2aW91c0xvZ2luIjoxNjU1MTI0MjU2MDAwLCJ1c2VyX25hbWUiOiIrNTQtMTI0NTc4IiwiZW50aXR5VHlwZSI6IkNVU1RPTUVSIiwic2NvcGUiOlsiYWxsIl0sInRvcyI6eyJhY2NlcHRlZCI6MTY0NDM1NDgzNzAwMH0sImVudGl0eUlkIjoiMTUwOSIsInN0YXRlIjoiQVItQiIsImV4cCI6MTY1NTE2OTEzNywidXNlciI6eyJpZCI6MjQ1NiwidXNlcm5hbWUiOiIrNTQtMTI0NTc4IiwiZmlyc3ROYW1lIjoiUUEiLCJsYXN0TmFtZSI6Ik1hbnVhbCIsImNvdW50cmllcyI6W3siaWQiOiJhciIsIm5hbWUiOiJBcmdlbnRpbmEifV19LCJhdXRob3JpdGllcyI6WyJGRV9XRUIiLCJGRV9CUkFOQ0hfT0ZGSUNFX0NIQU5HRSJdLCJqdGkiOiJiNjI0NDMyZS0zOTY3LTRjNWMtODAyNC03MjI2ODhjY2QyMDQiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.nxx7fMB_JNDvfjE6px3NrdgTcX83BIK7F_eTH5wsx14"
    }

    static PaymentResponse anyPaymentResponse(UUID paymentId, Long orderId, String externalId, String customerId, String supplierId,
                                              Instant created, MoneyResponse money, LoanResponse loan, InvoiceResponse invoice) {
        new PaymentResponse(
                paymentId, orderId, externalId, customerId, supplierId, created,
                money, loan, invoice
        )
    }

    static PaymentRequest anyPaymentRequest(Long orderId,  String customerId, String supplierId, String invoiceCode, BigDecimal amount) {
        new PaymentRequest(orderId, customerId, supplierId, invoiceCode, amount)
    }

    static LoanPaymentRequestInput anyLoanPaymentRequestInput(String token, String supplierId,
                                                              Long orderId, String code, BigDecimal amount) {
        new LoanPaymentRequestInput(accessToken: token, supplierId: supplierId,
                orderId: orderId, invoiceCode: code, amount: amount)
    }
}
