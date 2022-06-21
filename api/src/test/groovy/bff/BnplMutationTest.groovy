package bff

import bff.model.BnplMutation
import bff.model.LoanPayment
import bff.model.Money
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.InvoiceResponse
import bnpl.sdk.model.LoanResponse
import bnpl.sdk.model.LoanStatus
import bnpl.sdk.model.MoneyResponse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono

import java.time.Instant

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
        def orderId = 11111L
        def token = validAccessToken()
        def paymentId = UUID.randomUUID()
        def created = Instant.now()
        def dueDate = Instant.now().plusSeconds(200)
        def moneyResponse = new MoneyResponse("ARS", BigDecimal.TEN)
        def loanId = UUID.randomUUID()
        def loanCreated = Instant.now()
        def loanResponse = new LoanResponse(loanId, loanCreated, null, null, dueDate, LoanStatus.APPROVED)
        def invoiceId = UUID.randomUUID()
        def invoiceResponse = new InvoiceResponse(invoiceId, "code")

        def sdkResponse = TestExtensions.anyPaymentResponse(paymentId, orderId, "externalId", "customerId", "supplierId", created,
                moneyResponse, loanResponse, invoiceResponse)

        def sdkRequest = TestExtensions.anyPaymentRequest(orderId,
                "2456",
                "supplierId",
                "code",
                "ARS",
                BigDecimal.TEN)

        def expectedResponse = LoanPayment.fromSdk(sdkResponse)

        when(bnPlSdk.payWithLoan(eq(sdkRequest), eq(token))).thenReturn(Mono.just(sdkResponse))

        def response = sut.loanPayment(TestExtensions.anyLoanPaymentRequestInput(token, "supplierId", orderId, "code",
                new Money("ARS", BigDecimal.TEN))).get()

        assert response == expectedResponse
    }
}
