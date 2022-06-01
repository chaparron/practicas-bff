package bff.bridge

import bff.bridge.data.SupplierOrderBridgeTestData
import bff.bridge.http.SupplierOrderBridgeImpl
import bff.model.AppliedPromotionResponse
import bff.model.PromotionType
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
class SupplierOrderBridgeTest extends SupplierOrderBridgeTestData {

    @Mock
    RestOperations http

    @InjectMocks
    private SupplierOrderBridge supplierOrderBridge = new SupplierOrderBridgeImpl()

    @Before
    void init() {
        supplierOrderBridge.root = new URI("http://localhost:3000/")
    }

    @Test
    void 'should return applied promotions with type discount'() {
        // given
        Mockito.when(
                http.<List<AppliedPromotionResponse>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<AppliedPromotionResponse>>(
                        new JsonSlurper().parseText(APPLIED_DISCOUNT_PROMOTIONS_RESPONSE) as List<AppliedPromotionResponse>, HttpStatus.OK)
                )

        // when
        List<AppliedPromotionResponse> appliedPromotions = supplierOrderBridge.getPromotionsBySupplierOrderId(
                JWT_AR,
                1
        )

        // then
        Assert.assertNotNull(appliedPromotions)
        Assert.assertFalse(appliedPromotions.empty)
        Assert.assertEquals(PromotionType.DISCOUNT.name(), appliedPromotions.first().promotion.type)
    }

    @Test
    void 'should return applied promotions with type free'() {
        // given
        Mockito.when(
                http.<List<AppliedPromotionResponse>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<AppliedPromotionResponse>>(
                        new JsonSlurper().parseText(APPLIED_FREE_PROMOTIONS_RESPONSE) as List<AppliedPromotionResponse>, HttpStatus.OK)
                )

        // when
        List<AppliedPromotionResponse> appliedPromotions = supplierOrderBridge.getPromotionsBySupplierOrderId(
                JWT_AR,
                1
        )

        // then
        Assert.assertNotNull(appliedPromotions)
        Assert.assertFalse(appliedPromotions.empty)
        Assert.assertEquals(PromotionType.FREE.name(), appliedPromotions.first().promotion.type)
        Assert.assertNotNull(appliedPromotions.first().promotion.freeDetail)
    }

    @Test
    void 'should return empty applied promotions'() {
        // given
        Mockito.when(
                http.<List<AppliedPromotionResponse>> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<List<AppliedPromotionResponse>>(
                        new JsonSlurper().parseText("[]") as List<AppliedPromotionResponse>, HttpStatus.OK)
                )

        // when
        List<AppliedPromotionResponse> appliedPromotions = supplierOrderBridge.getPromotionsBySupplierOrderId(
                JWT_AR,
                1
        )

        // then
        Assert.assertNotNull(appliedPromotions)
        Assert.assertTrue(appliedPromotions.empty)
    }
}
