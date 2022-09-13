package bff.bridge.http

import bff.JwtToken
import bff.bridge.BnplBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.CreditLines
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.SupportedMinimumAmountResponse
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

import static bff.model.CreditLines.fromSdk

class BnplBridgeImpl implements BnplBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private BnPlSdk bnPlSdk

    private Logger logger = LoggerFactory.getLogger(BnplBridge.class)

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

    @Override
    Boolean isSupplierOnboarded(Long supplierId, String accessToken) {
        logger.trace("Attempt to fetch walletId for supplierId {}", supplierId)
        def status = bnPlSdk.supplierStatus(supplierId, accessToken)

        return status.block().onboarded
    }

    @Override
    CreditLines userBalance(String accessToken) {
        def userId = JwtToken.userIdFromToken(accessToken)
        bnPlSdk.fetchBalance(userId.toLong(), accessToken)
                .map {
                    fromSdk(it)
                }
                .toFuture().get()
    }
}


