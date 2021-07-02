package bff.bridge

import bff.bridge.http.CategoryBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.model.Category
import bff.model.CoordinatesInput
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestOperations

@RunWith(MockitoJUnitRunner.class)
class CategoryBridgeImplTest {
    private static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    private static final String JWT_ES = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJlcyJ9XX19.2n-uzIWGZMqK53Kea-tzHjnMw8fl2PD-fXbR3zYwAQU"
    private static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    private static final CoordinatesInput COORD_INPUT_ES = new CoordinatesInput(lat: 2, lng: 2, countryId: "es")
    private static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    CategoryBridgeImpl categoryBridge = new CategoryBridgeImpl(root: new URI("http://localhost:3000/"))

    private final def CATEGORIES_API_RESPONSE = [
            new Category(id: 1L, parentId: 1L, name: "Test1", enabled: true),
            new Category(id: 2L, parentId: 2L, name: "Test2", enabled: true)
    ]

    @Before
    void init() {
        Mockito.when(cacheConfiguration.categories).thenReturn(1L)
        categoryBridge.root = new URI("http://localhost:3000/")
        categoryBridge.init()
    }

    @Test
    void findRootCategoriesWithCountryIdSameCountryTest() {
        findRootCategoriesTest(1, JWT_AR, JWT_AR)
    }

    @Test
    void findRootCategoriesWithCountryIdDifferentCountriesTest() {
        findRootCategoriesTest(2, JWT_AR, JWT_ES)
    }

    @Test
    void previewRootCategoriesWithCountryIdSameCountryTest() {
        previewRootCategoriesTest(1, COORD_INPUT_AR, COORD_INPUT_AR)
    }

    @Test
    void previewRootCategoriesWithCountryIdDifferentCountriesTest() {
        previewRootCategoriesTest(2, COORD_INPUT_AR, COORD_INPUT_ES)
    }

    @Test
    void previewRootCategoriesWithoutCountryIdTest() {
        previewRootCategoriesTest(2, COORD_INPUT_AR_NO_COUNTRY_ID, COORD_INPUT_AR_NO_COUNTRY_ID)
    }

    private findRootCategoriesTest(int apiInvocationTimes, String... accessTokens) {
        Mockito.when(
                http.<List<Category>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Category>>(CATEGORIES_API_RESPONSE, HttpStatus.OK))

        accessTokens.each { findRootCategoriesCallTest(it, CATEGORIES_API_RESPONSE) }

        Mockito.verify(http, Mockito.times(apiInvocationTimes))
                .<List<Category>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    private findRootCategoriesCallTest(String accessToken, List<Category> expectedResponse) {
        def response = categoryBridge.findRootCategories(accessToken)
        Assert.assertNotNull(response)
        Assert.assertTrue(response.size() == 2)
        Assert.assertEquals(expectedResponse, response)
    }

    private previewRootCategoriesTest(int apiInvocationTimes, CoordinatesInput... coordinatesInputs) {
        Mockito.when(
                http.<List<Category>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Category>>(CATEGORIES_API_RESPONSE, HttpStatus.OK))

        coordinatesInputs.each { previewRootCategoriesCallTest(it, CATEGORIES_API_RESPONSE) }

        Mockito.verify(http, Mockito.times(apiInvocationTimes))
                .<List<Category>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    private previewRootCategoriesCallTest(CoordinatesInput coordinatesInput, List<Category> expectedResponse) {
        def response = categoryBridge.previewRootCategories(coordinatesInput)
        Assert.assertNotNull(response)
        Assert.assertTrue(response.categories.size() == 2)
        Assert.assertEquals(expectedResponse.get(0).id, response.categories.get(0).id)
        Assert.assertEquals(expectedResponse.get(1).id, response.categories.get(1).id)
    }
}
