package bff.bridge.http

import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.Country
import bff.model.CountryConfigurationEntry
import bff.model.ContactInfo
import bff.model.Currency
import bff.model.Detail
import bff.model.Fee
import bff.model.Language
import bff.model.WabiPay
import bff.model.CountryTranslation
import bff.model.LegalUrlsCountry
import bff.service.HttpBridge
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class CountryGatewayBridgeImpl implements CountryBridge {

    private static final String SELF_COUNTRY_ENDPOINT = "country/me"

    private static final String PUBLIC_COUNTRY_ENDPOINT = "country/public"

    private static final String PUBLIC_ENABLED_COUNTRY_ENDPOINT = "country/public/enabled"

    @Autowired
    private HttpBridge httpBridge

    @Value('${regional.config.url:}')
    URI regionalConfigUrl

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<CountryConfigurationEntry>> countryEntryCache

    private LoadingCache<String, List<Country>> countriesEnabledCache

    private LoadingCache<String, Country> countryConfigurationCache

    @PostConstruct
    void init() {
        countryEntryCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<CountryConfigurationEntry>>() {
                            @Override
                            List<CountryConfigurationEntry> load(String key) throws Exception {
                                getUnCachedCountryConfiguration(key)
                            }
                        }
                )

        countriesEnabledCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<Country>>() {
                            @Override
                            List<Country> load(String key) throws Exception {
                                getUnCachedHomeCountries(key)
                            }
                        }
                )

        countryConfigurationCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, Country>() {
                            @Override
                            Country load(String key) throws Exception {
                                getUnCachedCountry(key)
                            }
                        }
                )
    }

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        countryEntryCache.get(countryId)
    }

    @Override
    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {

        def requestUri = UriComponentsBuilder.fromUri(regionalConfigUrl.resolve(SELF_COUNTRY_ENDPOINT)).toUriString().toURI()

        httpBridge.get(
                requestUri,
                "Bearer $accessToken"
        )
                ?.config
                ?.collect {
                    new CountryConfigurationEntry(key: it.key, value: it.value)
                }
    }

    @Override
    List<Country> getHomeCountries(String locale) {
        countriesEnabledCache.get(Locale.forLanguageTag(locale).language)
    }

    @Override
    Country getCountry(String countryId) {
        countryConfigurationCache.get(countryId)
    }

    private def getUnCachedCountryConfiguration(String countryId) {

        def requestUri = UriComponentsBuilder.fromUri(regionalConfigUrl.resolve(PUBLIC_COUNTRY_ENDPOINT))
                .path("/{countryId}")
                .buildAndExpand(countryId)
                .toUriString()
                .toURI()

        httpBridge.get(
                requestUri,
                null
        )
                ?.config
                ?.collect {
                    new CountryConfigurationEntry(key: it.key, value: it.value)
                }
    }

    def getUnCachedCountry(String countryId) {

        def requestUri = UriComponentsBuilder.fromUri(regionalConfigUrl.resolve(PUBLIC_COUNTRY_ENDPOINT))
                .path("/{countryId}")
                .buildAndExpand(countryId)
                .toUriString()
                .toURI()

        def params = httpBridge.get(
                requestUri,
                null
        )
                ?.config

        def translations = []
        params.each({
            if (it["key"].matches("^name-[a-zA-Z]{2}"))
                translations.add(new CountryTranslation(
                        name: it["key"],
                        language: Locale.forLanguageTag(it["key"].toString().split("-")[1]).language,
                        value: it["value"])
                )
        })

        def legalUrls = new LegalUrlsCountry(
                tyc: params.find({ it["key"] == "tyc" })?.value,
                pp: params.find({ it["key"] == "pp" })?.value,
                cookies: params.find({ it["key"] == "cookies" })?.value,
                faqs: params.find({ it["key"] == "faqs" })?.value
        )

        def detail = new Detail(
                phonePrefix: params.find({ it["key"] == "country_code" })?.value,
                countryCode: Locale.forLanguageTag(countryId).language
        )

        def language = new Language(
                language: params.find({ it["key"] == "language" })?.value,
                locale: params.find({ it["key"] == "locale" })?.value,
                translations: translations
        )

        def contactInfo = new ContactInfo(
                whatsappNumber: params.find({ it["key"] == "whatsapp_number" })?.value,
                phoneNumber: params.find({ it["key"] == "phone_number" })?.value,
                direction: params.find({ it["key"] == "direction" })?.value
        )

        def currency = new Currency(
                currencyCode: params.find({ it["key"] == "currency_code" })?.value,
                currencySymbol: params.find({ it["key"] == "currency" })?.value
        )
        def fee = new Fee(
                displayFeeOnSupplierAdm: params.find({ it["key"] == "display_fee_on_supplier_adm" })?.value,
                serviceFeeType: params.find({ it["key"] == "service_fee_type" })?.value,
                serviceFee: new BigDecimal(params.find({ it["key"] == "service_fee" })?.value)
        )

        def wabiPay = new WabiPay(
                wabiPayEnabled: params.find({ it["key"] == "wabipay_enabled" })?.value,
                wabiPayCreditEnabled: params.find({ it["key"] == "wabipay_wabicredits_enabled" })?.value,
                wabiPayMoneyEnabled: params.find({ it["key"] == "wabipay_money_enabled" })?.value,
                wabiPayWcToMoneyWhenReleasingEnabled: params.find({ it["key"] == "wabipay_convert_wc_to_money_when_releasing" })?.value,
        )

        return new Country(
                id: countryId,
                name: params.find({ it["key"] == "name" })?.value,
                flag: params.find({ it["key"] == "flag" })?.value,
                legalUrls: legalUrls,
                detail: detail,
                language: language,
                contactInfo: contactInfo,
                currency: currency,
                fee: fee,
                wabiPay: wabiPay
        )
    }

    private def getUnCachedHomeCountries(String locale) {

        def requestUri = UriComponentsBuilder.fromUri(regionalConfigUrl.resolve(PUBLIC_ENABLED_COUNTRY_ENDPOINT))
                .queryParam("keys", "name-en,name-$locale,flag,tyc,pp,cookies,faqs")
                .toUriString()
                .toURI()

        httpBridge.get(
                requestUri,
                null,
                null,
                List
        )
                ?.collect {
                    new Country(
                            id: it.id,
                            name: it["config"]?.find({ config -> config["key"].contains("name-$locale") })?.value ?: it["config"]?.find({ config -> config["key"].contains("name-en") })?.value,
                            flag: it["config"]?.find({ config -> config["key"].contains("flag") })?.value,
                            legalUrls: new LegalUrlsCountry(
                                    tyc: it["config"]?.find({ config -> config["key"].contains("tyc") })?.value,
                                    pp: it["config"]?.find({ config -> config["key"].contains("pp") })?.value,
                                    cookies: it["config"]?.find({ config -> config["key"].contains("cookies") })?.value,
                                    faqs: it["config"]?.find({ config -> config["key"].contains("faqs") })?.value,
                            )
                    )
                }
                ?.sort({ it.name })

    }
}
