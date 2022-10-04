package bff.mapper

import bff.model.ContactInfo
import bff.model.CountryTranslation
import bff.model.Currency
import bff.model.Detail
import bff.model.Fee
import bff.model.Language
import bff.model.LegalDocumentInformation
import bff.model.LegalUrl
import bff.model.LegalUrlType
import bff.model.WabiPay
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.context.MessageSource
import wabi2b.sdk.regional.BranchOfficeConfiguration
import wabi2b.sdk.regional.ContactInformation
import wabi2b.sdk.regional.Country
import wabi2b.sdk.regional.CurrencyInformation
import wabi2b.sdk.regional.FeeConfiguration
import wabi2b.sdk.regional.Geolocation
import wabi2b.sdk.regional.LegalLink
import wabi2b.sdk.regional.ShoppingConfiguration
import wabi2b.sdk.regional.Translation
import wabi2b.sdk.regional.WabipayConfiguration

import static org.junit.Assert.assertEquals

@RunWith(MockitoJUnitRunner.class)
class CountryMapperTest {

    @Mock
    private MessageSource messageSource

    @InjectMocks
    private CountryMapper mapper = new CountryMapper()

    @Test
    void 'get mapped country'() {
        Mockito.when(messageSource.getMessage(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("labelMsg")

        Country regionalConfigCountry = new Country(
                "ar",
                "Argentina",
                new ContactInformation(
                        "+541120400002",
                        "541161290635",
                        "0862000780"
                ),
                new CurrencyInformation(
                        "\$",
                        "ARS"
                ),
                "America/Argentina/Buenos_Aires",
                "7ab0fd14-efa9-11eb-9a03-0242ac1300ar.png",
                "+54",
                [
                        new LegalLink("tyc.com", "tyc"),
                        new LegalLink("pp.com", "pp"),
                        new LegalLink("cookies.com", "cookies"),
                        new LegalLink("faqs.com", "faqs"),
                        new LegalLink("about.com", "about"),
                        new LegalLink("operation.com", "operation"),
                        new LegalLink("complaint.com", "complaint")
                ],
                new wabi2b.sdk.regional.LegalDocumentInformation(
                        "CUIT",
                        "999999999999",
                        "^\\\\d{1,12}\$",
                        []
                ),
                new FeeConfiguration(
                        "WABICREDITS_PERCENTAGE",
                        1
                ),
                new wabi2b.sdk.regional.Language(
                        "es",
                        "es-AR",
                        "ltr",
                        [new Translation("es", "Argentina")]
                ),
                new WabipayConfiguration(
                        true,
                        true,
                        true
                ),
                new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                new ShoppingConfiguration(true),
                new BranchOfficeConfiguration(false)
        )

        def country = mapper.buildCountry(regionalConfigCountry)
        def expectedContactInfo = new ContactInfo(
                whatsappNumber: "541161290635", phoneNumber: "+541120400002", zaloNumber: "0862000780")
        def expectedDetail = new Detail(countryCode: "+54", timezone: "America/Argentina/Buenos_Aires")
        def expectedTranslation = new CountryTranslation(language: "es", value: "Argentina")
        def expectedLanguage = new Language(
                language: "es", locale: "es-AR", direction: "ltr", translations: [expectedTranslation])
        def expectedCurrency = new Currency(symbol: "\$", code: "ARS")
        def expectedFee = new Fee(
                serviceFeeType: "WABICREDITS_PERCENTAGE", serviceFee: new BigDecimal(1), displayFeeOnSupplierAdm: false
        )
        def expectedWabipay = new WabiPay(
                enabled: true, creditEnabled: true, moneyEnabled: true, wcToMoneyWhenReleasingEnabled: false
        )
        def expectedLegalDocumentInfo = new LegalDocumentInformation(
                id: "CUIT", mask: "999999999999", maskRegex: "^\\\\d{1,12}\$"
        )
        def expectedLegalUrls = [
                new LegalUrl(type: LegalUrlType.TERMS_AND_CONDITIONS, value: "tyc.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.PRIVACY_POLICY, value: "pp.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.COOKIES, value: "cookies.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.FAQS, value: "faqs.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.ABOUT, value: "about.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.OPERATION, value: "operation.com", label: "labelMsg"),
                new LegalUrl(type: LegalUrlType.COMPLAINT, value: "complaint.com", label: "labelMsg"),
        ]
        assertEquals("ar", country.id)
        assertEquals("Argentina", country.name)
        assertEquals(expectedDetail.countryCode, country.detail.countryCode)
        assertEquals(expectedDetail.timezone, country.detail.timezone)
        assertEquals(expectedLanguage.language, country.language.language)
        assertEquals(expectedLanguage.locale, country.language.locale)
        assertEquals(expectedLanguage.direction, country.language.direction)
        assertEquals(expectedTranslation.language, country.language.translations.first().language)
        assertEquals(expectedTranslation.value, country.language.translations.first().value)
        assertEquals(expectedContactInfo.whatsappNumber, country.contactInfo.whatsappNumber)
        assertEquals(expectedContactInfo.phoneNumber, country.contactInfo.phoneNumber)
        assertEquals(expectedContactInfo.zaloNumber, country.contactInfo.zaloNumber)
        assertEquals(expectedCurrency.symbol, country.currency.symbol)
        assertEquals(expectedCurrency.code, country.currency.code)
        assertEquals(expectedFee.serviceFeeType, country.fee.serviceFeeType)
        assertEquals(expectedFee.serviceFee, country.fee.serviceFee)
        assertEquals(expectedFee.displayFeeOnSupplierAdm, country.fee.displayFeeOnSupplierAdm)
        assertEquals(expectedWabipay.enabled, country.wabiPay.enabled)
        assertEquals(expectedWabipay.creditEnabled, country.wabiPay.creditEnabled)
        assertEquals(expectedWabipay.moneyEnabled, country.wabiPay.moneyEnabled)
        assertEquals(expectedWabipay.wcToMoneyWhenReleasingEnabled, country.wabiPay.wcToMoneyWhenReleasingEnabled)
        assertEquals(expectedLegalDocumentInfo.id, country.legalDocumentInformation.id)
        assertEquals(expectedLegalDocumentInfo.mask, country.legalDocumentInformation.mask)
        assertEquals(expectedLegalDocumentInfo.maskRegex, country.legalDocumentInformation.maskRegex)
        checkLegalUrls(expectedLegalUrls, country.legalUrls)
    }

    private static checkLegalUrls(List<LegalUrl> expectedLegalUrls, List<LegalUrl> resultLegalUrls) {
        expectedLegalUrls.eachWithIndex { expected, idx ->
            def result = resultLegalUrls[idx]
            assertEquals(expected.type, result.type)
            assertEquals(expected.value, result.value)
            assertEquals(expected.label, result.label)
        }
    }
}
