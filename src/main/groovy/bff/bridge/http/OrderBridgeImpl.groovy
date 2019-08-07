package bff.bridge.http

import bff.bridge.OrderBridge
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder


@Slf4j
class OrderBridgeImpl implements OrderBridge {

    URI root
    RestOperations http

    @Override
    void cancel(CancelOrderInput cancelOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/${cancelOrderInput.orderId}/cancel"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.PUT, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(cancelOrderInput)
            , Map).body

    }

    @Override
    CustomerOrdersResponse findCustomerOrders(FindOrdersInput findOrdersInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order"))
            .queryParam("page", findOrdersInput.page)
            .queryParam("size", findOrdersInput.size)
            .queryParam("country_id", findOrdersInput.countryId)
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $findOrdersInput.accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , CustomerOrdersResponse).body

        r.content.each { it.accessToken = findOrdersInput.accessToken }
        r

    }

    @Override
    Address getDeliveryAddress(String accessToken, Long orderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/${orderId}/address"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Address).body
    }

    @Override
    List<SupplierOrder> getSupplierOrders(String accessToken, Long orderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/${orderId}/supplierOrder"))
            .toUriString().toURI()

        def param = new ParameterizedTypeReference<List<SupplierOrder>>() {}
        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , param).body

        r.each { it.accessToken = accessToken }
        r.each {
            it.rating = new RatingEntry(
                SUPPLIER: it.ratings?.get(RatingOwner.SUPPLIER),
                CUSTOMER: it.ratings?.get(RatingOwner.CUSTOMER)
            )
        }
        r
    }

    @Override
    Customer getCustomerOrder(String accessToken, Long orderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order/${orderId}/customer"))
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Customer).body

        r.accessToken = accessToken
        r
    }

    @Override
    void placeOrder(String accessToken, List<OrderInput> orders) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.POST, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body([orders: orders])
            , Map)

    }

    @Override
    SupplierOrder getSupplierOrder(String accessToken, Long supplierOrderId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/${supplierOrderId}"))
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , SupplierOrder).body

        r.accessToken = accessToken
        r
    }

    @Override
    OrderSummaryResponse getOrderSummary(String accessToken, List<SupplierCartProductInput> productsSupplier) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order/summary"))
            .toUriString().toURI()

        def response = http.exchange(
            RequestEntity.method(HttpMethod.POST, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body([products: productsSupplier])
            , OrderSummaryResponse).body

        response.orderSummary.forEach {
            it.summary.forEach { sm ->
                sm.metadata = sm?.meta?.keySet()?.collect { key ->
                    new MetaEntry(
                        key: key,
                        value: sm.meta.get(key)
                    )
                }
            }
        }

        response

    }
}
