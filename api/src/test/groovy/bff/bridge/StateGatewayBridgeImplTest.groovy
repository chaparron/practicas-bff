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

@RunWith(MockitoJUnitRunner.class)
class StateGatewayBridgeImplTest extends StateGatewayBridgeImplTestData {

    @Mock
    private HttpBridge httpBridge

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    StateGatewayBridgeImpl stateBridge = new StateGatewayBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.states).thenReturn(1L)
        stateBridge.regionalConfigUrl = new URI("http://localhost:3000/")
        stateBridge.init()
    }

    @Test
    void getByCountryIdTest() {
        Mockito.when(
                httpBridge.get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull()))
                .thenReturn(
                        new JsonSlurper().parseText(stateEsStr) as Map)

        def response = stateBridge.getByCountryId("es")
        Assert.assertNotNull(response)
        Assert.assertFalse(response.empty)
        Assert.assertTrue(response.size() == 2)

        response = stateBridge.getByCountryId("es")
        Assert.assertNotNull(response)
        Assert.assertFalse(response.empty)
        Assert.assertTrue(response.size() == 2)

        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull())
    }


}
