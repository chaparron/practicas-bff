package bff.bridge.http

import bff.bridge.OrderBridge
import bff.configuration.BadRequestErrorException
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
    OrderUpdateResult cancel(CancelOrderInput cancelOrderInput) {
        def reference = new ParameterizedTypeReference<FinalOrderState>() {}
        http.exchange(
                RequestEntity.method(HttpMethod.PUT, root.resolve('/customer/me/order/cancel'))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(
                                [
                                        orderId           : cancelOrderInput.orderId,
                                        supplierOrderId   : cancelOrderInput.supplierOrderId,
                                        cancelOptionReason: cancelOrderInput.cancelOptionReason,
                                        comment           : cancelOrderInput.comment
                                ]
                        )
                , reference).body
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
                                        orderId           : cancelOrderInput.orderId,
                                        supplierOrderId   : cancelOrderInput.supplierOrderId,
                                        cancelOptionReason: cancelOrderInput.cancelOptionReason,
                                        comment           : cancelOrderInput.comment,

                                ]
                        )
                , Map)
    }

    @Override
    CustomerOrdersResponse findCustomerOrders(FindOrdersInput findOrdersInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order"))
                .queryParam("page", findOrdersInput.page)
                .queryParam("size", findOrdersInput.size)
                .queryParam("status", findOrdersInput.status)
                .queryParam("id", findOrdersInput?.orderId)
                .toUriString().toURI()

        def r = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $findOrdersInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , CustomerOrdersResponse).body

        r.content.each {
            it.accessToken = findOrdersInput.accessToken
        }
        r

    }

    @Override
    CustomerOrderResponse findCustomerOrder(FindSupplierOrderInput findSupplierOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplier/order"))
                .queryParam("orderId", findSupplierOrderInput.orderId)
                .queryParam("supplierOrderId", findSupplierOrderInput.supplierOrderId)
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
        customerOrderResponse.customer.accessToken = findSupplierOrderInput.accessToken
        customerOrderResponse.supplierOrder.products.each { it.accessToken = findSupplierOrderInput.accessToken }

        customerOrderResponse.supplierOrder.summary = customerOrderResponse.supplierOrder.metadata.summary.collect { sm ->
            new Summary(
                    type: CartSummaryItemType.valueOf(sm.type),
                    value: sm.value,
                    metadata: sm?.meta?.keySet()?.collect { key ->
                        new MetaEntry(
                                key: key,
                                value: sm.meta.get(key)
                        )
                    }
            )
        }

        customerOrderResponse

    }

    @Override
    CustomerSupplierOrdersResponse findCustomerAndSupplierOrders(FindCustomerAndSupplierOrdersInput findOrderAndSupplierOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/supplierOrder/order"))
                .queryParam("orderId", findOrderAndSupplierOrderInput.orderId)
                .toUriString().toURI()

        def customerOrderResponse = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $findOrderAndSupplierOrderInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , CustomerSupplierOrdersResponse).body

        customerOrderResponse.accessToken = findOrderAndSupplierOrderInput.accessToken
        customerOrderResponse.customer.accessToken = findOrderAndSupplierOrderInput.accessToken
        customerOrderResponse.supplierOrderAndOrderCancellations.collect {
            it.accessToken = findOrderAndSupplierOrderInput.accessToken
            it.order.accessToken = findOrderAndSupplierOrderInput.accessToken
            it.order.id = findOrderAndSupplierOrderInput.orderId
            it.products.each { it.accessToken = findOrderAndSupplierOrderInput.accessToken }

            it.summary = it.metadata.summary.collect { sm ->
                new Summary(
                        type: CartSummaryItemType.valueOf(sm.type),
                        value: sm.value,
                        metadata: sm?.meta?.keySet()?.collect { key ->
                            new MetaEntry(
                                    key: key,
                                    value: sm.meta.get(key)
                            )
                        }
                )
            }

            it.orderedSummary = it.summary.findAll {it.type.visibleToSummary}.sort{it.type.position}
        }

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
    void placeOrder(String accessToken, List<OrderInput> orders, String wabiPayAccessToken, List<String> coupons) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([orders               : orders,
                               coupons              : coupons,
                               wabipay_order_details: [customer_wabipay_token: wabiPayAccessToken, use_wabipay: wabiPayAccessToken != null,
                                                       use_wabipay_credits   : wabiPayAccessToken != null,
                                                       use_wabipay_money     : wabiPayAccessToken != null]])
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
    OrderSummaryResponse getOrderSummary(String accessToken, List<SupplierCartProductInput> productsSupplier, String wabiPayAccessToken, List<String> coupons) {
        if (!productsSupplier.size()) throw new BadRequestErrorException(innerResponse: SummaryFailedReason.PRODUCTS_UNAVAILABLE.name())
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order/summary"))
                .toUriString().toURI()

        def response = http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([products             : productsSupplier,
                               coupons              : coupons,
                               wabipay_order_details: [customer_wabipay_token: wabiPayAccessToken,
                                                       use_wabipay_credits   : wabiPayAccessToken != null, use_wabipay: wabiPayAccessToken != null,
                                                       use_wabipay_money     : wabiPayAccessToken != null]])
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

        response.orderedOrderSummary = response.orderSummary.collect {
            return new OrderSummary(
                    supplier: it.supplier,
                    summary: it.summary.findAll{it.type.visibleToSummary}.sort{it.type.position}
            )
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

    @Override
    ValidateOrderResponse validateOrder(ValidateOrderInput validateOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/order/cart/validate"))
                .toUriString().toURI()


        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $validateOrderInput.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([orders: validateOrderInput.orders])
                , ValidateOrderResponse).body

    }
}















