package bff

import bff.model.*
import bnpl.sdk.model.*
import bnpl.sdk.model.requests.PaymentRequest
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.SupplierWalletResponse
import wabi2b.payments.common.model.response.WalletResponse

class TestExtensions {
    static String randomString() {
        UUID.randomUUID().toString()
    }

    static String validAccessToken() {
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmV2aW91c0xvZ2luIjoxNjU1MTI0MjU2MDAwLCJ1c2VyX25hbWUiOiIrNTQtMTI0NTc4IiwiZW50aXR5VHlwZSI6IkNVU1RPTUVSIiwic2NvcGUiOlsiYWxsIl0sInRvcyI6eyJhY2NlcHRlZCI6MTY0NDM1NDgzNzAwMH0sImVudGl0eUlkIjoiMTUwOSIsInN0YXRlIjoiQVItQiIsImV4cCI6MTY1NTE2OTEzNywidXNlciI6eyJpZCI6MjQ1NiwidXNlcm5hbWUiOiIrNTQtMTI0NTc4IiwiZmlyc3ROYW1lIjoiUUEiLCJsYXN0TmFtZSI6Ik1hbnVhbCIsImNvdW50cmllcyI6W3siaWQiOiJhciIsIm5hbWUiOiJBcmdlbnRpbmEifV19LCJhdXRob3JpdGllcyI6WyJGRV9XRUIiLCJGRV9CUkFOQ0hfT0ZGSUNFX0NIQU5HRSJdLCJqdGkiOiJiNjI0NDMyZS0zOTY3LTRjNWMtODAyNC03MjI2ODhjY2QyMDQiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.nxx7fMB_JNDvfjE6px3NrdgTcX83BIK7F_eTH5wsx14"
    }

    static PaymentResponse anyPaymentResponse(Long supplierOrderId, Long customerUserId, Long supplierId,
                                              MoneyResponse money, LoanResponse loan, InvoiceResponse invoice) {
        new PaymentResponse(
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

    static Customer anyCustomerWithIdAndAccessToken(String country) {
        new Customer(id: randomString(), accessToken: randomString(), country_id: country)
    }
}
