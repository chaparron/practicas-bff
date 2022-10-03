package bff.mapper

import bff.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import wabi2b.sdk.regional.ContactInformation
import wabi2b.sdk.regional.CurrencyInformation
import wabi2b.sdk.regional.FeeConfiguration
import wabi2b.sdk.regional.LegalLink
import wabi2b.sdk.regional.WabipayConfiguration

@Component
class CountryMapper {
    public static final String PARAM_TERMS = "terms"
    public static final String PARAM_TYC = "tyc"
    public static final String PARAM_PP = "pp"
    public static final String PARAM_COOKIE_PRIVACY = "cookie_privacy"
    public static final String PARAM_COOKIES = "cookies"
    public static final String PARAM_FAQ = "faq"
    public static final String PARAM_FAQS = "faqs"
    public static final String PARAM_ABOUT = "about"
    public static final String PARAM_OPERATION = "operation"
    public static final String PARAM_COMPLAINT = "complaint"

    @Autowired
    MessageSource messageSource

    Country buildCountry(
            wabi2b.sdk.regional.Country country
    ) {
        return new Country(
                id: country.code,
                name: country.name,
                flag: country.flag,
                legalUrls: buildLegalUrls(country.language.locale, country.links),
                detail: buildDetail(country),
                language: buildLanguage(country.language),
                contactInfo: buildContactInfo(country.contactInformation),
                currency: buildCurrency(country.currencyInformation),
                fee: buildFee(country.feeConfiguration),
                wabiPay: buildWabiPay(country.wabipayConfiguration),
                legalDocumentInformation: buildLegalDocumentInformation(country.legalDocumentInformation),
                geolocation: buildGeolocation(country.geolocation)
        )
    }

    Country buildCountryWithLocale(
            wabi2b.sdk.regional.Country country,
            String locale
    ) {
        return new Country(
                id: country.code,
                name: country.language.nameTranslations.find({ it.language == locale })?.value
                        ?: country.language.nameTranslations.find({ it.language == "en" })?.value,
                flag: country.flag,
                legalUrls: buildLegalUrls(locale, country.links),
                detail: buildDetail(country),
                language: buildLanguage(country.language),
                contactInfo: buildContactInfo(country.contactInformation),
                currency: buildCurrency(country.currencyInformation),
                fee: buildFee(country.feeConfiguration),
                wabiPay: buildWabiPay(country.wabipayConfiguration),
                legalDocumentInformation: buildLegalDocumentInformation(country.legalDocumentInformation),
                geolocation: buildGeolocation(country.geolocation),
                customerBranchesEnabled: country.branchOfficeConfiguration
        )
    }

    private static WabiPay buildWabiPay(WabipayConfiguration wabipayConfiguration) {
        return new WabiPay(
                enabled: wabipayConfiguration.enabled,
                creditEnabled: wabipayConfiguration.creditEnabled,
                moneyEnabled: wabipayConfiguration.moneyEnabled,
                wcToMoneyWhenReleasingEnabled: false
        )
    }

    private static Fee buildFee(FeeConfiguration feeConfiguration) {
        return new Fee(
                serviceFeeType: feeConfiguration.type,
                serviceFee: new BigDecimal(feeConfiguration.amount),
                displayFeeOnSupplierAdm: false
        )
    }

    private static Currency buildCurrency(CurrencyInformation currencyInformation) {
        return new Currency(code: currencyInformation.code, symbol: currencyInformation.symbol)
    }

    private static ContactInfo buildContactInfo(ContactInformation contactInformation) {
        return new ContactInfo(
                whatsappNumber: contactInformation.whatsapp,
                phoneNumber: contactInformation.phone,
                zaloNumber: contactInformation.zalo
        )
    }

    private static Language buildLanguage(wabi2b.sdk.regional.Language language) {
        def translations = []
        language.nameTranslations.each {
            translations.add(new CountryTranslation(
                    language: Locale.forLanguageTag(it.language).language,
                    value: it.value)
            )
        }
        return new Language(
                language: language.id,
                locale: language.locale,
                direction: language.direction,
                translations: translations
        )
    }

    private static Geolocation buildGeolocation(wabi2b.sdk.regional.Geolocation geoLocation) {
        return new Geolocation(lat: geoLocation.lat, lng: geoLocation.lng)
    }

    private static Detail buildDetail(wabi2b.sdk.regional.Country country) {
        return new Detail(countryCode: country.countryCode, timezone: country.timezone)
    }

    private static LegalDocumentInformation buildLegalDocumentInformation(
            wabi2b.sdk.regional.LegalDocumentInformation legalDocumentInformation
    ) {
        return new LegalDocumentInformation(
                id: legalDocumentInformation.id,
                mask: legalDocumentInformation.mask,
                maskRegex: legalDocumentInformation.maskRegex,
                legalDocumentFiles: buildLegalDocumentFiles(legalDocumentInformation.files)
        )
    }

    private static List<LegalDocumentFile> buildLegalDocumentFiles(List<wabi2b.sdk.regional.LegalDocumentFile> files) {
        files.collect {
            new LegalDocumentFile(id: it.id, label: it.name)
        }
    }

    def private buildLegalUrls(String languageTag, List<LegalLink> legalLinks) {
        def targetLocale = Locale.forLanguageTag(languageTag ?: "en")

        def legalUrls = []
        legalLinks.find({ it.type == PARAM_TYC })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.TERMS_AND_CONDITIONS, PARAM_TERMS, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_PP })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.PRIVACY_POLICY, PARAM_PP, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_COOKIES })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.COOKIES, PARAM_COOKIE_PRIVACY, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_FAQS })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.FAQS, PARAM_FAQ, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_ABOUT })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.ABOUT, PARAM_ABOUT, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_OPERATION })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.OPERATION, PARAM_OPERATION, targetLocale))
        }
        legalLinks.find({ it.type == PARAM_COMPLAINT })?.with {
            legalUrls.add(getLegalUrl(it.value, LegalUrlType.COMPLAINT, PARAM_COMPLAINT, targetLocale))
        }
        return legalUrls
    }

    private getLegalUrl(String url, LegalUrlType type, String label, Locale locale) {
        new LegalUrl(type: type, label: messageSource.getMessage(label, null, locale), value: url)
    }
}
