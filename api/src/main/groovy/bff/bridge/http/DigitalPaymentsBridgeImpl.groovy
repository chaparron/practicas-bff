package bff.bridge.http

import bff.bridge.DigitalPaymentsBridge
import bff.configuration.CacheConfigurationProperties
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.Provider
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class DigitalPaymentsBridgeImpl implements  DigitalPaymentsBridge {

    /*@Autowired
    private CacheConfigurationProperties cacheConfiguration*/

    @Autowired
    private DigitalPaymentsSdk digitalPaymentsSdk

    /*private Cache<String, Mono<List<Provider>>> providersCache

    @PostConstruct
    void init() {
        providersCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.providers, TimeUnit.MINUTES)
                .build()
    }*/

    @Override
    List<Provider> getPaymentProviders(String supplierId, String accessToken) {
        digitalPaymentsSdk.getPaymentProviders(supplierId, accessToken).block()
    }
}
