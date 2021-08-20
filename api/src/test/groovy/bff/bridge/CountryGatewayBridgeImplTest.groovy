package bff.bridge

import bff.bridge.data.CountryGatewayBridgeImplTestData
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
class CountryGatewayBridgeImplTest extends CountryGatewayBridgeImplTestData {

    @Mock
    private HttpBridge httpBridge

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private CountryGatewayBridgeImpl countryBridge = new CountryGatewayBridgeImpl()

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
                        (String) ArgumentMatchers.isNull()))
                .thenReturn(
                        new JsonSlurper().parseText(countryEsPublicStr) as Map)

        def countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size() == 3)

        countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size() == 3)

        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String) ArgumentMatchers.isNull())
    }

    @Test
    void getCountryConfiguration_NoResponse() {
        Mockito.when(
                httpBridge.get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String) ArgumentMatchers.isNull()))
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
        Assert.assertTrue(countryConfigs.size() == 3)
    }

    @Test
    void getCustomerCountryConfiguration_NoResponse() {
        Mockito.when(
                httpBridge.get(
                        (URI) ArgumentMatchers.any(URI.class),
                        ArgumentMatchers.anyString()))
                .thenReturn(null)

        def countryConfigs = countryBridge.getCustomerCountryConfiguration("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0ZXN0QHRlc3QucnUiLCJzY29wZSI6WyJhbGwiXSwidG9zIjp7InVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6bnVsbCwiZmlyc3ROYW1lIjpudWxsLCJsYXN0TmFtZSI6bnVsbCwicGhvbmUiOm51bGwsImNyZWRlbnRpYWxzIjpudWxsLCJwcm9maWxlcyI6bnVsbCwiY291bnRyaWVzIjpudWxsLCJjcmVhdGVkIjpudWxsLCJhY2NlcHRXaGF0c0FwcCI6dHJ1ZX0sImFjY2VwdGVkIjoxNjEzODA5OTA5MDAwfSwiZW50aXR5SWQiOiIxNTU4NSIsInN0YXRlIjpudWxsLCJleHAiOjE2MjE0NzUyODQsInVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6InRlc3RAdGVzdC5ydSIsInByb2ZpbGVzIjpbeyJpZCI6OCwibmFtZSI6IkZFX0NVU1RPTUVSIiwiYXV0aG9yaXRpZXMiOm51bGx9XSwiZmlyc3ROYW1lIjoi0KLRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiIwZjY0MGMzNy05NDNkLTQ0MmQtODM5Mi00YTU2ZmMxYzFkYWYiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.2VUXTAK1PdhtTaqmF7cZC3bElKJ_cRZ9AWsk54Jx4b8")

        Assert.assertNull(countryConfigs)
    }

    @Test
    void getHomeCountries() {
        Mockito.when(
                httpBridge.get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        new JsonSlurper().parseText("[$countryEsPublicStr,$countryArPublicStr]") as List)

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 2)

        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 2)


        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }

    @Test
    void checkHomeCountriesOrderByCountryName() {
        Mockito.when(
                httpBridge.get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        new JsonSlurper().parseText(homeCountriesResponse) as List)

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egypt", countriesHome.get(0).name)
        Assert.assertEquals("Morocco", countriesHome.get(1).name)
        Assert.assertEquals("Philippines", countriesHome.get(2).name)


        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egypt", countriesHome.get(0).name)
        Assert.assertEquals("Morocco", countriesHome.get(1).name)
        Assert.assertEquals("Philippines", countriesHome.get(2).name)

        countriesHome = countryBridge.getHomeCountries("ar")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egipto", countriesHome.get(0).name)
        Assert.assertEquals("المغرب", countriesHome.get(1).name)
        Assert.assertEquals("فيلبيني", countriesHome.get(2).name)


        countriesHome = countryBridge.getHomeCountries("ar-AR")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egipto", countriesHome.get(0).name)
        Assert.assertEquals("المغرب", countriesHome.get(1).name)
        Assert.assertEquals("فيلبيني", countriesHome.get(2).name)


        Mockito.verify(httpBridge, Mockito.times(2))
                .get(
                        (URI) Mockito.any(URI.class),
                        (String) Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }

    @Test
    void getCountryNewResponseMap() {

        Mockito.when(
                httpBridge.get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String) ArgumentMatchers.isNull()))
                .thenReturn(
                        new JsonSlurper().parseText(publicCountryResponse) as Map)

        def country = countryBridge.getCountry("ru")
        Assert.assertNotNull(country)

        country = countryBridge.getCountry("ar")
        Assert.assertNotNull(country)

        country = countryBridge.getCountry("ru")
        Assert.assertNotNull(country)
        Assert.assertNotNull(country.legalUrls)
        Assert.assertNotNull(country.detail)
        Assert.assertNotNull(country.language)
        Assert.assertNotNull(country.language.translations)
        Assert.assertNotNull(country.contactInfo)
        Assert.assertNotNull(country.currency)
        Assert.assertNotNull(country.fee)
        Assert.assertNotNull(country.wabiPay)

        Assert.assertEquals("ru-RU", country.language.locale)
        Assert.assertEquals(8, country.language.translations.size())
        Assert.assertEquals("WABICREDITS_PERCENTAGE", country.fee.serviceFeeType)
        Assert.assertEquals("+7", country.detail.phonePrefix)

        Mockito.verify(httpBridge, Mockito.times(2))
                .get(
                        (URI) ArgumentMatchers.any(URI.class),
                        (String) ArgumentMatchers.isNull())
    }
}
