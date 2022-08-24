package bff.bridge

import bff.bridge.http.DigitalPaymentsBridgeImpl
import bff.configuration.CacheConfigurationProperties
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.Provider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono

import static bff.TestExtensions.randomString

@RunWith(MockitoJUnitRunner.class)
class DigitalPaymentsBridgeTest {

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @Mock
    DigitalPaymentsSdk digitalPaymentsSdk

    @InjectMocks
    DigitalPaymentsBridge digitalPaymentsBridge = new DigitalPaymentsBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.providers).thenReturn(10L)
        digitalPaymentsBridge.init()
    }

    @Test
    void 'Should return cached payment providers'() {
        def anySupplierId = randomString()
        def accessToken = randomString()
        def providers = [Provider.JP_MORGAN]

        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken).block()
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken).block()

        assert otherActual == actual
        assert actual == providers.toList()

        Mockito.verify(digitalPaymentsSdk, Mockito.times(1)).getPaymentProviders(anySupplierId, accessToken)
    }
}
