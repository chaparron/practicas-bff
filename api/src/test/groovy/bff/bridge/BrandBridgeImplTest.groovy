package bff.bridge

import bff.bridge.data.BrandBridgeImplTestData
import bff.bridge.http.BrandBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.model.Brand
import bff.model.CoordinatesInput
import groovy.json.JsonSlurper
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
class BrandBridgeImplTest extends BrandBridgeImplTestData {

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private BrandBridgeImpl brandBridge = new BrandBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.brands).thenReturn(1L)
        brandBridge.root = new URI("http://localhost:3000/")
        brandBridge.init()
    }

    @Test
    void findBrandsWithCoordsAndCountryId() {

        Mockito.when(
                http.<List<Brand>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Brand>>(new JsonSlurper().parseText(arBrands) as List<Brand>, HttpStatus.OK))

        def brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findBrandsWithoutCoords() {

        Mockito.when(
                http.<List<Brand>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Brand>>(new JsonSlurper().parseText(arBrands) as List<Brand>, HttpStatus.OK))

        def brands = brandBridge
                .previewHomeBrands(NO_COORD_INPUT_AR)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(NO_COORD_INPUT_AR)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findBrandsWithOnlyCoordsRequest() {

        Mockito.when(
                http.<List<Brand>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Brand>>(new JsonSlurper().parseText(arBrands) as List<Brand>, HttpStatus.OK))

        def brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR_NO_COUNTRY_ID)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR_NO_COUNTRY_ID)
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findHomeBrandsUsingJwt() {

        Mockito.when(
                http.<List<Brand>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Brand>>(new JsonSlurper().parseText(arBrands) as List<Brand>, HttpStatus.OK))

        def brands = brandBridge
                .getHome(JWT_AR, "")
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .getHome(JWT_AR, "")
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findHomeBrandsUsingNoJwtAndCountryId() {

        Mockito.when(
                http.<List<Brand>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<Brand>>(new JsonSlurper().parseText(arBrands) as List<Brand>, HttpStatus.OK))

        def brands = brandBridge
                .getHome(null, "ar")
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .getHome(null, "ar")
                .brands

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }
}
