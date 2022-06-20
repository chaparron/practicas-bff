package bff.bridge.data

import bff.model.*
import bff.model.order.CartLineInput
import bff.model.order.OrderInputV2
import bff.model.order.ValidateOrderInputV2

abstract class OrderBridgeImplTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"

    @Deprecated
    protected static final ValidateOrderInput VALIDATE_ORDER_INPUT = new ValidateOrderInput(
            accessToken: JWT_AR,
            orders: [new OrderInput(
                    supplierId: 1,
                    deliveryZoneId: 1L,
                    deliveryCost: new BigDecimal(10),
                    products: [new ProductOrderInput(
                            productId: 1,
                            units: 1,
                            quantity: 1,
                            price: new BigDecimal(10)
                    )]
            )]
    )
    protected static final ValidateOrderInputV1 VALIDATE_ORDER_INPUT_V1 = new ValidateOrderInputV1(
            accessToken: JWT_AR,
            orders: [new OrderInputV1(
                    supplierId: 1,
                    deliveryZoneId: 1L,
                    deliveryCost: new BigDecimal(10),
                    products: [new ProductOrderInput(
                            productId: 1,
                            units: 1,
                            quantity: 1,
                            price: new BigDecimal(10)
                    )]
            )]
    )
    protected static final ValidateOrderInputV2 VALIDATE_ORDER_INPUT_V2 = new ValidateOrderInputV2(
            accessToken: JWT_AR,
            orders: [new OrderInputV2(
                    supplierId: 1,
                    deliveryZoneId: 1L,
                    deliveryCost: new BigDecimal(10),
                    products: [new CartLineInput(
                            productId: 1,
                            units: 1,
                            quantity: 1,
                            price: new BigDecimal(10)
                    )]
            )]
    )

    protected static final ValidateOrderInput VALIDATE_ORDER_PROMOTION_FREE_INPUT = new ValidateOrderInput(
            accessToken: JWT_AR,
            orders: [new OrderInput(
                    supplierId: 1,
                    deliveryZoneId: 1L,
                    deliveryCost: new BigDecimal(10),
                    products: [new ProductOrderInput(
                            productId: 1,
                            units: 1,
                            quantity: 1,
                            price: new BigDecimal(10)
                    )],
                    productsFree: [new ProductFreeInput(
                            triggerCartItems: [new TriggerCartItem(
                                    productId: 1,
                                    units: 1
                            )],
                            product: new ProductFreeItemInput(
                                    productId: 2,
                                    units: 1,
                                    quantity: 1
                            )
                    )]
            )]
    )

    protected static final ValidateOrderInputV1 VALIDATE_ORDER_PROMOTION_FREE_INPUT_V1 = new ValidateOrderInputV1(
            accessToken: JWT_AR,
            orders: [new OrderInputV1(
                    supplierId: 1,
                    deliveryZoneId: 1L,
                    deliveryCost: new BigDecimal(10),
                    products: [new ProductOrderInput(
                            productId: 1,
                            units: 1,
                            quantity: 1,
                            price: new BigDecimal(10)
                    )],
                    appliedPromotions: [new AppliedPromotionInput(
                            type: PromotionType.FREE,
                            triggerCartItems: [new TriggerCartItem(
                                    productId: 1,
                                    units: 1
                            )],
                            product: new ProductFreeItemInput(
                                    productId: 2,
                                    units: 1,
                                    quantity: 1
                            ),
                            promotionId: "1"
                    )]
            )]
    )

    protected static final FindOrdersInput FIND_ORDERS_INPUT = new FindOrdersInput(
            page: 1,
            size: 10,
            accessToken: JWT_AR,
            orderId: 1,
            countryId: "ar",
            status: FilterOrderStatus.ALL,
            period: new MillisecondsPeriodInput(from: 1646036565193L)
    )

    protected static final String VALIDATE_ORDER_RESPONSE_EMPTY =
            "{\n" +
                    "}"

    protected static final String VALIDATE_ORDER_RESPONSE_ERROR =
            "{\n" +
                    "    \"errors\": [\n" +
                    "        {\n" +
                    "            \"field\": \"orders\",\n" +
                    "            \"message\": \"INVALID_ORDER\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"field\": \"orders\",\n" +
                    "            \"message\": \"INVALID_ORDER\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"

    protected static final String CUSTOMER_ORDERS_RESPONSE_EMPTY =
            "{\n" +
                    "    \"headers\": {\n" +
                    "            \"page\": 1,\n" +
                    "            \"page_size\": 10,\n" +
                    "            \"total\": 0,\n" +
                    "            \"sort\": {}\n" +
                    "    },\n" +
                    "    \"content\": []\n" +
                    "}"
}
