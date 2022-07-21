package bff.bridge

import bff.bridge.data.OrderBridgeImplTestData
import bff.bridge.http.OrderBridgeImpl
import bff.model.CustomerOrdersResponse
import bff.model.FilterOrderStatus
import bff.model.ValidateOrderResponse
import bff.model.ValidateOrderResponseV1
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

    @Deprecated
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
    void testValidateOrderV1() {
        // given
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/v1/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_INPUT_V1.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_INPUT_V1.orders])
                        , ValidateOrderResponseV1))
                .thenReturn(new ResponseEntity<ValidateOrderResponseV1>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_ERROR) as ValidateOrderResponseV1, HttpStatus.OK)
                )

        // when
        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_INPUT_V1)

        // then
        Assert.assertEquals(2, validateOrder.errors.size())
    }

    @Test
    void testValidateOrderV2() {
        // given
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/v2/order/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_INPUT_V2.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_INPUT_V2.orders])
                        , ValidateOrderResponseV1))
                .thenReturn(new ResponseEntity<ValidateOrderResponseV1>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_ERROR) as ValidateOrderResponseV1, HttpStatus.OK)
                )

        // when
        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_INPUT_V2)

        // then
        Assert.assertEquals(2, validateOrder.errors.size())
    }

    @Test
    void testValidateOrderWithFreePromotion() {
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_PROMOTION_FREE_INPUT.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_PROMOTION_FREE_INPUT.orders])
                        , ValidateOrderResponse))
                .thenReturn(new ResponseEntity<ValidateOrderResponse>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_ERROR) as ValidateOrderResponse, HttpStatus.OK)
                )

        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_PROMOTION_FREE_INPUT)
        Assert.assertEquals(2, validateOrder.errors.size())
    }

    @Test
    void testValidateOrderWithFreePromotionV1() {
        // given
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/v1/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_PROMOTION_FREE_INPUT_V1.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_PROMOTION_FREE_INPUT_V1.orders])
                        , ValidateOrderResponseV1))
                .thenReturn(new ResponseEntity<ValidateOrderResponseV1>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_ERROR) as ValidateOrderResponseV1, HttpStatus.OK)
                )

        // when
        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_PROMOTION_FREE_INPUT_V1)

        // then
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

    @Test
    void testValidateOrderEmptyV1() {
        // given
        Mockito.when(
                http.exchange(
                        RequestEntity.method(HttpMethod.POST, UriComponentsBuilder.fromUri(orderBridge.root.resolve("/order/v1/cart/validate"))
                                .toUriString().toURI())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer $VALIDATE_ORDER_INPUT_V1.accessToken")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body([orders: VALIDATE_ORDER_INPUT_V1.orders])
                        , ValidateOrderResponseV1))
                .thenReturn(new ResponseEntity<ValidateOrderResponseV1>(
                        new JsonSlurper().parseText(VALIDATE_ORDER_RESPONSE_EMPTY) as ValidateOrderResponseV1, HttpStatus.OK)
                )

        // when
        def validateOrder = orderBridge.validateOrder(VALIDATE_ORDER_INPUT_V1)

        // then
        Assert.assertNull(validateOrder.errors)
    }

    @Test
    void findCustomerOrders() {
        def uriStr = UriComponentsBuilder.fromUri(
                orderBridge.root.resolve("/customer/me/order")
        ).toUriString()

        String expectedUrl = UriComponentsBuilder.fromHttpUrl(uriStr)
                .queryParam("page", 1L)
                .queryParam("size", 10L)
                .queryParam("status", FilterOrderStatus.ALL)
                .queryParam("id", 1)
                .queryParam("from", 1646036565193L)
                .queryParam("to", null)
                .encode()
                .toUriString()
        def expectedRequestEntity = RequestEntity.method(HttpMethod.GET, expectedUrl.toURI())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $FIND_ORDERS_INPUT.accessToken")
                .build()
        Mockito.when(
                http.exchange(expectedRequestEntity, CustomerOrdersResponse))
                .thenReturn(
                        new ResponseEntity<CustomerOrdersResponse>(
                                new JsonSlurper().parseText(CUSTOMER_ORDERS_RESPONSE_EMPTY) as CustomerOrdersResponse,
                                HttpStatus.OK
                        )
                )

        def validateOrder = orderBridge.findCustomerOrders(FIND_ORDERS_INPUT)
        Assert.assertEquals(1, validateOrder.headers.getPage())
        Assert.assertEquals(10, validateOrder.headers.getPage_size())
        Assert.assertEquals(0, validateOrder.headers.getTotal())
        Assert.assertNull(validateOrder.headers.getSort().field)
        Assert.assertNull(validateOrder.headers.getSort().direction)
        Assert.assertEquals([], validateOrder.content)
    }
}
