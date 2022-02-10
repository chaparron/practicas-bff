package bff.bridge

import bff.bridge.data.CountryGatewayBridgeImplTestData
import bff.bridge.http.CountryGatewayBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.mapper.CountryMapper
import bff.model.LegalUrlType
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.context.support.AbstractMessageSource
import reactor.core.publisher.Mono
import wabi2b.sdk.regional.RegionalConfigSdk

import java.text.MessageFormat

@RunWith(MockitoJUnitRunner.class)
class CountryGatewayBridgeImplTest extends CountryGatewayBridgeImplTestData {

    @Mock
    private RegionalConfigSdk regionalConfigSdk

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private CountryGatewayBridgeImpl countryBridge = new CountryGatewayBridgeImpl()

    CountryMapper countryMapper = new CountryMapper()

    private static final JWT = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ0ZXN0QHRlc3QucnUiLCJzY29wZSI6WyJhbGwiXSwidG9zIjp7InVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6bnVsbCwiZmlyc3ROYW1lIjpudWxsLCJsYXN0TmFtZSI6bnVsbCwicGhvbmUiOm51bGwsImNyZWRlbnRpYWxzIjpudWxsLCJwcm9maWxlcyI6bnVsbCwiY291bnRyaWVzIjpudWxsLCJjcmVhdGVkIjpudWxsLCJhY2NlcHRXaGF0c0FwcCI6dHJ1ZX0sImFjY2VwdGVkIjoxNjEzODA5OTA5MDAwfSwiZW50aXR5SWQiOiIxNTU4NSIsInN0YXRlIjpudWxsLCJleHAiOjE2MjE0NzUyODQsInVzZXIiOnsiaWQiOjE3NDk3LCJ1c2VybmFtZSI6InRlc3RAdGVzdC5ydSIsInByb2ZpbGVzIjpbeyJpZCI6OCwibmFtZSI6IkZFX0NVU1RPTUVSIiwiYXV0aG9yaXRpZXMiOm51bGx9XSwiZmlyc3ROYW1lIjoi0KLRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiIwZjY0MGMzNy05NDNkLTQ0MmQtODM5Mi00YTU2ZmMxYzFkYWYiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.2VUXTAK1PdhtTaqmF7cZC3bElKJ_cRZ9AWsk54Jx4b8"

    @Before
    void init() {
        Mockito.when(cacheConfiguration.countries).thenReturn(1L)
        countryMapper.messageSource = new AbstractMessageSource() {
            protected MessageFormat resolveCode(String code, Locale locale) {
                return new MessageFormat("")
            }
        }
        countryBridge.countryMapper = countryMapper
        countryBridge.init()
    }

    @Test
    void getCountryConfiguration() {
        Mockito.when(regionalConfigSdk.findCountryConfig("es")).thenReturn(Mono.just(countryServiceResponseEs))
        def countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size() == 3)

