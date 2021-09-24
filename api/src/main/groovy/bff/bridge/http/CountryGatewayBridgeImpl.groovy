package bff.bridge.http

import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.mapper.CountryMapper
import bff.model.Country
import bff.model.CountryConfigurationEntry
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

    public static final String ENDPOINT_COUNTRY_ID = "/{countryId}"

    @Autowired
    CountryMapper countryMapper

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
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, List<CountryConfigurationEntry>>() {
                            @Override
                            List<CountryConfigurationEntry> load(String key) throws Exception {
                                getUnCachedCountryConfiguration(key)
                            }
                        }
                )

        countriesEnabledCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, List<Country>>() {
                            @Override
                            List<Country> load(String key) throws Exception {
                                getUnCachedHomeCountries(key)
                            }
                        }
                )

        countryConfigurationCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.MINUTES)
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
                .path(ENDPOINT_COUNTRY_ID)
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
                .path(ENDPOINT_COUNTRY_ID)
                .buildAndExpand(countryId)
                .toUriString()
                .toURI()

        def response = httpBridge.get(
                requestUri,
                null
        )

        return countryMapper.buildCountryFromParams(
                response.id as String,
                response.config as ArrayList
        )
    }

    private def getUnCachedHomeCountries(String locale) {

        def requestUri = UriComponentsBuilder.fromUri(regionalConfigUrl.resolve(PUBLIC_ENABLED_COUNTRY_ENDPOINT))
                .toUriString()
                .toURI()

        httpBridge.get(
                requestUri,
                null,
                null,
                List
        )
                ?.collect {
                    return countryMapper.buildCountryFromParamsWithLocale(
                            it.id as String,
                            it.config as ArrayList,
                            locale
                    )
                }
                ?.sort(
                        false,
                        { it.name }
                )

    }
}
