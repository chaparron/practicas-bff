package bff.bridge.http

import bff.bridge.SupplierBridge
import bff.configuration.CacheConfigurationProperties
import bff.service.HttpBridge
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class SupplierBridgeImpl implements SupplierBridge {

    URI root

    @Autowired
    HttpBridge httpBridge

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<SupplierAverageDeliveryDaysKey, String> supplierAverageCache

    @PostConstruct
    void init() {
        supplierAverageCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.suppliers, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<SupplierAverageDeliveryDaysKey, String>() {
                            @Override
                            String load(SupplierAverageDeliveryDaysKey key) throws Exception {
                                getUnCachedAverageDeliveryDays(key.accessToken, key.supplierId)
                            }
                        }
                )
    }

    String getAverageDeliveryDays(String accessToken, Long supplierId) {
        supplierAverageCache.get(new SupplierAverageDeliveryDaysKey(
                accessToken: accessToken,
                supplierId: supplierId
        ))
    }

    String getUnCachedAverageDeliveryDays(String accessToken, Long supplierId) {
        URI uri = UriComponentsBuilder.fromUri(root.resolve("/supplier/${supplierId}/average-delivery")).toUriString().toURI()

        httpBridge.get(uri, "Bearer $accessToken" , null, String.class)
    }


}

@EqualsAndHashCode(includes = ["supplierId"] )
@InheritConstructors
class SupplierAverageDeliveryDaysKey {
    String accessToken
    Long supplierId
}
