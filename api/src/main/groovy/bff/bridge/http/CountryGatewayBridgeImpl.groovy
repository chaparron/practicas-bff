package bff.bridge.http

import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.CountryConfigurationEntry
import bff.model.CountryHomeResponse
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
    @Autowired
    private HttpBridge httpBridge

    @Value('${regional.config.url:}')
    URI regionalConfigUrl

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<CountryConfigurationEntry>> countryCache

    private LoadingCache<String, List<CountryHomeResponse>> countriesEnabled

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

        countriesEnabled = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.countries, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<CountryHomeResponse>>() {
                            @Override
                            List<CountryHomeResponse> load(String key) throws Exception {
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
        httpBridge.get(
                UriComponentsBuilder.fromUri(regionalConfigUrl.resolve("country/me")).toUriString().toURI(),
                "Bearer $accessToken")?.config?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }

    private def getUnCachedCountryConfiguration(String countryId) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(regionalConfigUrl.resolve("country/public/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }

    @Override
    List<CountryHomeResponse> getHomeCountries(String locale) {
        countriesEnabled.get(Locale.forLanguageTag(locale).language)
    }

    private def getUnCachedHomeCountries(String locale) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(regionalConfigUrl.resolve("country/public/enabled?keys=name-en,name-$locale,flag,tyc,pp,cookies,faqs,tyc-supplier,pp-supplier")).toUriString().toURI(),
                null,
                null,
                List)?.collect {
            new CountryHomeResponse(
                    id: it.id,
                    name: it["config"]?.find({ config -> config["key"].contains("name-$locale")})?.value ?: it["config"]?.find({ config -> config["key"].contains("name-en")})?.value,
                    flag: it["config"]?.find({ config -> config["key"].contains("flag")})?.value,
                    legalUrls: new LegalUrlsCountry(
                            tyc: it["config"]?.find({ config -> config["key"].contains("tyc")})?.value,
                            pp: it["config"]?.find({ config -> config["key"].contains("pp")})?.value,
                            cookies: it["config"]?.find({ config -> config["key"].contains("cookies")})?.value,
                            faqs: it["config"]?.find({ config -> config["key"].contains("faqs")})?.value,
                            tycSupplier: it["config"]?.find({ config -> config["key"].contains("tyc-supplier")})?.value,
                            ppSupplier: it["config"]?.find({ config -> config["key"].contains("pp-supplier")})?.value,
                    )
            )
        }
    }
}
