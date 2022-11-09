package bff.bridge.http

import bff.JwtToken
import bff.bridge.DigitalPaymentsBridge
import bff.configuration.CacheConfigurationProperties
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.PaymentOption
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

@Slf4j
class DigitalPaymentsBridgeImpl implements  DigitalPaymentsBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private DigitalPaymentsSdk digitalPaymentsSdk

    private Cache<PaymentOptionListKey, List<PaymentOption>> paymentOptionCache

    @PostConstruct
    void init() {
        paymentOptionCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.providers, TimeUnit.MINUTES)
                .build()
    }

    @Override
    List<PaymentOption> getPaymentMethods(String supplierId, String accessToken) {
        def userIdFromToken = JwtToken.userIdFromToken(accessToken)
        log.trace("getting payment options for supplierId=$supplierId, userIdFromToken=$userIdFromToken")
        paymentOptionCache.get(new PaymentOptionListKey(supplierId: supplierId, customerUserId: userIdFromToken)) {
            return digitalPaymentsSdk.getPaymentMethods(it.supplierId, accessToken).block()
        }
    }

    @Override
    Boolean isDelayed(String supplierOrderId, String accessToken) {
        providersCache.get(supplierOrderId) {
            digitalPaymentsSdk.isDelayedSupplierOrder(it, accessToken)
        }
    }
}

@Immutable
@EqualsAndHashCode
class PaymentOptionListKey {
    String supplierId
    String customerUserId
}
