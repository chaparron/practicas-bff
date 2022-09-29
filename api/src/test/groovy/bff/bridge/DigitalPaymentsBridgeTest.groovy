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
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono
import wabi2b.sdk.featureflags.FeatureFlagsSdk

import static bff.TestExtensions.*
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner.class)
class DigitalPaymentsBridgeTest {

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @Mock
    DigitalPaymentsSdk digitalPaymentsSdk

    @Mock
    FeatureFlagsSdk featureFlagsSdk

    @InjectMocks
    DigitalPaymentsBridge digitalPaymentsBridge = new DigitalPaymentsBridgeImpl()

    private static String JPMC_ENABLED_FEATURE_FLAG_KEY = "JPMC_ENABLED"

    @Before
    void init() {
        when(cacheConfiguration.providers).thenReturn(10L)
        digitalPaymentsBridge.init()
    }

    @Test
    void 'when calling twice with same supplierId and accessToken and feature flag enabled should call bridge once'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def providers = [Provider.JP_MORGAN]

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)

        assert otherActual == actual
        assert actual == providers.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
    }

    @Test
    void 'when calling twice with different supplierId and same accessToken and feature flag enabled should call bridge twice'() {
        def anySupplierId = randomString()
        def anotherSupplierId = randomString()
        def accessToken = validAccessToken()

        def providers = [Provider.JP_MORGAN]
        def anotherProviders = []

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))
        when(digitalPaymentsSdk.getPaymentProviders(anotherSupplierId, accessToken)).thenReturn(Mono.just(anotherProviders))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anotherSupplierId, accessToken)

        assert otherActual == anotherProviders
        assert actual == providers.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
        verify(digitalPaymentsSdk).getPaymentProviders(anotherSupplierId, accessToken)
    }

    @Test
    void 'when calling twice with same supplierId and different accessToken and feature flag enabled should call bridge twice'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def anotherAccessToken = anotherValidAccessToken()

        def providers = [Provider.JP_MORGAN]
        def anotherProviders = []

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, accessToken)).thenReturn(Mono.just(providers))
        when(digitalPaymentsSdk.getPaymentProviders(anySupplierId, anotherAccessToken)).thenReturn(Mono.just(anotherProviders))

        def actual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentProviders(anySupplierId, anotherAccessToken)

        assert otherActual == anotherProviders
        assert actual == providers.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, accessToken)
        verify(digitalPaymentsSdk).getPaymentProviders(anySupplierId, anotherAccessToken)
    }

    @Test
    void 'when feature flag is disabled then return empty list'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(false)

        def result = digitalPaymentsBridge.getPaymentProviders(anySupplierId, accessToken)
        assert result.isEmpty()

        verify(featureFlagsSdk).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verifyZeroInteractions(digitalPaymentsSdk)
    }
}
