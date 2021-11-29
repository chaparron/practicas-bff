package bff.bridge.data

import bff.model.OrderInput
import bff.model.ProductOrderInput
import bff.model.ValidateOrderInput

abstract class OrderBridgeImplTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"

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
}
