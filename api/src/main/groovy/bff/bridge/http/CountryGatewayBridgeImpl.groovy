package bff.bridge.http

import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.Country
import bff.model.CountryConfigurationEntry
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

    private LoadingCache<String, List<CountryConfigurationEntry>> countryCache

    private LoadingCache<String, List<Country>> countriesEnabledCache

    @PostConstruct
    void init() {
        countryCache = Caffeine.newBuilder()
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
    }

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        countryCache.get(countryId)
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
                ?.sort(
                        false,
                        { it.name }
                )

    }
}
