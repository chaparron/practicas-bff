package bff.bridge.http

import bff.bridge.StateBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.State
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

class StateGatewayBridgeImpl implements StateBridge {

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

    private LoadingCache<String, List<State>> stateCache

    @PostConstruct
    void init() {
        countryUrl = serviceDiscovery.discover(countryServiceName, countryUrl)
        stateCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.states, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<State>>() {
                            @Override
                            List<State> load(String key) throws Exception {
                                getUnCachedState(key)
                            }
                        }
                )
    }

    @Override
    List<State> getByCountryId(String countryId) {
        stateCache.get(countryId)
    }

    private def getUnCachedState(String countryId) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(countryUrl.resolve("state/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new State(id: it.iso_code, name: it.name)
        }
    }
}
