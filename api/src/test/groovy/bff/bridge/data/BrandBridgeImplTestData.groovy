package bff.bridge.data

import bff.model.CoordinatesInput

abstract class BrandBridgeImplTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    protected static final String JWT_ES = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJlcyJ9XX19.2n-uzIWGZMqK53Kea-tzHjnMw8fl2PD-fXbR3zYwAQU"
    protected static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    protected static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: "ar")
    protected static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    protected static final String arBrands = "[\n" +
            "  {\n" +
            "    \"id\": 130,\n" +
            "    \"name\": \"CITRIC\",\n" +
            "    \"enabled\": true,\n" +
            "    \"logo\": \"6c63bad4-b66e-453b-b5cf-ed5f66cd0cc5.jpg\",\n" +
            "    \"country_id\": \"ar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 21,\n" +
            "    \"name\": \"Arcor\",\n" +
            "    \"enabled\": true,\n" +
            "    \"logo\": \"05887f06-cd35-40d3-b0fd-233f0fa57e0a.jpg\",\n" +
            "    \"country_id\": \"ar\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 9,\n" +
            "    \"name\": \"Powerade\",\n" +
            "    \"enabled\": true,\n" +
            "    \"logo\": \"1cfcd378-d6fc-4619-8678-7288fa483488.png\",\n" +
            "    \"country_id\": \"ar\"\n" +
            "  }\n" +
            "]"
    
    
}