        countryConfigs = countryBridge.getCountryConfiguration("es")
        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size() == 3)

        Mockito.verify(regionalConfigSdk, Mockito.times(1)).findCountryConfig("es")
    }

    @Test
    void getCountryConfiguration_NoResponse() {
        Mockito.when(regionalConfigSdk.findCountryConfig("es")).thenReturn(Mono.justOrEmpty(Optional.empty()))
        def countryConfigs = countryBridge.getCountryConfiguration("es")

        Assert.assertNull(countryConfigs)
    }

    @Test
    void getCustomerCountryConfiguration() {
        Mockito.when(regionalConfigSdk.findCountryConfig("ru")).thenReturn(Mono.just(countryServiceResponseEs))
        def countryConfigs = countryBridge.getCustomerCountryConfiguration(JWT)

        Assert.assertNotNull(countryConfigs)
        Assert.assertFalse(countryConfigs.empty)
        Assert.assertTrue(countryConfigs.size() == 3)
    }

    @Test
    void getCustomerCountryConfiguration_NoResponse() {
        Mockito.when(regionalConfigSdk.findCountryConfig("ru")).thenReturn(Mono.justOrEmpty(Optional.empty()))
        def countryConfigs = countryBridge.getCustomerCountryConfiguration(JWT)

        Assert.assertNull(countryConfigs)
    }

    @Test
    void getHomeCountries() {
        Mockito.when(regionalConfigSdk.findCountries(true)).thenReturn(Mono.just(
                [regionalCountryEs, regionalCountryAr]
        ))

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 2)

        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 2)
    }

    @Test
    void checkHomeCountriesOrderByCountryName() {
        Mockito.when(regionalConfigSdk.findCountries(true)).thenReturn(Mono.just(homeCountriesResponse))

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egipto", countriesHome.find { it.id == "eg" }?.name)
        Assert.assertEquals("Marruecos", countriesHome.find { it.id == "ma" }?.name)
        Assert.assertEquals("Filipinas", countriesHome.find { it.id == "ph" }?.name)


        countriesHome = countryBridge.getHomeCountries("es-ES")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egipto", countriesHome.find { it.id == "eg" }?.name)
        Assert.assertEquals("Marruecos", countriesHome.find { it.id == "ma" }?.name)
        Assert.assertEquals("Filipinas", countriesHome.find { it.id == "ph" }?.name)

        countriesHome = countryBridge.getHomeCountries("gb")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("Egypt", countriesHome.find { it.id == "eg" }?.name)
        Assert.assertEquals("Morocco", countriesHome.find { it.id == "ma" }?.name)
        Assert.assertEquals("Philippines", countriesHome.find { it.id == "ph" }?.name)

        countriesHome = countryBridge.getHomeCountries("ar")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("مصر", countriesHome.find { it.id == "eg" }?.name)
        Assert.assertEquals("المغرب", countriesHome.find { it.id == "ma" }?.name)
        Assert.assertEquals("فيلبيني", countriesHome.find { it.id == "ph" }?.name)

        countriesHome = countryBridge.getHomeCountries("ar-AR")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)
        Assert.assertEquals("مصر", countriesHome.find { it.id == "eg" }?.name)
        Assert.assertEquals("eg", countriesHome.find { it.id == "eg" }?.id)
        Assert.assertEquals("Africa/Cairo", countriesHome.find { it.id == "eg" }?.detail?.timezone)
        Assert.assertEquals("TIN", countriesHome.find { it.id == "eg" }?.legalDocumentInformation?.id)
        Assert.assertEquals("D*", countriesHome.find { it.id == "eg" }?.legalDocumentInformation?.mask)
        Assert.assertEquals("^[a-zA-Z0-9]*\$", countriesHome.find { it.id == "eg" }?.legalDocumentInformation?.maskRegex)

        Assert.assertEquals("المغرب", countriesHome.find { it.id == "ma" }?.name)
        Assert.assertEquals("ma", countriesHome.find { it.id == "ma" }?.id)
        Assert.assertEquals("Africa/Casablanca", countriesHome.find { it.id == "ma" }?.detail?.timezone)
        Assert.assertEquals("ICE", countriesHome.find { it.id == "ma" }?.legalDocumentInformation?.id)
        Assert.assertEquals("000000000000000", countriesHome.find { it.id == "ma" }?.legalDocumentInformation?.mask)
        Assert.assertEquals("^\\d{15}\$", countriesHome.find { it.id == "ma" }?.legalDocumentInformation?.maskRegex)

        Assert.assertEquals("فيلبيني", countriesHome.find { it.id == "ph" }?.name)
        Assert.assertEquals("ph", countriesHome.find { it.id == "ph" }?.id)
        Assert.assertEquals("Asia/Manila", countriesHome.find { it.id == "ph" }?.detail?.timezone)
        Assert.assertEquals("TIN", countriesHome.find { it.id == "ph" }?.legalDocumentInformation?.id)
        Assert.assertEquals("000000009999", countriesHome.find { it.id == "ph" }?.legalDocumentInformation?.mask)
        Assert.assertEquals("^\\d{8,12}\$", countriesHome.find { it.id == "ph" }?.legalDocumentInformation?.maskRegex)

        Mockito.verify(regionalConfigSdk, Mockito.times(3)).findCountries(true)
    }

    @Test
    void getCountryNewResponseMap() {
        Mockito.when(regionalConfigSdk.findCountry("ru")).thenReturn(Mono.just(publicCountryResponse))

        def country = countryBridge.getCountry("ru")
        Assert.assertNotNull(country)
        Assert.assertNotNull(country.legalUrls)
        Assert.assertNotNull(country.detail)
        Assert.assertNotNull(country.language)
        Assert.assertNotNull(country.language.translations)
        Assert.assertNotNull(country.contactInfo)
        Assert.assertNotNull(country.currency)
        Assert.assertNotNull(country.fee)
        Assert.assertNotNull(country.wabiPay)
        Assert.assertNotNull(country.legalDocumentInformation)

        Assert.assertEquals("ru", country.id)
        Assert.assertEquals("ru-RU", country.language.locale)
        Assert.assertEquals(8, country.language.translations.size())
        Assert.assertEquals("WABICREDITS_PERCENTAGE", country.fee.serviceFeeType)
        Assert.assertEquals("+7", country.detail.countryCode)

        Mockito.verify(regionalConfigSdk, Mockito.times(1)).findCountry("ru")
    }

    @Test
    void 'check legal urls'() {
        Mockito.when(regionalConfigSdk.findCountries(true)).thenReturn(Mono.just(homeCountriesResponse))

        def countriesHome = countryBridge.getHomeCountries("es")
        Assert.assertNotNull(countriesHome)
        Assert.assertFalse(countriesHome.empty)
        Assert.assertTrue(countriesHome.size() == 3)

        def countryEG = countriesHome.find { it.id == "eg" }
        Assert.assertEquals("Egipto", countryEG?.name)
        Assert.assertNotNull(countryEG?.legalUrls)
        Assert.assertFalse(countryEG?.legalUrls?.isEmpty())
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.TERMS_AND_CONDITIONS })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.PRIVACY_POLICY })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.COOKIES })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.FAQS })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.ABOUT })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.OPERATION })
        Assert.assertNotNull(countryEG?.legalUrls?.find { it?.type == LegalUrlType.COMPLAINT })

        def countryMA = countriesHome.find { it.id == "ma" }
        Assert.assertEquals("Marruecos", countryMA?.name)
        Assert.assertNotNull(countryMA?.legalUrls)
        Assert.assertTrue(countryMA?.legalUrls?.isEmpty())

        Mockito.verify(regionalConfigSdk, Mockito.times(1)).findCountries(true)
    }
}
