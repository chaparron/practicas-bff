package bff

import bff.model.*
import bnpl.sdk.model.*
import bnpl.sdk.model.requests.PaymentRequest
import digitalpayments.sdk.model.CreatePaymentRequest
import digitalpayments.sdk.model.CreatePaymentResponse
import digitalpayments.sdk.model.UpdatePaymentRequest
import digitalpayments.sdk.model.UpdatePaymentResponse
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.SupplierWalletResponse
import wabi2b.payments.common.model.response.WalletResponse

class TestExtensions {
    static String randomString() {
        UUID.randomUUID().toString()
    }

    static randomLong() {
        new Random().nextLong()
    }

    static randomBigDecimal() {
        new BigDecimal(new Random().nextFloat())
    }

    static String validAccessToken() {
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmV2aW91c0xvZ2luIjoxNjU1MTI0MjU2MDAwLCJ1c2VyX25hbWUiOiIrNTQtMTI0NTc4IiwiZW50aXR5VHlwZSI6IkNVU1RPTUVSIiwic2NvcGUiOlsiYWxsIl0sInRvcyI6eyJhY2NlcHRlZCI6MTY0NDM1NDgzNzAwMH0sImVudGl0eUlkIjoiMTUwOSIsInN0YXRlIjoiQVItQiIsImV4cCI6MTY1NTE2OTEzNywidXNlciI6eyJpZCI6MjQ1NiwidXNlcm5hbWUiOiIrNTQtMTI0NTc4IiwiZmlyc3ROYW1lIjoiUUEiLCJsYXN0TmFtZSI6Ik1hbnVhbCIsImNvdW50cmllcyI6W3siaWQiOiJhciIsIm5hbWUiOiJBcmdlbnRpbmEifV19LCJhdXRob3JpdGllcyI6WyJGRV9XRUIiLCJGRV9CUkFOQ0hfT0ZGSUNFX0NIQU5HRSJdLCJqdGkiOiJiNjI0NDMyZS0zOTY3LTRjNWMtODAyNC03MjI2ODhjY2QyMDQiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.nxx7fMB_JNDvfjE6px3NrdgTcX83BIK7F_eTH5wsx14"
    }

    static String anotherValidAccessToken() {
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmV2aW91c0xvZ2luIjoxNjYwMTQ1OTIxMDAwLCJ1c2VyX25hbWUiOiIrOTEtNzMxMTE2NTEzMiIsImVudGl0eVR5cGUiOiJDVVNUT01FUiIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsiYWNjZXB0ZWQiOjE2NDg3MjMyNzIwMDB9LCJlbnRpdHlJZCI6IjE3MzEiLCJzdGF0ZSI6IklOLU1IIiwiZXhwIjoxNjYwMTg5MTIwLCJ1c2VyIjp7ImlkIjoyODExLCJ1c2VybmFtZSI6Iis5MS03MzExMTY1MTMyIiwiZmlyc3ROYW1lIjoiQU1JVCAiLCJsYXN0TmFtZSI6IktVTUFSIiwiY291bnRyaWVzIjpbeyJpZCI6ImluIiwibmFtZSI6IkluZGlhIn1dfSwiYXV0aG9yaXRpZXMiOlsiRkVfV0VCIiwiRkVfQlJBTkNIX09GRklDRV9DSEFOR0UiXSwianRpIjoiMTNlOTEyNGQtOTBmNi00YjBhLTkwOTUtMjMzMTk4NzUwMTUzIiwiY2xpZW50X2lkIjoiaW50ZXJuYWxfYXBpIn0.IUJZ5GU3fLtVN-zHNitpGRiFBK4A7WzCLom-mKQDaYg"
    }

    static PaymentResponse anyPaymentResponse(Long paymentId, Long supplierOrderId, Long customerUserId, Long supplierId,
                                              MoneyResponse money, LoanResponse loan, InvoiceResponse invoice) {
        new PaymentResponse(
                paymentId,
                supplierOrderId,
                customerUserId,
                supplierId,
                money,
                invoice,
                loan
        )
    }

    static PaymentRequest anyPaymentRequest(Long supplierOrderId, Long customerUserId, String invoiceCode, BigDecimal amount) {
        new PaymentRequest(supplierOrderId, customerUserId, invoiceCode, randomString(), amount)
    }

    static LoanPaymentRequestInput anyLoanPaymentRequestInput(String token, Long supplierId,
                                                              Long supplierOrderId, String code, String fileId, BigDecimal amount) {
        new LoanPaymentRequestInput(accessToken: token, supplierId: supplierId,
                supplierOrderId: supplierOrderId, invoice: new InvoiceInput(code: code, fileId: fileId), amount: amount)
    }

    static OrderSummary anyOrderSummary(Money totalProducts, Supplier supplier, List<Summary> summary) {
        new OrderSummary(
                totalProducts: totalProducts,
                supplier: supplier,
                summary: summary)
    }

    static WalletResponse anyWalletResponse(String userId, String walletId, WalletProvider walletProvider) {
        new WalletResponse(userId, walletId, walletProvider.value)
    }

    static SupplierWalletResponse anySupplierWalletResponse(String supplierId, String walletId, WalletProvider bnplProvider) {
        new SupplierWalletResponse(supplierId, walletId, bnplProvider.value)
    }

    static SupportedMinimumAmountResponse anySupportedMinimumAmountResponse(String country) {
        new SupportedMinimumAmountResponse(BigDecimal.TEN, country)
    }

    static Order anyOrder(OrderStatus status, List<SupplierOrder> supplierOrders, BigDecimal total_money = null) {
        new Order(id: 007, status: status, supplierOrders: supplierOrders, total_money: total_money)
    }

    static Customer anyCustomerWithIdAndAccessToken(String country, CustomerStatus customerStatus = CustomerStatus.APPROVED) {
        new Customer(id: randomString(), accessToken: randomString(), country_id: country, customerStatus: customerStatus)
    }

    static anyCreatePaymentResponse() {
        new CreatePaymentResponse(randomString(), randomString(), randomString(), randomString())
    }

    static anyCreatePaymentRequest(Long supplierOrderId, BigDecimal amount, String invoiceId) {
        new CreatePaymentRequest(supplierOrderId, amount, invoiceId)
    }

    static anyCreateDigitalPaymentInput(Long supplierOrderId, BigDecimal amount, String accessToken, String invoiceId) {
        new CreateDigitalPaymentInput(
                supplierOrderId: supplierOrderId,
                amount: amount,
                accessToken: accessToken,
                invoiceId: invoiceId
        )
    }

    static UpdatePaymentResponse anyUpdatePaymentResponse(Long paymentId, Long supplierOrderId, BigDecimal amount, String responseCode, String message) {
        new UpdatePaymentResponse(
                paymentId,
                supplierOrderId,
                amount,
                responseCode,
                message
        )
    }

    static FinalizeDigitalPaymentInput anyFinalizeDigitalPaymentInput(String encData, String accessToken) {
        new FinalizeDigitalPaymentInput(encData: encData, accessToken: accessToken)
    }

    static Supplier anySupplier() {
        new Supplier(id: 213)
    }

    static SupplierOrder anySupplierOrder(SupplierOrderStatus status = SupplierOrderStatus.CONFIRMED) {
        new SupplierOrder(id: 1, accessToken: randomString(), status: status)
    }
}
