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
            "            \"key\": \"name-en\",\n" +
            "            \"value\": \"Spain\",\n" +
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
            "            \"key\": \"name-en\",\n" +
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

    private static String homeCountriesResponse = "[\n" +
            "  {\n" +
            "    \"id\": \"eg\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Egipto\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"Egipto\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Egypt\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300eg.png\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"tyc\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-T-C?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"pp\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-P-P?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"cookies\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/article/Wabi2b-Store-Egypt-P-C?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"faqs\",\n" +
            "        \"value\": \"https://wabi.force.com/wabi2b/s/topic/0TO2E00000029SOWAY/preguntas-frecuentes?language=ar_EG\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ph\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Philippines\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"فيلبيني\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Philippines\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300ph.png\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"tyc\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Philipines-T-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"pp\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Philipines-P-P?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"cookies\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Algeria-P-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ma\",\n" +
            "    \"config\": [\n" +
            "      {\n" +
            "        \"key\": \"name\",\n" +
            "        \"value\": \"Morocco\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-ar\",\n" +
            "        \"value\": \"المغرب\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"name-en\",\n" +
            "        \"value\": \"Morocco\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"flag\",\n" +
            "        \"value\": \"7ab0fd14-efa9-11eb-9a03-0242ac1300ma.png\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"tyc\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Morocco-T-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"pp\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Morocco-P-P?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"key\": \"cookies\",\n" +
            "        \"value\": \"https://wabi2u.force.com/wabi2b/s/article/Wabi2b-Store-Morocco-P-C?language=en_US\",\n" +
            "        \"private\": false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"enabled\": true\n" +
            "  }]"
    @Before
    void init() {
        Mockito.when(cacheConfiguration.countries).thenReturn(1L)
        countryBridge.regionalConfigUrl = new URI("http://localhost:3000/")
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
        Assert.assertTrue(countryConfigs.size()==3)

        countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size()==3)

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
        Assert.assertTrue(countryConfigs.size()==3)
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

    @Test
    void getHomeCountries() {
        Mockito.when(
                httpBridge.get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        new JsonSlurper().parseText("[$countryEsPublicStr,$countryArPublicStr]") as List)

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==2)

        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==2)


        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }

    @Test
    void checkHomeCountriesOrderByCountryName() {
        Mockito.when(
                httpBridge.get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        new JsonSlurper().parseText(homeCountriesResponse) as List)

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==3)
        Assert.assertEquals("Egypt",countriesHome.get(0).name)
        Assert.assertEquals("Morocco",countriesHome.get(1).name)
        Assert.assertEquals("Philippines",countriesHome.get(2).name)


        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==3)
        Assert.assertEquals("Egypt",countriesHome.get(0).name)
        Assert.assertEquals("Morocco",countriesHome.get(1).name)
        Assert.assertEquals("Philippines",countriesHome.get(2).name)

        countriesHome = countryBridge.getHomeCountries("ar")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==3)
        Assert.assertEquals("Egipto",countriesHome.get(0).name)
        Assert.assertEquals("المغرب",countriesHome.get(1).name)
        Assert.assertEquals("فيلبيني",countriesHome.get(2).name)


        countriesHome = countryBridge.getHomeCountries("ar-AR")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size()==3)
        Assert.assertEquals("Egipto",countriesHome.get(0).name)
        Assert.assertEquals("المغرب",countriesHome.get(1).name)
        Assert.assertEquals("فيلبيني",countriesHome.get(2).name)


        Mockito.verify(httpBridge, Mockito.times(2))
                .get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }
}
