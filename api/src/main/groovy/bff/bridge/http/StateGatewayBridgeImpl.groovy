package bff.bridge.http

import bff.bridge.StateBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.State
import bff.service.HttpBridge
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class StateGatewayBridgeImpl implements StateBridge {
    @Autowired
    private HttpBridge httpBridge

    @Value('${regional.config.url:}')
    URI regionalConfigUrl

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<State>> stateCache

    @PostConstruct
    void init() {
        stateCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.states, TimeUnit.MINUTES)
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
                UriComponentsBuilder.fromUri(regionalConfigUrl.resolve("state/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new State(id: it.iso_code, name: it.name)
        }
    }
}
