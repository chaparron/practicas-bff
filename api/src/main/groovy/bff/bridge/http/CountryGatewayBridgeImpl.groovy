package bff.bridge.http

import bff.bridge.CountryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.CountryConfigurationEntry
import bff.service.HttpBridge
import bff.service.ServiceDiscovery
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class CountryGatewayBridgeImpl implements CountryBridge {

    @Value('${country.service:RegionalConfigService}')
    String countryServiceName

    @Autowired
    HttpBridge httpBridge

    @Autowired
    ServiceDiscovery serviceDiscovery

    @Value('${country.url:}')
    URI countryUrl

    @Autowired
    CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<CountryConfigurationEntry>> countryCache

    @PostConstruct
    void init() {
        countryUrl = serviceDiscovery.discover(countryServiceName, countryUrl)
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
    }

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        countryCache.get(countryId)
    }

    @Override
    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(countryUrl.resolve("country/me")).toUriString().toURI(),
                "Bearer $accessToken")?.config?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }

    private def getUnCachedCountryConfiguration(String countryId) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(countryUrl.resolve("country/public/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }
}
