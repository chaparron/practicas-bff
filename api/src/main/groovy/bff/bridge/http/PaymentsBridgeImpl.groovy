package bff.bridge.http

import bff.bridge.PaymentsBridge
import bff.configuration.CacheConfigurationProperties
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse
import wabi2b.payments.sdk.client.WabiPaymentSdkClient

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class PaymentsBridgeImpl implements PaymentsBridge {

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    @Autowired
    private WabiPaymentSdkClient wabiPaymentSdkClient

    private Cache<Long, Mono<GetSupplierOrderPaymentResponse>> supplierOrderPaymentCache

    @PostConstruct
    void init() {
        supplierOrderPaymentCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.supplierOrderPayments, TimeUnit.SECONDS)
                .build()
    }

    @Override
    Mono<GetSupplierOrderPaymentResponse> getSupplierOrderPayments(GetSupplierOrderPaymentRequest getSupplierOrderPaymentRequest, String apiClientToken) {
        supplierOrderPaymentCache.get(getSupplierOrderPaymentRequest.supplierOrderId) {
            wabiPaymentSdkClient.getSupplierOrderPayments(getSupplierOrderPaymentRequest, apiClientToken)
        }
    }
}
