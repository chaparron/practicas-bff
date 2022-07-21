package bff.bridge.http

import bff.bridge.WalletBridge
import bff.configuration.CacheConfigurationProperties
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import groovy.transform.EqualsAndHashCode
import org.springframework.beans.factory.annotation.Autowired
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.CheckSupportedProvidersResponse
import wabi2b.payments.common.model.response.WalletResponse
import wabi2b.payments.sdk.client.WalletSdk

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class WalletBridgeImpl implements WalletBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private WalletSdk walletSdk

    private Cache<WalletCacheKey, WalletResponse> walletCache
    private Cache<SupportedProvidersCacheKey, CheckSupportedProvidersResponse> supportedProvidersCache

    @PostConstruct
    void init() {
        walletCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.wallets, TimeUnit.MINUTES)
                .build()

        supportedProvidersCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.wallets, TimeUnit.MINUTES)
                .build()
    }

    @Override
    WalletResponse getWallet(Long userId, WalletProvider walletProvider, String accessToken) {
        walletCache.get(new WalletCacheKey(userId: userId, walletProvider:  walletProvider)){
            getUnCachedWallet(it.walletProvider, accessToken)
        }
    }

    private WalletResponse getUnCachedWallet(WalletProvider walletProvider, String accessToken) {
        walletSdk.getWallet(walletProvider, accessToken)
    }

    @Override
    CheckSupportedProvidersResponse getSupportedProvidersBetween(List<String> suppliersId, String userId, WalletProvider walletProvider, String accessToken) {
        supportedProvidersCache.get(
                new SupportedProvidersCacheKey(
                        suppliersId: suppliersId,
                        userId: userId,
                        walletProvider: walletProvider
                )
        ){
            getUnCachedSupportedProviders(it.suppliersId, it.userId, it.walletProvider, accessToken)
        }
    }

    private CheckSupportedProvidersResponse getUnCachedSupportedProviders(List<String> suppliersId, String userId, WalletProvider walletProvider, String accessToken) {
        walletSdk.getSupportedProvidersBetween(suppliersId, userId, walletProvider, accessToken)
    }
}

@EqualsAndHashCode
class WalletCacheKey {
    Long userId
    WalletProvider walletProvider
}

@EqualsAndHashCode
class SupportedProvidersCacheKey {
    List<String> suppliersId
    String userId
    WalletProvider walletProvider
}
