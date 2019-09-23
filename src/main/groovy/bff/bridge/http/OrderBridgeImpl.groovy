package bff.bridge.http

import bff.bridge.OrderBridge
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder


@Slf4j
class OrderBridgeImpl implements OrderBridge {

    URI root
    RestOperations http

    @Override
    OrderUpdateResult cancel(CancelOrderInput cancelOrderInput) {
        try {
            def reference = new ParameterizedTypeReference<FinalOrderState>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me/order/cancel'))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    orderId: cancelOrderInput.orderId,
                                    supplierOrderId: cancelOrderInput.supplierOrderId,
                                    cancelOptionReason : cancelOrderInput.cancelOptionReason
                            ]
                    )
                    , reference).body

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to Cancel Order", e)
        }
    }

    @Override
    void cancelReason(CancelOrderInput cancelOrderInput) {
            // def reference = new ParameterizedTypeReference<FinalOrderState>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me/order/cancel/reason'))
                            .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(
                            [
                                    orderId: cancelOrderInput.orderId,
                                    supplierOrderId: cancelOrderInput.supplierOrderId,
                                    cancelOptionReason : cancelOrderInput.cancelOptionReason
                            ]
                    )
                    , Map)
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
    CustomerOrderResponse findCustomerOrder(FindSupplierOrderInput findSupplierOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/order"))
                .queryParam("orderId", findSupplierOrderInput.orderId)
                .queryParam("supplierOrderId", findSupplierOrderInput.supplierOrderId)
                .queryParam("country_id", findSupplierOrderInput.countryId)
                .toUriString().toURI()

        def customerOrderResponse = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $findSupplierOrderInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , CustomerOrderResponse).body

        customerOrderResponse.accessToken = findSupplierOrderInput.accessToken
        customerOrderResponse.supplierOrder.accessToken = findSupplierOrderInput.accessToken
        customerOrderResponse.supplierOrder.order.accessToken = findSupplierOrderInput.accessToken
        customerOrderResponse.supplierOrder.order.id = findSupplierOrderInput.orderId
        customerOrderResponse.customer.accessToken = findSupplierOrderInput.accessToken

        customerOrderResponse

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
    void placeOrder(String accessToken, List<OrderInput> orders, String wabiPayAccessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([orders: orders, wabipay_order_details: [customer_wabipay_token: wabiPayAccessToken, use_wabipay: wabiPayAccessToken != null]])
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
    OrderSummaryResponse getOrderSummary(String accessToken, List<SupplierCartProductInput> productsSupplier, String wabiPayAccessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order/summary"))
                .toUriString().toURI()

        def response = http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([products: productsSupplier, wabipay_order_details: [customer_wabipay_token: wabiPayAccessToken, use_wabipay: wabiPayAccessToken != null]])
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

    @Override
    List<OrderCancellation> getOrdersCancellation(String accessToken, Long orderId) {

        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/ordersCancellation/${orderId}"))
                .toUriString().toURI()

        def ordersCancellationReference = new ParameterizedTypeReference<List<OrderCancellation>>() {}

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , ordersCancellationReference).body
    }
}















