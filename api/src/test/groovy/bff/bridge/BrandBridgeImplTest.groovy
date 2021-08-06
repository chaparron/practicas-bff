package bff.bridge

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
class BrandBridgeImplTest {

    private static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    private static final String JWT_ES = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJlcyJ9XX19.2n-uzIWGZMqK53Kea-tzHjnMw8fl2PD-fXbR3zYwAQU"
    private static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    private static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: "ar")
    private static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private BrandBridgeImpl brandBridge = new BrandBridgeImpl()

    private static final String arBrands = "[\n" +
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
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR)
                .brands;

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
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(NO_COORD_INPUT_AR)
                .brands;

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
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .previewHomeBrands(COORD_INPUT_AR_NO_COUNTRY_ID)
                .brands;

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
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .getHome(JWT_AR, "")
                .brands;

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
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        brands = brandBridge
                .getHome(null, "ar")
                .brands;

        Assert.assertNotNull(brands)
        Assert.assertFalse(brands.empty)
        Assert.assertTrue(brands.size() == 3)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }
}
