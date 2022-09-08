package bff.bridge


import bff.bridge.http.PaymentsBridgeImpl
import bff.configuration.CacheConfigurationProperties
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.dto.type.PaymentMethod
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse
import wabi2b.payments.common.model.response.SupplierOrderPaymentResponse
import wabi2b.payments.sdk.client.WabiPaymentSdkClient

import static bff.TestExtensions.randomLong
import static bff.TestExtensions.randomString

@RunWith(MockitoJUnitRunner.class)
class PaymentsBridgeTest {

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @Mock
    WabiPaymentSdkClient wabiPaymentSdkClient

    @InjectMocks
    PaymentsBridge paymentBridge = new PaymentsBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.supplierOrderPayments).thenReturn(10L)
        paymentBridge.init()
    }

    @Test
    void 'Should return cached payment response for a supplier order'() {
        def request = new GetSupplierOrderPaymentRequest(randomLong())
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("50"), somePayments)
        def accessToken = randomString()

        Mockito.when(wabiPaymentSdkClient.getSupplierOrderPayments(request, accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def actual = paymentBridge.getSupplierOrderPayments(request, accessToken)
        def otherActual = paymentBridge.getSupplierOrderPayments(request, accessToken)

        assert otherActual == actual
        assert actual == supplierOrderPayments

        Mockito.verify(wabiPaymentSdkClient, Mockito.times(1)).getSupplierOrderPayments(request, accessToken)
    }
}
