package bff.bridge

import bff.bridge.data.CategoryBridgeImplTestData
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
class CategoryBridgeImplTest extends CategoryBridgeImplTestData{

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    CategoryBridgeImpl categoryBridge = new CategoryBridgeImpl(root: new URI("http://localhost:3000/"))

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
