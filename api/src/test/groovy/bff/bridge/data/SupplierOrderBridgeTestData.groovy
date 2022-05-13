package bff.bridge.data

abstract class SupplierOrderBridgeTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"

    protected static final String APPLIED_PROMOTIONS_RESPONSE = "[\n" +
            "                    {\n" +
            "                        \"promotion\": {\n" +
            "                            \"id\": \"30adc8df-42a7-4b4a-b615-1bbaa540066f\",\n" +
            "                            \"description\": \"Description\",\n" +
            "                            \"code\": \"SimpleMile\",\n" +
            "                            \"type\": \"DISCOUNT\"\n" +
            "                        },\n" +
            "                        \"involvedCartItems\": [\n" +
            "                            \"19217\"\n" +
            "                        ]\n" +
            "                    }\n" +
            "                ]"

}
