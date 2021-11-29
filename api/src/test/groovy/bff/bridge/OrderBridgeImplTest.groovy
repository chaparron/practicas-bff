package bff.bridge

import bff.bridge.data.OrderBridgeImplTestData
import bff.bridge.http.OrderBridgeImpl
import bff.model.ValidateOrderResponse
import groovy.json.JsonSlurper
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.http.*
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@RunWith(MockitoJUnitRunner.class)
class OrderBridgeImplTest extends OrderBridgeImplTestData {

    @Mock
    RestOperations http

    @InjectMocks
    private OrderBridgeImpl orderBridge = new OrderBridgeImpl()

    @Before
    void init() {
        orderBridge.root = new URI("http://localhost:3000/")
    }

    @Test
    void testValidateOrder() {
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_INPUT.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_INPUT.orders])
                        , ValidateOrderResponse))
                .thenReturn(new ResponseEntity<ValidateOrderResponse>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_ERROR) as ValidateOrderResponse, HttpStatus.OK)
                )

        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_INPUT)
        Assert.assertEquals(2, validateOrder.errors.size())
    }

    @Test
    void testValidateOrderEmpty() {
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_INPUT.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_INPUT.orders])
                        , ValidateOrderResponse))
                .thenReturn(new ResponseEntity<ValidateOrderResponse>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_EMPTY) as ValidateOrderResponse, HttpStatus.OK)
                )

        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_INPUT)
        Assert.assertNull(validateOrder.errors)
    }
}
