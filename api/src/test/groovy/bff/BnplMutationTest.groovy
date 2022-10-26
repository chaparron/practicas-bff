package bff

import bff.model.BnplMutation
import bff.model.LoanPayment
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.InvoiceResponse
import bnpl.sdk.model.LoanResponse
import bnpl.sdk.model.MoneyResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import wabi.sdk.impl.CustomSdkException
import wabi.sdk.impl.DetailedError

import java.time.Instant

import static bff.TestExtensions.*
import static bff.model.LoanPaymentFailedReason.INVALID_SUPPLIER_ORDER_ID
import static bff.model.LoanPaymentFailedReason.UNKNOWN
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when

class BnplMutationTest {

    def bnPlSdk = Mockito.mock(BnPlSdk)

    def sut = new BnplMutation(
            bnPlSdk: bnPlSdk
    )

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    void 'should return loan payment result if the request is valid'() {
        Long supplierOrderId = 11111L
        def token = validAccessToken()
        def moneyResponse = new MoneyResponse("ARS", BigDecimal.TEN)
        def loanCreated = Instant.now()
        def loanResponse = new LoanResponse("externalId", loanCreated)
        def invoiceResponse = new InvoiceResponse("code")

        def sdkResponse = anyPaymentResponse(666L, supplierOrderId, 2456, 5624,
                moneyResponse, loanResponse, invoiceResponse)

        def sdkRequest = anyPaymentRequest(
                supplierOrderId,
                sdkResponse.customerUserId,
                invoiceResponse.code,
                BigDecimal.TEN)

        def expectedResponse = LoanPayment.fromSdk(sdkResponse)

        when(bnPlSdk.payWithLoan(eq(sdkRequest), eq(token))).thenReturn(Mono.just(sdkResponse))

        def response = sut.loanPayment(anyLoanPaymentRequestInput(
                token,
                666L,
                supplierOrderId,
                sdkRequest.invoiceCode,
                sdkRequest.invoiceFileId,
                sdkRequest.amount)).get()

        assert response == expectedResponse
    }

    @Test
    void 'when sdk fails with CustomSdkException for a mapped error should return LoanPaymentFailed with mapped LoanPaymentFailedReason'() {
        Long supplierOrderId = 11111L
        def token = validAccessToken()
        def moneyResponse = new MoneyResponse("ARS", BigDecimal.TEN)
        def loanCreated = Instant.now()
        def loanResponse = new LoanResponse("externalId", loanCreated)
        def invoiceResponse = new InvoiceResponse("code")

        def sdkResponse = anyPaymentResponse(666L, supplierOrderId, 2456, 5624,
                moneyResponse, loanResponse, invoiceResponse)

        def sdkRequest = anyPaymentRequest(
                supplierOrderId,
                sdkResponse.customerUserId,
                invoiceResponse.code,
                BigDecimal.TEN)

        def sdkException = new CustomSdkException(new DetailedError(INVALID_SUPPLIER_ORDER_ID.name(), randomString()))

        when(bnPlSdk.payWithLoan(eq(sdkRequest), eq(token))).thenReturn(Mono.error(sdkException))

        def response = sut.loanPayment(anyLoanPaymentRequestInput(
                token,
                666L,
                supplierOrderId,
                sdkRequest.invoiceCode,
                sdkRequest.invoiceFileId,
                sdkRequest.amount)).get()

        assert response == INVALID_SUPPLIER_ORDER_ID.build(sdkException.error.reason)
    }

    @Test
    void 'when sdk fails with CustomSdkException for a non mapped error should return LoanPaymentFailed with UNKNOWN LoanPaymentFailedReason'() {
        Long supplierOrderId = 11111L
        def token = validAccessToken()
        def moneyResponse = new MoneyResponse("ARS", BigDecimal.TEN)
        def loanCreated = Instant.now()
        def loanResponse = new LoanResponse("externalId", loanCreated)
        def invoiceResponse = new InvoiceResponse("code")

        def sdkResponse = anyPaymentResponse(666L, supplierOrderId, 2456, 5624,
                moneyResponse, loanResponse, invoiceResponse)

        def sdkRequest = anyPaymentRequest(
                supplierOrderId,
                sdkResponse.customerUserId,
                invoiceResponse.code,
                BigDecimal.TEN)

        def sdkException = new CustomSdkException(new DetailedError(randomString(), randomString()))

        when(bnPlSdk.payWithLoan(eq(sdkRequest), eq(token))).thenReturn(Mono.error(sdkException))

        def response = sut.loanPayment(anyLoanPaymentRequestInput(
                token,
                666L,
                supplierOrderId,
                sdkRequest.invoiceCode,
                sdkRequest.invoiceFileId,
                sdkRequest.amount)).get()

        assert response == UNKNOWN.build(sdkException.error.reason)
    }
}
