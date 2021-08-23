package bff.mapper.impl

import bff.mapper.CountryMapper
import bff.model.ContactInfo
import bff.model.Country
import bff.model.CountryTranslation
import bff.model.Currency
import bff.model.Detail
import bff.model.Fee
import bff.model.Language
import bff.model.LegalUrl
import bff.model.WabiPay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class CountryMapperImpl implements CountryMapper {

    @Autowired
    private final MessageSource messageSource

    @Override
    def buildCountryFromParams(
            String countryId,
            ArrayList params
    ) {
        return new Country(
                id: countryId,
                name: params.find({ it["key"] == "name" })?.value,
                flag: params.find({ it["key"] == "flag" })?.value,
                legalUrls: buildLegalUrls(params),
                detail: buildDetail(params),
                language: buildLanguage(params),
                contactInfo: buildContactInfo(params),
                currency: buildCurrency(params),
                fee: buildFee(params),
                wabiPay: buildWabiPay(params)
        )
    }

    @Override
    def buildCountryListFromMapParams(LinkedHashMap map) {
        def targetLocale = Locale.forLanguageTag(map["config"]?.find({ it["key"] == "locale" })?.value)
        return new Country(
                id: map.id,
                name: map["config"]?.find({ config -> config["key"].contains("name-$targetLocale") })?.value ?: map["config"]?.find({ config -> config["key"].contains("name-en") })?.value,
                flag: map["config"]?.find({ config -> config["key"].contains("flag") })?.value,
                legalUrls: buildLegalUrls(map.config),
                detail: buildDetail(map.config),
                language: buildLanguage(map.config),
                contactInfo: buildContactInfo(map.config),
                currency: buildCurrency(map.config),
                fee: buildFee(map.config),
                wabiPay: buildWabiPay(map.config)
        )
    }


    def private buildWabiPay(Object params) {
        return new WabiPay(
                enabled: params.find({ it["key"] == "wabipay_enabled" })?.value,
                creditEnabled: params.find({ it["key"] == "wabipay_wabicredits_enabled" })?.value,
                moneyEnabled: params.find({ it["key"] == "wabipay_money_enabled" })?.value,
                wcToMoneyWhenReleasingEnabled: params.find({ it["key"] == "wabipay_convert_wc_to_money_when_releasing" })?.value,
        )
    }

    def private buildFee(Object params) {
        return new Fee(
                displayFeeOnSupplierAdm: params.find({ it["key"] == "display_fee_on_supplier_adm" })?.value,
                serviceFeeType: params.find({ it["key"] == "service_fee_type" })?.value,
                serviceFee: new BigDecimal(params.find({ it["key"] == "service_fee" })?.value ?: 0)
        )
    }

    def private buildCurrency(Object params) {
        return new Currency(
                code: params.find({ it["key"] == "currency_code" })?.value,
                symbol: params.find({ it["key"] == "currency" })?.value
        )
    }

    def private buildContactInfo(Object params) {
        return new ContactInfo(
                whatsappNumber: params.find({ it["key"] == "whatsapp_number" })?.value,
                phoneNumber: params.find({ it["key"] == "phone_number" })?.value,
        )
    }

    def private buildLanguage(Object params) {
        def translations = []
        params.each({
            if (it["key"].matches("^name-[a-zA-Z]{2}"))
                translations.add(new CountryTranslation(
                        language: Locale.forLanguageTag(it["key"].toString().split("-")[1]).language,
                        value: it["value"])
                )
        })
        return new Language(
                language: params.find({ it["key"] == "language" })?.value,
                locale: params.find({ it["key"] == "locale" })?.value,
                direction: params.find({ it["key"] == "direction" })?.value,
                translations: translations
        )
    }

    def private buildDetail(Object params) {
        return new Detail(
                countryCode: params.find({ it["key"] == "country_code" })?.value
        )
    }

    def private buildLegalUrls(Object params) {
        def targetLocale = Locale.forLanguageTag(params.find({ it["key"] == "locale" })?.value)

        return [
                new LegalUrl(
                        type: "tyc",
                        label: messageSource.getMessage("terms", null, targetLocale),
                        value: params.find({ it["key"] == "tyc" })?.value),
                new LegalUrl(
                        type: "pp",
                        label: messageSource.getMessage("pp", null, targetLocale),
                        value: params.find({ it["key"] == "pp" })?.value),
                new LegalUrl(
                        type: "cookies",
                        label: messageSource.getMessage("cookie_privacy", null, targetLocale),
                        value: params.find({ it["key"] == "cookies" })?.value),
                new LegalUrl(
                        type: "faqs",
                        label: messageSource.getMessage("terms", null, targetLocale),
                        value: params.find({ it["key"] == "faqs" })?.value)
        ]
    }
}
