package bff.bridge

import bff.bridge.data.StateGatewayBridgeImplTestData
import bff.bridge.http.StateGatewayBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.service.HttpBridge
import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono
import wabi2b.sdk.regional.RegionalConfigSdk

@RunWith(MockitoJUnitRunner.class)
class StateGatewayBridgeImplTest extends StateGatewayBridgeImplTestData {

    @Mock
    private RegionalConfigSdk regionalConfigSdk

    @InjectMocks
    StateGatewayBridgeImpl stateBridge = new StateGatewayBridgeImpl()

    @Test
    void getByCountryIdTest() {
        Mockito.when(regionalConfigSdk.findStatesForCountry("es")).thenReturn(statesEs)

        def response = stateBridge.getByCountryId("es")
        Assert.assertNotNull(response)
        Assert.assertFalse(response.empty)
        Assert.assertTrue(response.size() == 2)

        Mockito.verify(regionalConfigSdk).findStatesForCountry("es")
    }

}
