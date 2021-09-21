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

        // TODO: implement api - just for qa ios application
        response.orderedOrderSummary = [
                new OrderSummary(
                        summary: [
                                new Summary(
                                    type: CartSummaryItemType.PRODUCTS_TOTAL,
                                    value: 30650,
                                    metadata: []
                                ),
                                new Summary(
                                        type: CartSummaryItemType.IBB,
                                        value: 1664.28,
                                        metadata: [
                                                new MetaEntry(
                                                    key: "usingDefaultAliquot",
                                                    value: "true"),
                                                new MetaEntry(
                                                        key: "aliquot",
                                                        value: "6"),
                                                new MetaEntry(
                                                        key: "NET_SUBTOTAL",
                                                        value: "27738.0")
                                        ]
                                ),
                                new Summary(
                                        type: CartSummaryItemType.DELIVERY_COST,
                                        value: 0,
                                        metadata: [
                                                new MetaEntry(
                                                        key: "usingDefaultAliquot",
                                                        value: "true"),
                                                new MetaEntry(
                                                        key: "aliquot",
                                                        value: "3")
                                        ]
                                ),
                                new Summary(
                                        type: CartSummaryItemType.DISCOUNT,
                                        value: 0,
                                        metadata: [
                                                new MetaEntry(
                                                        key: "usingDefaultAliquot",
                                                        value: "false")
                                        ]
                                ),
                                new Summary(
                                        type: CartSummaryItemType.MAX_AMOUNT_WABIPAY_ALLOWED,
                                        value: 30650,
                                        metadata: [
                                                new MetaEntry(
                                                        key: "usingDefaultAliquot",
                                                        value: "true"),
                                                new MetaEntry(
                                                        key: "aliquot",
                                                        value: "2"),
                                                new MetaEntry(
                                                        key: "NET_SUBTOTAL",
                                                        value: "27738.0")
                                        ]
                                ),
                                new Summary(
                                        type: CartSummaryItemType.CREDITS_USED,
                                        value: 0,
                                        metadata: []
                                ),
                                new Summary(
                                        type: CartSummaryItemType.WABIMONEY_USED,
                                        value: 0,
                                        metadata: [
                                                new MetaEntry(
                                                    key: "usingDefaultAliquot",
                                                    value: "true"),
                                                new MetaEntry(
                                                     key: "aliquot",
                                                     value: "1"
                                                )
                                        ]
                                ),
                                new Summary(
                                        type: CartSummaryItemType.ORDER_TOTAL,
                                        value: 30650,
                                        metadata: []
                                ),
                                new Summary(
                                        type: CartSummaryItemType.SERVICE_FEE,
                                        value: 30650,
                                        metadata: []
                                ),
                                new Summary(
                                        type: CartSummaryItemType.PAYMENT_PENDING,
                                        value: 30650,
                                        metadata: [
                                                new MetaEntry(
                                                        key: "usingDefaultAliquot",
                                                        value: "true"),
                                                new MetaEntry(
                                                        key: "aliquot",
                                                        value: "7"
                                                )
                                        ]
                                )
                        ],
                        supplier: new Supplier(
                                id: 446,
                                name: "CAROMAR",
                                legalName: "Supplier - legal test mock",
                                avatar: "f95e6bbe-5e96-45aa-b791-50a30776a699.jpg",
                                enabled: true,
                                phone: "01541012365",
                                legalId: "0125547400",
                                address: "Altamirano Gutierrez 5560",
                                postalCode: "1254",
                                maxAmount: 150000,
                                minAmount: 2000,
                                country_id: "ar",
                                averageDeliveryDay: "Monday",
                                orderPercentageCap: 23,
                                rating: new RatingScore(
                                        average: 21,
                                        count: 2,
                                        percentage: 9
                                ),
                                deliveryZones: [
                                        new DeliveryZone(
                                                id: 1,
                                                minAmount: 1000,
                                                maxAmount: 1500000,
                                                deliveryCost: 250
                                        )
                                ],
                                wabipayConfiguration: new WabipayConfiguration(
                                        use_wabipay_cap: false,
                                        order_percentage_cap: 0
                                )

                        )
                )
        ]

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















