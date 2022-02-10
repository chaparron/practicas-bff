package bff.bridge.http

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.mapper.CountryMapper
import bff.model.Country
import bff.model.CountryConfigurationEntry
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.beans.factory.annotation.Autowired
import wabi2b.sdk.regional.RegionalConfigSdk

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class CountryGatewayBridgeImpl implements CountryBridge {

    @Autowired
    CountryMapper countryMapper

    @Autowired
    private RegionalConfigSdk regionalConfigSdk

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
        String countryId = JwtToken.countryFromString(accessToken)
        getCountryConfiguration(countryId)
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
        regionalConfigSdk.findCountryConfig(countryId).block()
                ?.config
                ?.collect {
                    new CountryConfigurationEntry(key: it.key, value: it.value)
                }
    }

    def getUnCachedCountry(String countryId) {
        return countryMapper.buildCountry(regionalConfigSdk.findCountry(countryId).block())
    }

    private def getUnCachedHomeCountries(String locale) {
        regionalConfigSdk.findCountries(true).block()?.collect {
            return countryMapper.buildCountryWithLocale(it, locale)
        }?.sort(false, { it.name })
    }
}
