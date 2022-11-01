package bff.bridge

import bff.bridge.http.DigitalPaymentsBridgeImpl
import bff.configuration.CacheConfigurationProperties
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.PaymentOption
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
    void 'when calling twice with same supplierId and accessToken and feature flag is enabled, the first bridge call is cached'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def paymentOptions = [PaymentOption.ISG_DIGITAL_PAYMENT]

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentMethods(anySupplierId, accessToken)).thenReturn(Mono.just(paymentOptions))

        def actual = digitalPaymentsBridge.getPaymentMethods(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentMethods(anySupplierId, accessToken)

        assert otherActual == actual
        assert actual == paymentOptions.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentMethods(anySupplierId, accessToken)
    }

    @Test
    void 'when calling twice with different supplierId and same accessToken and feature flag is enabled should call bridge twice'() {
        def anySupplierId = randomString()
        def anotherSupplierId = randomString()
        def accessToken = validAccessToken()

        def paymentOptions = [PaymentOption.ISG_DIGITAL_PAYMENT]
        def anotherPaymentOptions = []

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentMethods(anySupplierId, accessToken)).thenReturn(Mono.just(paymentOptions))
        when(digitalPaymentsSdk.getPaymentMethods(anotherSupplierId, accessToken)).thenReturn(Mono.just(anotherPaymentOptions))

        def actual = digitalPaymentsBridge.getPaymentMethods(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentMethods(anotherSupplierId, accessToken)

        assert otherActual == anotherPaymentOptions
        assert actual == paymentOptions.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentMethods(anySupplierId, accessToken)
        verify(digitalPaymentsSdk).getPaymentMethods(anotherSupplierId, accessToken)
    }

    @Test
    void 'when calling twice with same supplierId and different accessToken and feature flag is enabled should call bridge twice'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()
        def anotherAccessToken = anotherValidAccessToken()

        def paymentOptions = [PaymentOption.ISG_DIGITAL_PAYMENT]
        def anotherPaymentOptions = []

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(true)
        when(digitalPaymentsSdk.getPaymentMethods(anySupplierId, accessToken)).thenReturn(Mono.just(paymentOptions))
        when(digitalPaymentsSdk.getPaymentMethods(anySupplierId, anotherAccessToken)).thenReturn(Mono.just(anotherPaymentOptions))

        def actual = digitalPaymentsBridge.getPaymentMethods(anySupplierId, accessToken)
        def otherActual = digitalPaymentsBridge.getPaymentMethods(anySupplierId, anotherAccessToken)

        assert otherActual == anotherPaymentOptions
        assert actual == paymentOptions.toList()

        verify(featureFlagsSdk, times(2)).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verify(digitalPaymentsSdk).getPaymentMethods(anySupplierId, accessToken)
        verify(digitalPaymentsSdk).getPaymentMethods(anySupplierId, anotherAccessToken)
    }

    @Test
    void 'when feature flag is disabled then return empty list'() {
        def anySupplierId = randomString()
        def accessToken = validAccessToken()

        when(featureFlagsSdk.isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)).thenReturn(false)

        def result = digitalPaymentsBridge.getPaymentMethods(anySupplierId, accessToken)
        assert result.isEmpty()

        verify(featureFlagsSdk).isActive(JPMC_ENABLED_FEATURE_FLAG_KEY)
        verifyZeroInteractions(digitalPaymentsSdk)
    }
}
