package bff.bridge.http

import bff.bridge.BnplBridge
import bff.configuration.CacheConfigurationProperties
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.SupportedMinimumAmountResponse
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class BnplBridgeImpl implements BnplBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private BnPlSdk bnPlSdk

    private Cache<String, SupportedMinimumAmountResponse> supportedMinimumCache

    @PostConstruct
    void init() {
        supportedMinimumCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.bnpl, TimeUnit.MINUTES)
                .build()
    }

    @Override
    SupportedMinimumAmountResponse supportedMinimumAmount(String country, String accessToken) {
        supportedMinimumCache.get(country) {
            getUnCachedSupportedMinimum(it, accessToken)
        }
    }

    private SupportedMinimumAmountResponse getUnCachedSupportedMinimum(String country, String accessToken) {
        bnPlSdk.supportedMinimumAmount(country, accessToken).block()
    }
}

