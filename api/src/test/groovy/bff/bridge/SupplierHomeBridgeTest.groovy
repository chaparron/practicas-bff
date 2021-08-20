package bff.bridge

import bff.bridge.data.SupplierHomeBridgeTestData
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
class SupplierHomeBridgeTest extends SupplierHomeBridgeTestData {

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private SupplierHomeBridge supplierHomeBridge = new SupplierHomeBridgeImpl()

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