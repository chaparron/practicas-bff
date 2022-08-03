package bff.bridge.http

import bff.bridge.OrderBridge
import bff.model.FindOrdersInput
import bff.model.Order
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.matching.EqualToPattern
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

import static bff.TestExtensions.validAccessToken
import static com.github.tomakehurst.wiremock.client.WireMock.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class OrderBridgeImplWireMockTest{
    private WireMockConfiguration wireMockConfiguration = WireMockConfiguration.wireMockConfig().port(11111)
    private Integer port= wireMockConfiguration.portNumber()

    @Rule
    public TestRule wireMockRule = new WireMockRule(wireMockConfiguration)

    private OrderBridge sut

    @Before
    void setup(){
        sut = new OrderBridgeImpl(root: URI.create("http://localhost:$port"), http: new RestTemplateBuilder().build())
    }

    @Test
    void 'findCustomerOrders does not have supplierOrders'(){
        def accessToken = validAccessToken()
        def input = new FindOrdersInput(accessToken: accessToken, page: 1, size: 10)
        def orderId = new Random().nextLong()

        stubFor(
                get(urlPathEqualTo("/customer/me/order"))
                        .withQueryParam("page", equalTo(input.page.toString()))
                        .withQueryParam("size", equalTo(input.size.toString()))
                        .withHeader(HttpHeaders.CONTENT_TYPE, new EqualToPattern(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(oneElementOrderListPayload(orderId, input))
                                .withStatus(200)
                        )
        )

        def orders = sut.findCustomerOrders(input)

        assertNull(orders.content.first().supplierOrders)
    }

    @Test
    void 'getSupplierOrders paymentId resolves from metadata payment_id if exists'(){
        def accessToken = validAccessToken()

        def random = new Random()
        def orderId = random.nextLong()
        def paymentId = random.nextLong()

        stubFor(
                get(urlPathEqualTo("/customer/me/order/$orderId/supplierOrder"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, new EqualToPattern(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(anySupplierOrderListFor(orderId, paymentId))
                                .withStatus(200)
                        )
        )

        def supplierOrders = sut.getSupplierOrders(accessToken, new Order(id: orderId))

        assertEquals(paymentId, supplierOrders.first().payment.paymentId)
    }

    @Test
    void 'getSupplierOrders paymentId when metadata payment_id does not exist'(){
        def accessToken = validAccessToken()

        def random = new Random()
        def orderId = random.nextLong()
        def paymentId = null

        stubFor(
                get(urlPathEqualTo("/customer/me/order/$orderId/supplierOrder"))
                        .withHeader(HttpHeaders.CONTENT_TYPE, new EqualToPattern(MediaType.APPLICATION_JSON_VALUE))
                        .willReturn(aResponse()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody(anySupplierOrderListFor(orderId, paymentId))
                                .withStatus(200)
                        )
        )

        def supplierOrders = sut.getSupplierOrders(accessToken, new Order(id: orderId))

        assertNull(supplierOrders.first().payment)
    }

    private static String anySupplierOrderListFor(Long orderId, Long paymentId) {
        def random = new Random()
        """
[
    {
        "id": ${random.nextLong()},
        "order": {
            "id": $orderId,
            "total_wabipay_used": 0,
            "total_wabipay": 0
        },
        "supplier": {
            "id": 182,
            "enabled": true,
            "canSuggestOrder": false,
            "type": "SUPPLIER"
        },
        "status": "PENDING",
        "created": "2022-08-02T20:31:45+0000",
        "updated": "2022-08-02T20:31:45+0000",
        "deliveryCost": 0,
        "total": 540.00,
        "credits_paid": 0,
        "credits_used": 0,
        "money_paid": 0,
        "service_fee": 0,
        "total_wabipay": 0,
        "canCustomerRate": false,
        "canSupplierRate": false,
        "availabilityDifference": false,
        "localTaxes": 0,
        "metadata": {
            "summary": [
                {
                    "value": 60.00,
                    "type": "PROMOTION",
                    "meta": {}
                },
                {
                    "value": 600.00,
                    "type": "PRODUCTS_TOTAL",
                    "meta": {}
                },
                {
                    "value": 600.00,
                    "type": "SUBTOTAL",
                    "meta": {}
                },
                {
                    "value": 600.00,
                    "type": "NET_SUBTOTAL",
                    "meta": {}
                },
                {
                    "value": 0.00,
                    "type": "DELIVERY_COST",
                    "meta": {}
                },
                {
                    "value": 540.00,
                    "type": "ORDER_TOTAL",
                    "meta": {
                        "SUBTOTAL": 600.00,
                        "DELIVERY_COST": 0.00,
                        "PROMOTION": 60.00
                    }
                },
                {
                    "value": 0.00,
                    "type": "CREDITS_USED"
                },
                {
                    "value": 0.00,
                    "type": "WABIMONEY_USED"
                },
                {
                    "value": 540.00,
                    "type": "PAYMENT_PENDING"
                }
            ]
            ${Optional.ofNullable(paymentId).map{", \"payment_id\": $it" }.orElse{""}}
        },
        "cancelRequested": false,
        "total_credits_used": 0,
        "discount_used": 0,
        "total_wabipay_used": 0,
        "total_bill_customer": 540.00,
        "total_bill": 0,
        "payment_pending": 540.00,
        "subTotal": 600.00,
        "creditsUsedFromSummary": 0,
        "discounts": 0.00,
        "totalFromSummary": 540.00,
        "subTotalFromSummary": 600.00,
        "customerRated": false,
        "supplierRated": false,
        "promotionFromSummary": 60.00
    }
]
"""
    }

    private static String oneElementOrderListPayload(Long orderId, FindOrdersInput input) {
        """
{
    "headers": {
        "page": ${input.page},
        "page_size": ${input.size},
        "total": 189,
        "sort": {}
    },
    "content": [
        {
            "id": $orderId,
            "status": "PENDING",
            "created": "2022-08-02T20:31:45+0000",
            "updated": "2022-08-02T20:31:45+0000",
            "subTotal": 600.00,
            "total_credits": 0,
            "total_credits_used": 0,
            "total_money": 0,
            "total_service_fee": 0,
            "total_pending": 540.00,
            "total_taxes": 0,
            "total_delivery": 0,
            "total_discounts_used": 0,
            "discounts": 0,
            "total_wabipay_used": 0,
            "total": 540.00,
            "total_wabipay": 0
        }
    ]
}
"""
    }
}














