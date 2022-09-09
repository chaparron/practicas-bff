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

import static bff.TestExtensions.anotherValidAccessToken
import static bff.TestExtensions.randomString
import static bff.TestExtensions.validAccessToken

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
    void 'when calling twice with same supplierId and accessToken should call bridge once'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def providers = [Provider.JP_MORGAN]

        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)

        assert otherActual == actual
        assert actual == providers.toList()

        Mockito.verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
    }

    @Test
    void 'when calling twice with different supplierId and same accessToken should call bridge twice'() {
        def anySupplierId = randomString()
        def anotherSupplierId = randomString()
        def accessToken = validAccessToken()

        def providers = [Provider.JP_MORGAN]
        def anotherProviders = []

        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))
        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anotherSupplierId, accessToken)).thenReturn(Mono.just(anotherProviders))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anotherSupplierId, accessToken)

        assert otherActual == anotherProviders
        assert actual == providers.toList()

        Mockito.verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
        Mockito.verify(digitalPaymentsSdk).getPaymentProviders(anotherSupplierId, accessToken)
    }

    @Test
    void 'when calling twice with same supplierId and different accessToken should call bridge twice'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def anotherAccessToken = anotherValidAccessToken()

        def providers = [Provider.JP_MORGAN]
        def anotherProviders = []

        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))
        Mockito.when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, anotherAccessToken)).thenReturn(Mono.just(anotherProviders))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, anotherAccessToken)

        assert otherActual == anotherProviders
        assert actual == providers.toList()

        Mockito.verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
        Mockito.verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, anotherAccessToken)
    }
}
