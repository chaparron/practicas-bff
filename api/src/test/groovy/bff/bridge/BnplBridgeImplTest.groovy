package bff.bridge

import bff.TestExtensions
import bff.bridge.http.BnplBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bnpl.sdk.BnPlSdk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono

@RunWith(MockitoJUnitRunner.class)
class BnplBridgeImplTest {

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @Mock
    BnPlSdk bnPlSdk

    @InjectMocks
    private BnplBridgeImpl bnplBridge = new BnplBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.bnpl).thenReturn(1L)
        bnplBridge.init()
    }

    @Test
    void 'should return parsed supportedMinimumAmountResponse from cached response'() {
        def country = "in"
        def accessToken = "indianToken"
        def anotherToken = "anotherToken"
        def supportedMinimumAmountResponse = TestExtensions.anySupportedMinimumAmountResponse(country)

        Mockito.when(bnPlSdk.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(supportedMinimumAmountResponse))

        assert bnplBridge.supportedMinimumAmount(country, accessToken) == supportedMinimumAmountResponse
        assert bnplBridge.supportedMinimumAmount(country, anotherToken) == supportedMinimumAmountResponse

        Mockito.verify(bnPlSdk).supportedMinimumAmount(country, accessToken)
        Mockito.verifyNoMoreInteractions(bnPlSdk)
    }

    @Test
    void 'should return parsed supportedMinimumAmountResponse from uncached response'() {
        def country = "in"
        def secondCountry = "ru"
        def accessToken = "token"
        def supportedMinimumAmountResponse = TestExtensions.anySupportedMinimumAmountResponse(country)
        def secondSupportedMinimumAmountResponse = TestExtensions.anySupportedMinimumAmountResponse(secondCountry)

        Mockito.when(bnPlSdk.supportedMinimumAmount(country, accessToken)).thenReturn(Mono.just(supportedMinimumAmountResponse))
        Mockito.when(bnPlSdk.supportedMinimumAmount(secondCountry, accessToken)).thenReturn(Mono.just(secondSupportedMinimumAmountResponse))

        assert bnplBridge.supportedMinimumAmount(country, accessToken) == supportedMinimumAmountResponse
        assert bnplBridge.supportedMinimumAmount(secondCountry, accessToken) == secondSupportedMinimumAmountResponse

        Mockito.verify(bnPlSdk).supportedMinimumAmount(country, accessToken)
        Mockito.verify(bnPlSdk).supportedMinimumAmount(secondCountry, accessToken)
        Mockito.verifyNoMoreInteractions(bnPlSdk)
    }

}
