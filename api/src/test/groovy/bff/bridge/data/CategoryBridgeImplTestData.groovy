package bff.bridge.data

import bff.model.Category
import bff.model.CoordinatesInput

abstract class CategoryBridgeImplTestData {

    protected static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    protected static final String JWT_ES = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJlcyJ9XX19.2n-uzIWGZMqK53Kea-tzHjnMw8fl2PD-fXbR3zYwAQU"
    protected static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    protected static final CoordinatesInput COORD_INPUT_ES = new CoordinatesInput(lat: 2, lng: 2, countryId: "es")
    protected static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    protected final def CATEGORIES_API_RESPONSE = [
            new Category(id: 1L, parentId: 1L, name: "Test1", enabled: true),
            new Category(id: 2L, parentId: 2L, name: "Test2", enabled: true)
    ]

}
