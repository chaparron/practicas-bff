package bff

import bff.model.CreateDigitalPaymentFailedReason
import bff.model.DigitalPayment
import bff.model.DigitalPaymentFailed
import bff.model.DigitalPaymentFailedReason
import bff.model.DigitalPaymentMutation
import bff.model.JpMorganCreateDigitalPayment
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.CreatePaymentRequest
import digitalpayments.sdk.model.UpdatePaymentRequest
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import wabi.sdk.impl.CustomSdkException
import wabi.sdk.impl.DetailedError

import static bff.TestExtensions.*
import static org.mockito.Mockito.*


class DigitalPaymentMutationTest {

    def digitalPaymentSdk = mock(DigitalPaymentsSdk)

    def sut = new DigitalPaymentMutation(
            digitalPaymentsSdk: digitalPaymentSdk
    )

    @Before
    void setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    void 'should create digital payment'() {
        def supplierOrderId = randomLong()
        def amount = randomBigDecimal()
        def invoiceId = randomString()
        def accessToken = validAccessToken()
        def sdkResponse = anyCreatePaymentResponse()
        def sdkRequest = new CreatePaymentRequest(supplierOrderId, amount, invoiceId)

        def expectedResponse = JpMorganCreateDigitalPayment.fromSdk(sdkResponse)

        when(digitalPaymentSdk.createPayment(sdkRequest, accessToken)).thenReturn(Mono.just(sdkResponse))

        def actualResponse = sut.createDigitalPayment(
                anyCreateDigitalPaymentInput(supplierOrderId, amount, accessToken, invoiceId)
        ).get()

        assert expectedResponse == actualResponse
        verify(digitalPaymentSdk).createPayment(sdkRequest, accessToken)
    }

    @Test
    void 'should return sdk error when sdk fail on create payment'() {
        def supplierOrderId = randomLong()
        def amount = randomBigDecimal()
        def invoiceId = randomString()
        def accessToken = validAccessToken()
        def sdkRequest = new CreatePaymentRequest(supplierOrderId, amount, invoiceId)

        def sdkException = new CustomSdkException(new DetailedError(randomString(), randomString()))

        when(digitalPaymentSdk.createPayment(sdkRequest, accessToken)).thenReturn(Mono.error(sdkException))

        def actualResponse = sut.createDigitalPayment(
                anyCreateDigitalPaymentInput(supplierOrderId, amount, accessToken, invoiceId)
        ).get()

        assert DigitalPaymentFailedReason.UNKNOWN.build(sdkException.getError().getDetail()) == actualResponse
        verify(digitalPaymentSdk).createPayment(sdkRequest, accessToken)
    }

    @Test
    void 'should finalize digital payment'() {
        def paymentId = randomLong()
        def supplierOrderId = randomLong()
        def amount = randomBigDecimal()
        def responseCode = randomString()
        def message = randomString()
        def encData = randomString()
        def accessToken = validAccessToken()
        def sdkRequest = new UpdatePaymentRequest(encData)

        def sdkResponse = anyUpdatePaymentResponse(
                paymentId,
                supplierOrderId,
                amount,
                responseCode,
                message
        )

        def expectedResponse = DigitalPayment.fromSdk(sdkResponse)

        when(digitalPaymentSdk.updatePayment(sdkRequest, accessToken)).thenReturn(Mono.just(sdkResponse))

        def actualResponse = sut.finalizeDigitalPayment(
                anyFinalizeDigitalPaymentInput(encData, accessToken)
        ).get()

        assert expectedResponse == actualResponse
        verify(digitalPaymentSdk, times(1)).updatePayment(sdkRequest, accessToken)
    }

    @Test
    void 'should return sdk error when sdk fail on finalize payment'() {
        def sdkException = new CustomSdkException(new DetailedError(randomString(), randomString()))
        def expectedResponse =  DigitalPaymentFailedReason.UNKNOWN.build(sdkException.getError().getDetail())
        def accessToken = validAccessToken()
        def encData = randomString()
        def sdkRequest = new UpdatePaymentRequest(encData)

        when(digitalPaymentSdk.updatePayment(any(), any())).thenReturn(Mono.error(sdkException))

        def actualResponse = sut.finalizeDigitalPayment(
                anyFinalizeDigitalPaymentInput(encData, accessToken)
        ).get()
        assert expectedResponse == actualResponse
        verify(digitalPaymentSdk, times(1)).updatePayment(sdkRequest, accessToken)
    }
}
