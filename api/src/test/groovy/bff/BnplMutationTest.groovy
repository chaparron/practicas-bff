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

import java.time.Instant

import static bff.TestExtensions.anyLoanPaymentRequestInput
import static bff.TestExtensions.validAccessToken
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

        def sdkResponse = TestExtensions.anyPaymentResponse(supplierOrderId, 2456, 5624,
                moneyResponse, loanResponse, invoiceResponse)

        def sdkRequest = TestExtensions.anyPaymentRequest(
                supplierOrderId,
                sdkResponse.customerUserId,
                sdkResponse.supplierId,
                invoiceResponse.code,
                BigDecimal.TEN)

        def expectedResponse = LoanPayment.fromSdk(sdkResponse)

        when(bnPlSdk.payWithLoan(eq(sdkRequest), eq(token))).thenReturn(Mono.just(sdkResponse))

        def response = sut.loanPayment(anyLoanPaymentRequestInput(
                token,
                sdkRequest.supplierId,
                supplierOrderId,
                sdkRequest.invoiceCode,
                sdkRequest.invoiceFileId,
                sdkRequest.amount)).get()

        assert response == expectedResponse
    }
}
