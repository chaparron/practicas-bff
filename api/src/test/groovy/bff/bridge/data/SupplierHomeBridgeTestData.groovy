package bff.bridge.data

import bff.model.CoordinatesInput

abstract class SupplierHomeBridgeTestData {

    protected static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    protected static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: "ar")
    protected static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    protected static final String arSuppliers = "[\n" +
            "  {\n" +
            "    \"id\": 17,\n" +
            "    \"name\": \"TEST Masivos S.A.\",\n" +
            "    \"legalName\": \"TEST Masivos S.As\",\n" +
            "    \"avatar\": \"fba7a4e7-df7f-4c61-aa29-87b97af4c1d3.png\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 18,\n" +
            "    \"name\": \"Lucciano\",\n" +
            "    \"legalName\": \"Luccianos\",\n" +
            "    \"avatar\": \"b1623cb8-bd77-46db-93be-985313eef4e5.jpg\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 1,\n" +
            "    \"name\": \"Vital\",\n" +
            "    \"legalName\": \"421321\",\n" +
            "    \"avatar\": \"6f055fd8-b68a-4e91-8b68-dd3782c9d583.jpeg\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": 19,\n" +
            "    \"name\": \"TEST Potigian\",\n" +
            "    \"legalName\": \"TEST Potigian\",\n" +
            "    \"avatar\": \"463d864f-3fe9-44cd-baab-5449b117d40a.jpg\"\n" +
            "  }\n" +
            "]"
}
