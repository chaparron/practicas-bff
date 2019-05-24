package bff.bridge.http

import bff.bridge.OrderBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.CancelOrderInput
import bff.model.CustomerOrdersResult
import bff.model.FindOrdersInput
import bff.model.OrderUpdateReason
import bff.model.SearchResult
import groovy.util.logging.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.swing.text.html.parser.Entity

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
                , SearchResult).body
        }
        catch (BadRequestErrorException ex) {
            OrderUpdateReason.INVALID_SUPPLIER_ORDERS_STATUS.doThrow()
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.doThrow()
        }
        return
    }

    @Override
    CustomerOrdersResult findCustomerOrders(FindOrdersInput findOrdersInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/order"))
            .toUriString().toURI()

        try {
            http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer $cancelOrderInput.accessToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(findOrdersInput)
                , SearchResult).body
        }
        catch (BadRequestErrorException ex) {
            OrderUpdateReason.INVALID_SUPPLIER_ORDERS_STATUS.doThrow()
        }
        catch (EntityNotFoundException ex) {
            OrderUpdateReason.ORDER_NOT_FOUND.doThrow()
        }
        return
    }
}
