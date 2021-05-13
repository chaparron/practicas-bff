package bff.bridge

import bff.bridge.http.CountryGatewayBridgeImpl
import bff.service.HttpBridge
import bff.service.ServiceDiscovery
import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.web.util.UriComponentsBuilder

@RunWith(MockitoJUnitRunner.class)
class CountryGatewayBridgeImplTest {

    @Mock
    private HttpBridge httpBridge

    @Mock
    ServiceDiscovery serviceDiscovery

    @InjectMocks
    private CountryGatewayBridgeImpl countryBridge = new CountryGatewayBridgeImpl()

    private static String countryEsPublicStr =
            "{\n" +
            "    \"id\": \"es\",\n" +
            "    \"config\": [\n" +
            "        {\n" +
            "            \"key\": \"name\",\n" +
            "            \"value\": \"España\",\n" +
            "            \"private\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"locale\",\n" +
            "            \"value\": \"es_ES\",\n" +
            "            \"private\": false\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    private static String countryArPublicStr =
            "{\n" +
            "    \"id\": \"ar\",\n" +
            "    \"config\": [\n" +
            "        {\n" +
            "            \"key\": \"name\",\n" +
            "            \"value\": \"Argentina\",\n" +
            "            \"private\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"key\": \"locale\",\n" +
            "            \"value\": \"es_AR\",\n" +
            "            \"private\": false\n" +
            "        }\n" +
            "    ]\n" +
            "}"

    @Before
    void init() {
        countryBridge.countryServiceName = "RegionalConfigService"
        Mockito.when(serviceDiscovery.discover("RegionalConfigService")).thenReturn("http://localhost:3000/")
        countryBridge.init()
    }

    @Test
    void getCountryConfiguration() {
        Mockito.when(
                httpBridge.get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String)ArgumentMatchers.isNull()))
                .thenReturn(
                        new JsonSlurper().parseText(countryEsPublicStr) as Map)

        def countryConfigs = countryBridge.getCountryConfiguration("es")

        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size()==2)
    }

    @Test
    void getCountryConfiguration_NoResponse() {
        Mockito.when(
                httpBridge.get(
                        (URI)ArgumentMatchers.any(URI.class),
                        (String)ArgumentMatchers.isNull()))
                .thenReturn(null)

        def countryConfigs = countryBridge.getCountryConfiguration("es")

        Assert.assertNull(countryConfigs)
    }

    @Test
    void getCustomerCountryConfiguration() {
        Mockito.when(
                httpBridge.get(
                        (URI)ArgumentMatchers.any(URI.class),
                        (String)ArgumentMatchers.any(String.class)))
                .thenReturn(
                        new JsonSlurper().parseText(countryEsPublicStr) as Map)

        def countryConfigs = countryBridge.getCustomerCountryConfiguration("12345")

        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size()==2)
    }

    @Test
    void getCustomerCountryConfiguration_NoResponse() {
        Mockito.when(
                httpBridge.get(
                        (URI)ArgumentMatchers.any(URI.class),
                        (String)ArgumentMatchers.any(String.class)))
                .thenReturn(null)

        def countryConfigs = countryBridge.getCustomerCountryConfiguration("12345")

        Assert.assertNull(countryConfigs)
    }
}
