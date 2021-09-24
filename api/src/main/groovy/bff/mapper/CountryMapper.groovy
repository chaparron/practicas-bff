package bff.mapper


import bff.model.ContactInfo
import bff.model.Country
import bff.model.CountryTranslation
import bff.model.Currency
import bff.model.Detail
import bff.model.Fee
import bff.model.Language
import bff.model.LegalDocumentInformation
import bff.model.LegalUrl
import bff.model.LegalUrlType
import bff.model.WabiPay
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class CountryMapper {

    public static final String TRANSLATION_LOCALE_REGEXP = "^name-[a-zA-Z]{2}"

    public static final String PARAM_KEY = "key"
    public static final String PARAM_NAME = "name"
    public static final String PARAM_FLAG = "flag"
    public static final String PARAM_WABIPAY_ENABLED = "wabipay_enabled"
    public static final String PARAM_WABIPAY_WABICREDITS_ENABLED = "wabipay_wabicredits_enabled"
    public static final String PARAM_WABIPAY_MONEY_ENABLED = "wabipay_money_enabled"
    public static final String PARAM_WABIPAY_CONVERT_WC_TO_MONEY_WHEN_RELEASING = "wabipay_convert_wc_to_money_when_releasing"
    public static final String PARAM_DISPLAY_FEE_ON_SUPPLIER_ADM = "display_fee_on_supplier_adm"
    public static final String PARAM_SERVICE_FEE_TYPE = "service_fee_type"
    public static final String PARAM_SERVICE_FEE = "service_fee"
    public static final String PARAM_CURRENCY_CODE = "currency_code"
    public static final String PARAM_CURRENCY = "currency"
    public static final String PARAM_WHATSAPP_NUMBER = "whatsapp_number"
    public static final String PARAM_PHONE_NUMBER = "phone_number"
    public static final String PARAM_VALUE = "value"
    public static final String PARAM_LANGUAGE = "language"
    public static final String PARAM_LOCALE = "locale"
    public static final String PARAM_DIRECTION = "direction"
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
    public static final String PARAM_COUNTRY_CODE = "country_code"
    public static final String PARAM_LEGAL_ID = "legalId"
    public static final String PARAM_LEGAL_MASK = "legalMask"
    public static final String PARAM_TIMEZONE = "timezone"

    @Autowired
    MessageSource messageSource

    /**
     * Build Country Object from original API params
     * @param params The original API parameters
     * @return Country
     */
    Country buildCountryFromParams(
            String countryId,
            ArrayList params
    ) {
        return new Country(
                id: countryId,
                name: params.find({ it[PARAM_KEY] == PARAM_NAME })?.value,
                flag: params.find({ it[PARAM_KEY] == PARAM_FLAG })?.value,
                legalUrls: buildLegalUrls(params),
                detail: buildDetail(params),
                language: buildLanguage(params),
                contactInfo: buildContactInfo(params),
                currency: buildCurrency(params),
                fee: buildFee(params),
                wabiPay: buildWabiPay(params),
                legalDocumentInformation: buildLegalDocumentInformation(params)
        )
    }

    /**
     * Build Country Object from original API params with translated name
     * @param params The original API parameters
     * @return Country
     */
    Country buildCountryFromParamsWithLocale(
            String countryId,
            ArrayList params,
            String locale
    ) {
        return new Country(
                id: countryId,
                name: params.find({ it[PARAM_KEY] == "$PARAM_NAME-$locale" })?.value
                        ?: params.find({ it[PARAM_KEY] == "$PARAM_NAME-en" })?.value,
                flag: params.find({ it[PARAM_KEY] == PARAM_FLAG })?.value,
                legalUrls: buildLegalUrls(params),
                detail: buildDetail(params),
                language: buildLanguage(params),
                contactInfo: buildContactInfo(params),
                currency: buildCurrency(params),
                fee: buildFee(params),
                wabiPay: buildWabiPay(params),
                legalDocumentInformation: buildLegalDocumentInformation(params)
        )
    }

    private WabiPay buildWabiPay(Object params) {
        return new WabiPay(
                enabled: params.find({ it[PARAM_KEY] == PARAM_WABIPAY_ENABLED })?.value,
                creditEnabled: params.find({ it[PARAM_KEY] == PARAM_WABIPAY_WABICREDITS_ENABLED })?.value,
                moneyEnabled: params.find({ it[PARAM_KEY] == PARAM_WABIPAY_MONEY_ENABLED })?.value,
                wcToMoneyWhenReleasingEnabled: params.find({ it[PARAM_KEY] == PARAM_WABIPAY_CONVERT_WC_TO_MONEY_WHEN_RELEASING })?.value,
        )
    }

    private Fee buildFee(Object params) {
        return new Fee(
                displayFeeOnSupplierAdm: params.find({ it[PARAM_KEY] == PARAM_DISPLAY_FEE_ON_SUPPLIER_ADM })?.value,
                serviceFeeType: params.find({ it[PARAM_KEY] == PARAM_SERVICE_FEE_TYPE })?.value,
                serviceFee: new BigDecimal(params.find({ it[PARAM_KEY] == PARAM_SERVICE_FEE })?.value ?: 0)
        )
    }

    private Currency buildCurrency(Object params) {
        return new Currency(
                code: params.find({ it[PARAM_KEY] == PARAM_CURRENCY_CODE })?.value,
                symbol: params.find({ it[PARAM_KEY] == PARAM_CURRENCY })?.value
        )
    }

    private ContactInfo buildContactInfo(Object params) {
        return new ContactInfo(
                whatsappNumber: params.find({ it[PARAM_KEY] == PARAM_WHATSAPP_NUMBER })?.value,
                phoneNumber: params.find({ it[PARAM_KEY] == PARAM_PHONE_NUMBER })?.value,
        )
    }

    private Language buildLanguage(Object params) {
        def translations = []
        params.each({
            if (it[PARAM_KEY].matches(TRANSLATION_LOCALE_REGEXP))
                translations.add(new CountryTranslation(
                        language: Locale.forLanguageTag(it[PARAM_KEY].toString().split("-")[1]).language,
                        value: it[PARAM_VALUE])
                )
        })
        return new Language(
                language: params.find({ it[PARAM_KEY] == PARAM_LANGUAGE })?.value,
                locale: params.find({ it[PARAM_KEY] == PARAM_LOCALE })?.value,
                direction: params.find({ it[PARAM_KEY] == PARAM_DIRECTION })?.value ?: "",
                translations: translations
        )
    }

    private Detail buildDetail(Object params) {
        return new Detail(
                countryCode: params.find({ it[PARAM_KEY] == PARAM_COUNTRY_CODE })?.value,
                timezone: params.find({ it[PARAM_KEY] == PARAM_TIMEZONE })?.value
        )
    }

    private LegalDocumentInformation buildLegalDocumentInformation(Object params) {
        return new LegalDocumentInformation(
                id: params.find({ it[PARAM_KEY] == PARAM_LEGAL_ID })?.value ?: "",
                mask: params.find({ it[PARAM_KEY] == PARAM_LEGAL_MASK })?.value ?: "",
        )
    }

    def private buildLegalUrls(ArrayList params) {
        def targetLocale = Locale.forLanguageTag(params.find({ it[PARAM_KEY] == PARAM_LOCALE })?.value ?: "en")

        def legalUrls = []
        params.find({ it[PARAM_KEY] == PARAM_TYC })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.TERMS_AND_CONDITIONS, PARAM_TERMS, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_PP })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.PRIVACY_POLICY, PARAM_PP, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_COOKIES })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.COOKIES, PARAM_COOKIE_PRIVACY, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_FAQS })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.FAQS, PARAM_FAQ, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_ABOUT })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.ABOUT, PARAM_ABOUT, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_OPERATION })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.OPERATION, PARAM_OPERATION, targetLocale))
        }
        params.find({ it[PARAM_KEY] == PARAM_COMPLAINT })?.with{
            legalUrls.add(getLegalUrl(it, LegalUrlType.COMPLAINT, PARAM_COMPLAINT, targetLocale))
        }
        return legalUrls
    }

    private getLegalUrl(Object url, LegalUrlType type, String label, Locale locale) {
        new LegalUrl(
                type: type,
                label: messageSource.getMessage(label, null, locale),
                value: url.value)
    }
}
