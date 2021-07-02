package bff.bridge

import bff.bridge.http.CountryGatewayBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.service.HttpBridge
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

@RunWith(MockitoJUnitRunner.class)
class CountryGatewayBridgeImplTest {

    @Mock
    private HttpBridge httpBridge

    @Mock
    CacheConfigurationProperties cacheConfiguration

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

    @Before
    void init() {
        Mockito.when(cacheConfiguration.countries).thenReturn(1L)
        countryBridge.countryUrl = new URI("http://localhost:3000/")
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

        countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size()==2)

        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String)ArgumentMatchers.isNull())
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
                        (URI) ArgumentMatchers.any(URI.class),
                        ArgumentMatchers.anyString()))
                .thenReturn(
                        new JsonSlurper().parseText(countryEsPublicStr) as Map)

        def countryConfigs = countryBridge.getCustomerCountryConfiguration("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0ZXN0QHRlc3QucnUiLCJzY29wZSI6WyJhbGwiXSwidG9zIjp7InVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6bnVsbCwiZmlyc3ROYW1lIjpudWxsLCJsYXN0TmFtZSI6bnVsbCwicGhvbmUiOm51bGwsImNyZWRlbnRpYWxzIjpudWxsLCJwcm9maWxlcyI6bnVsbCwiY291bnRyaWVzIjpudWxsLCJjcmVhdGVkIjpudWxsLCJhY2NlcHRXaGF0c0FwcCI6dHJ1ZX0sImFjY2VwdGVkIjoxNjEzODA5OTA5MDAwfSwiZW50aXR5SWQiOiIxNTU4NSIsInN0YXRlIjpudWxsLCJleHAiOjE2MjE0NzUyODQsInVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6InRlc3RAdGVzdC5ydSIsInByb2ZpbGVzIjpbeyJpZCI6OCwibmFtZSI6IkZFX0NVU1RPTUVSIiwiYXV0aG9yaXRpZXMiOm51bGx9XSwiZmlyc3ROYW1lIjoi0KLRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiIwZjY0MGMzNy05NDNkLTQ0MmQtODM5Mi00YTU2ZmMxYzFkYWYiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.2VUXTAK1PdhtTaqmF7cZC3bElKJ_cRZ9AWsk54Jx4b8")

        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size()==2)
    }

    @Test
    void getCustomerCountryConfiguration_NoResponse() {
        Mockito.when(
                httpBridge.get(
                        (URI)ArgumentMatchers.any(URI.class),
                        ArgumentMatchers.anyString()))
                .thenReturn(null)

        def countryConfigs = countryBridge.getCustomerCountryConfiguration("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0ZXN0QHRlc3QucnUiLCJzY29wZSI6WyJhbGwiXSwidG9zIjp7InVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6bnVsbCwiZmlyc3ROYW1lIjpudWxsLCJsYXN0TmFtZSI6bnVsbCwicGhvbmUiOm51bGwsImNyZWRlbnRpYWxzIjpudWxsLCJwcm9maWxlcyI6bnVsbCwiY291bnRyaWVzIjpudWxsLCJjcmVhdGVkIjpudWxsLCJhY2NlcHRXaGF0c0FwcCI6dHJ1ZX0sImFjY2VwdGVkIjoxNjEzODA5OTA5MDAwfSwiZW50aXR5SWQiOiIxNTU4NSIsInN0YXRlIjpudWxsLCJleHAiOjE2MjE0NzUyODQsInVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6InRlc3RAdGVzdC5ydSIsInByb2ZpbGVzIjpbeyJpZCI6OCwibmFtZSI6IkZFX0NVU1RPTUVSIiwiYXV0aG9yaXRpZXMiOm51bGx9XSwiZmlyc3ROYW1lIjoi0KLRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiIwZjY0MGMzNy05NDNkLTQ0MmQtODM5Mi00YTU2ZmMxYzFkYWYiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.2VUXTAK1PdhtTaqmF7cZC3bElKJ_cRZ9AWsk54Jx4b8")

        Assert.assertNull(countryConfigs)
    }
}
