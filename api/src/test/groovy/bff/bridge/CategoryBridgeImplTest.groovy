package bff.bridge

import bff.bridge.http.CategoryBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.model.Category
import bff.model.CoordinatesInput
import bff.service.HttpBridge
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.class)
class CategoryBridgeImplTest {

    @Mock
    HttpBridge httpBridge

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
    void findRootCategoriesTest() {
        def expectedResponse = [
                new Category(id: 1L, parentId: 1L, name: "Test1", enabled: true),
                new Category(id: 2L, parentId: 2L, name: "Test2", enabled: true)
        ]

        Mockito.when(
                httpBridge.get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.anyString(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        expectedResponse)

        def response = categoryBridge.findRootCategories("1234")
        Assert.assertNotNull(response)
        Assert.assertTrue(response.size() == 2)
        Assert.assertEquals(expectedResponse.get(0).id, response.get(0).id)
        Assert.assertEquals(expectedResponse.get(1).id, response.get(1).id)

        response = categoryBridge.findRootCategories("1234")
        Assert.assertNotNull(response)
        Assert.assertTrue(response.size() == 2)
        Assert.assertEquals(expectedResponse.get(0).id, response.get(0).id)
        Assert.assertEquals(expectedResponse.get(1).id, response.get(1).id)

        Mockito.verify(httpBridge, Mockito.times(2))
                .get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.anyString(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }

    @Test
    void previewRootCategoriesTest() {
        def expectedResponse = [
                new Category(id: 1L, parentId: 1L, name: "Test1", enabled: true),
                new Category(id: 2L, parentId: 2L, name: "Test2", enabled: true)
        ]

        Mockito.when(
                httpBridge.get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class)))
                .thenReturn(
                        expectedResponse)

        def response = categoryBridge.previewRootCategories(new CoordinatesInput(lat: 1, lng: 1, countryId: "ar"))
        Assert.assertNotNull(response)
        Assert.assertTrue(response.categories.size() == 2)
        Assert.assertEquals(expectedResponse.get(0).id, response.categories.get(0).id)
        Assert.assertEquals(expectedResponse.get(1).id, response.categories.get(1).id)

        response = categoryBridge.previewRootCategories(new CoordinatesInput(lat: 1, lng: 1, countryId: "ar"))
        Assert.assertNotNull(response)
        Assert.assertTrue(response.categories.size() == 2)
        Assert.assertEquals(expectedResponse.get(0).id, response.categories.get(0).id)
        Assert.assertEquals(expectedResponse.get(1).id, response.categories.get(1).id)

        Mockito.verify(httpBridge, Mockito.times(1))
                .get(
                        (URI)Mockito.any(URI.class),
                        (String)Mockito.isNull(),
                        Mockito.isNull(),
                        Mockito.any(Class.class))
    }


}
