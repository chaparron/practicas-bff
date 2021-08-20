package bff.bridge.data

import bff.model.CoordinatesInput
import bff.model.Promotion

abstract class PromotionBridgeImplTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    protected static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: TARGET_COUNTRY_ID)
    protected static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: TARGET_COUNTRY_ID)
    protected static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)
    protected static final String TARGET_COUNTRY_ID = "ar"

    protected static final Promotion singlePromotion = new Promotion(
            id: 1,
            banner: "8697dc0a-d14d-4caa-8835-3441a155cae2.png",
            banner_mobile: "8c2a0cd7-de6a-41af-81ef-fe0c05f5abba.png",
            tag: "test_tag",
            country_id: TARGET_COUNTRY_ID
    )

    protected static final String promotionJsonResponse = "{\n" +
            "  \"headers\": {\n" +
            "    \"page\": 1,\n" +
            "    \"page_size\": 200,\n" +
            "    \"total\": 2,\n" +
            "    \"sort\": {}\n" +
            "  },\n" +
            "  \"content\": [\n" +
            "    {\n" +
            "      \"id\": 8,\n" +
            "      \"position\": 1,\n" +
            "      \"to_date\": \"2021-11-30\",\n" +
            "      \"created\": \"2019-09-04T20:57:06+0000\",\n" +
            "      \"banner\": \"8697dc0a-d14d-4caa-8835-3441a155cae2.png\",\n" +
            "      \"banner_mobile\": \"8c2a0cd7-de6a-41af-81ef-fe0c05f5abba.png\",\n" +
            "      \"number_of_products\": 5,\n" +
            "      \"tag\": \"juanpi\",\n" +
            "      \"country_id\": \"ar\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 20,\n" +
            "      \"position\": 1,\n" +
            "      \"to_date\": \"2022-03-05\",\n" +
            "      \"created\": \"2021-03-22T20:24:52+0000\",\n" +
            "      \"banner\": \"289fa29f-9d19-4b0b-a14f-ba65b46671ca.jpeg\",\n" +
            "      \"banner_mobile\": \"2d0cc49d-4461-48f3-9060-2b2103d17ce5.jpeg\",\n" +
            "      \"number_of_products\": 6,\n" +
            "      \"tag\": \"prueba\",\n" +
            "      \"country_id\": \"ar\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"
}
