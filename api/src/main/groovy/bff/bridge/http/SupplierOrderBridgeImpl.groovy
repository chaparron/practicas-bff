package bff.bridge.http

import bff.bridge.SupplierOrderBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.*
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class SupplierOrderBridgeImpl implements SupplierOrderBridge {

    URI root
    RestOperations http

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private Cache<Long, Supplier> suppliersCache
    private Cache<Long, Order> ordersCache

    @PostConstruct
    void init() {
        suppliersCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.supplierOrders, TimeUnit.MINUTES)
                .build()

        ordersCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.supplierOrders, TimeUnit.MINUTES)
                .build()
    }

    @Override
    Supplier getSupplierBySupplierOrderId(String accessToken, Long supplierOrderId) {
        suppliersCache.get(supplierOrderId){
            getUnCachedSupplierBySupplierOrderId(accessToken, it)
        }
    }

    @Override
    List<OrderItem> getOrderItemsBySupplierOrderId(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/${supplierOrderId}/items"))
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<OrderItem>>() {}).body

        r.each {
            it.accessToken = accessToken
            it.product?.prices?.each {
                it.accessToken = accessToken
            }
        }
        r
    }

    @Override
    RatingEntry getRatingBySupplierOrderId(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/${supplierOrderId}/ratings"))
            .toUriString().toURI()

        def ratings = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Rating>>() {}).body

        def supplierRating = ratings?.find { it.owner == RatingOwner.SUPPLIER }
        supplierRating?.accessToken = accessToken

        def customerRating = ratings?.find { it.owner == RatingOwner.CUSTOMER }
        customerRating?.accessToken = accessToken

        new RatingEntry(
                SUPPLIER: supplierRating,
                CUSTOMER: customerRating
        )

    }

    @Override
    Order getOrderBySupplierOrderId(String accessToken, Long supplierOrderId) {
        ordersCache.get(supplierOrderId){
            getUnCachedOrderBySupplierOrderId(accessToken, it)
        }
    }

    @Override
    Product getProductByOrderItem(String accessToken, Long orderItemId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/orderItem/${orderItemId}/product"))
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Product).body

        r.accessToken = accessToken
        r.prices?.each {
            it?.supplier?.accessToken = accessToken
        }
        //r.category = r.category.id
        r
    }

    @Override
    PartialSummary getPartialSummaryByOrderItem(String accessToken, Long orderItemId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/orderitem/${orderItemId}/partialSummary"))
                .toUriString().toURI()

        def partialSummaries = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , PartialSummary).body

        partialSummaries
    }

    @Override
    List<AppliedPromotionResponse> getPromotionsBySupplierOrderId(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/${supplierOrderId}/promotions"))
                .toUriString().toURI()

        return http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , new ParameterizedTypeReference<List<AppliedPromotionResponse>>() {}).body
    }

    private Supplier getUnCachedSupplierBySupplierOrderId(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/${supplierOrderId}/supplier"))
                .toUriString().toURI()
        def r = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , Supplier).body

        r.accessToken = accessToken
        r
    }

    private Order getUnCachedOrderBySupplierOrderId(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrders/${supplierOrderId}/order"))
                .toUriString().toURI()

        def r = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , Order).body

        r.accessToken = accessToken
        r

    }
}
