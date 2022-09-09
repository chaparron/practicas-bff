package bff.bridge.http

import bff.JwtToken
import bff.bridge.DigitalPaymentsBridge
import bff.configuration.CacheConfigurationProperties
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.Provider
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class DigitalPaymentsBridgeImpl implements  DigitalPaymentsBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private DigitalPaymentsSdk digitalPaymentsSdk

    private Cache<PaymentProviderListKey, List<Provider>> providersCache

    @PostConstruct
    void init() {
        providersCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.providers, TimeUnit.MINUTES)
                .build()
    }

    @Override
    List<Provider> getPaymentProviders(String supplierId, String accessToken) {
        def userIdFromToken = JwtToken.userIdFromToken(accessToken)
        providersCache.get(new PaymentProviderListKey(supplierId: supplierId, customerUserId: userIdFromToken)) {
            digitalPaymentsSdk.getPaymentProviders(it.supplierId, accessToken).block()
        }
    }
}

@Immutable
@EqualsAndHashCode
class PaymentProviderListKey{
    String supplierId
    String customerUserId
}
