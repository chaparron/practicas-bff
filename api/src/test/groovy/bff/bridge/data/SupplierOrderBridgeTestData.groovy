package bff.bridge.data

abstract class SupplierOrderBridgeTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"

    protected static final String APPLIED_DISCOUNT_PROMOTIONS_RESPONSE = "[\n" +
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

    protected static final String APPLIED_FREE_PROMOTIONS_RESPONSE = "[\n" +
            "    {\n" +
            "        \"promotion\": {\n" +
            "            \"id\": \"30adc8df-42a7-4b4a-b615-1bbaa540066f\",\n" +
            "            \"code\": \"FREE_PROMO\",\n" +
            "            \"description\": \"Description\",\n" +
            "            \"type\": \"FREE\",\n" +
            "            \"freeDetail\": {\n" +
            "                \"ean\": \"7798339250601\",\n" +
            "                \"quantity\": 1,\n" +
            "                \"units\": 12,\n" +
            "                \"title\": \"CocaCola\",\n" +
            "                \"image\": \"6aaf19e5-4834-4a39-a7cb-810c823d8172.png\"\n" +
            "            }\n" +
            "        },\n" +
            "        \"involvedCartItems\": [\n" +
            "            19217\n" +
            "        ]\n" +
            "    }\n" +
            "]"
}
