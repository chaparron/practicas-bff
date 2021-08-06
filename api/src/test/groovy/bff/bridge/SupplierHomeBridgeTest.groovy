package bff.bridge

import bff.bridge.http.SupplierHomeBridgeImpl
import bff.configuration.BadRequestErrorException
import bff.configuration.CacheConfigurationProperties
import bff.model.CoordinatesInput
import bff.model.PreviewHomeSupplierFailedReason
import bff.model.PreviewSupplier
import bff.model.SearchFailedReason
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
class SupplierHomeBridgeTest {

    private static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: "ar")
    private static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: "ar")
    private static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private SupplierHomeBridge supplierHomeBridge = new SupplierHomeBridgeImpl()

    private static final String arSuppliers = "[\n" +
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

    @Before
    void init() {
        Mockito.when(cacheConfiguration.suppliers).thenReturn(1L)
        supplierHomeBridge.root = new URI("http://localhost:3000/")
        supplierHomeBridge.init()
    }

    @Test
    void findSuppliersTwiceToCheckCache() {

        Mockito.when(
                http.<List<PreviewSupplier>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<PreviewSupplier>>(new JsonSlurper().parseText(arSuppliers) as List<PreviewSupplier>, HttpStatus.OK))

        def suppliers = supplierHomeBridge
                .previewHomeSuppliers(COORD_INPUT_AR)
                .suppliers

        Assert.assertNotNull(suppliers)
        Assert.assertFalse(suppliers.empty)
        Assert.assertTrue(suppliers.size() == 4)

        suppliers = supplierHomeBridge
                .previewHomeSuppliers(COORD_INPUT_AR)
                .suppliers

        Assert.assertNotNull(suppliers)
        Assert.assertFalse(suppliers.empty)
        Assert.assertTrue(suppliers.size() == 4)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findSuppliersWithoutCountryId() {

        Mockito.when(
                http.<List<PreviewSupplier>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<PreviewSupplier>>(new JsonSlurper().parseText(arSuppliers) as List<PreviewSupplier>, HttpStatus.OK))

        def suppliers = supplierHomeBridge
                .previewHomeSuppliers(COORD_INPUT_AR_NO_COUNTRY_ID)
                .suppliers

        Assert.assertNotNull(suppliers)
        Assert.assertFalse(suppliers.empty)
        Assert.assertTrue(suppliers.size() == 4)

        suppliers = supplierHomeBridge
                .previewHomeSuppliers(COORD_INPUT_AR_NO_COUNTRY_ID)
                .suppliers

        Assert.assertNotNull(suppliers)
        Assert.assertFalse(suppliers.empty)
        Assert.assertTrue(suppliers.size() == 4)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void findSuppliersErrorWithoutCoorsWithCountryId() {

        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenThrow(new BadRequestErrorException(innerResponse: SearchFailedReason.INVALID_LOCATION.name()))

        try {
            supplierHomeBridge.previewHomeSuppliers(NO_COORD_INPUT_AR)
        }
        catch (BadRequestErrorException ex) {
            def failedResponse = PreviewHomeSupplierFailedReason.valueOf((String) ex.innerResponse).build()
            Assert.assertEquals(SearchFailedReason.INVALID_LOCATION.name(), failedResponse.getReason().name())
        }

        try {
            supplierHomeBridge.previewHomeSuppliers(NO_COORD_INPUT_AR)
        }
        catch (BadRequestErrorException ex) {
            def failedResponse = PreviewHomeSupplierFailedReason.valueOf((String) ex.innerResponse).build()
            Assert.assertEquals(SearchFailedReason.INVALID_LOCATION.name(), failedResponse.getReason().name())
        }

        try {
            supplierHomeBridge.previewHomeSuppliers(NO_COORD_INPUT_AR)
        }
        catch (BadRequestErrorException ex) {
            def failedResponse = PreviewHomeSupplierFailedReason.valueOf((String) ex.innerResponse).build()
            Assert.assertEquals(SearchFailedReason.INVALID_LOCATION.name(), failedResponse.getReason().name())
        }

        finally {
            Mockito.verify(http, Mockito.times(3))
                    .exchange(
                            (RequestEntity) Mockito.any(RequestEntity.class),
                            (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
        }

    }
}