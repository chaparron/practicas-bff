package bff.mapper

import bff.model.ContactInfo
import bff.model.Country
import bff.model.CountryTranslation
import bff.model.Currency
import bff.model.Detail
import bff.model.Fee
import bff.model.Language
import bff.model.LegalDocumentInformation
import bff.model.WabiPay
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThrows

class CountryMapperTest {

    private CountryMapper mapper = new CountryMapper()

    @Test
    void 'get mapped country'() {
        def id = "ar"
        def params = [
                [ "key" : "name", "value"  : "Argentina", "private": false ],
                [ "key" : "timezone", "value"  : "America/Argentina/Buenos_Aires", "private": false],
                [ "key" : "legalId", "value"  : "CUIT", "private": false ],
                [ "key" : "legalMask", "value"  : "999999999999", "private": false ],
                [ "key" : "legalMaskRegex", "value"  : "^\\\\d{1,12}\$", "private": false ],
                [ "key" : "name-es", "value"  : "Argentina", "private": false ],
                [ "key" : "country_code", "value"  : "+54", "private": false ],
                [ "key" : "currency", "value"  : "\$", "private": false ],
                [ "key" : "currency_code", "value"  : "ARS", "private": false ],
                [ "key" : "lat", "value"  : "-34.6052956", "private": false ],
                [ "key" : "lng", "value"  : "-58.3870496", "private": false ],
                [ "key" : "language", "value"  : "es", "private": false ],
                [ "key" : "locale", "value"  : "es-AR", "private": false ],
                [ "key" : "wabipay_enabled", "private": false ],
                [ "key" : "wabipay_wabicredits_enabled", "value"  : "true", "private": false],
                [ "key" : "wabipay_money_enabled", "value"  : "true", "private": false ],
                [ "key" : "wabipay_convert_wc_to_money_when_releasing", "value"  : "false", "private": false],
                [ "key" : "whatsapp_number", "value"  : "541161290635", "private": false ],
                [ "key" : "zalo_number", "value"  : "0862000780", "private": false ],
                [ "key" : "phone_number", "value"  : "+541120400002", "private": false ],
                [ "key" : "direction", "value"  : "ltr", "private": false ],
                [ "key" : "service_fee", "value"  : "1", "private": false ],
                [ "key" : "service_fee_type", "value"  : "WABICREDITS_PERCENTAGE", "private": false ],
                [ "key" : "display_fee_on_supplier_adm", "value"  : "false", "private": false ],
                [ "key" : "flag", "value"  : "7ab0fd14-efa9-11eb-9a03-0242ac1300ar.png", "private": false ]
        ]

        def country = mapper.buildCountryFromParams(id, params)
        def expectedContactInfo = new ContactInfo(
                whatsappNumber: "541161290635", phoneNumber: "+541120400002", zaloNumber: "0862000780")
        def expectedDetail = new Detail(countryCode: "+54", timezone: "America/Argentina/Buenos_Aires")
        def expectedTranslation = new CountryTranslation(language: "es", value: "Argentina")
        def expectedLanguage = new Language(
                language: "es", locale: "es-AR", direction: "ltr", translations: [expectedTranslation])
        def expectedCurrency = new Currency(symbol: "\$", code: "ARS")
        def expectedFee = new Fee(
                serviceFeeType: "WABICREDITS_PERCENTAGE", serviceFee: new BigDecimal(1), displayFeeOnSupplierAdm: true
        )
        def expectedWabipay = new WabiPay(
                enabled: true, creditEnabled: true, moneyEnabled: true, wcToMoneyWhenReleasingEnabled: true
        )
        def expectedLegalDocumentInfo = new LegalDocumentInformation(
                id: "CUIT", mask: "999999999999", maskRegex: "^\\\\d{1,12}\$"
        )
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
    }
}
