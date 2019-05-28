package bff.bridge.http

import bff.bridge.OrderBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import java.lang.Void

@Slf4j
class OrderBridgeImpl implements OrderBridge {

    URI root
    RestOperations http

    @Override
    void cancel(CancelOrderInput cancelOrderInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/user/me/order/${cancelOrderInput.orderId}/cancel"))
            .toUriString().toURI()

        try {
            http.exchange(
                RequestEntity.method(HttpMethod.PUT, uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cancelOrderInput)
                , Map).body
            Void.SUCCESS
        }
        catch (BadRequestErrorException ex) {
            OrderUpdateReason.INVALID_SUPPLIER_ORDERS_STATUS.doThrow()
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.doThrow()
        }
    }

    @Override
    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order"))
            .toUriString().toURI()

        try {
            def r = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $findOrdersInput.accessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(findOrdersInput)
                , CustomerOrdersResponse).body

            r.content.each { it.accessToken = findOrdersInput.accessToken }
            r
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.doThrow()
        }
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
}
