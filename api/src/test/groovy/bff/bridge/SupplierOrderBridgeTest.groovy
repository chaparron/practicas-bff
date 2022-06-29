package bff.bridge

import bff.bridge.data.SupplierOrderBridgeTestData
import bff.bridge.http.SupplierOrderBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.model.AppliedPromotionResponse
import bff.model.Order
import bff.model.PromotionType
import bff.model.Supplier
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

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private SupplierOrderBridge supplierOrderBridge = new SupplierOrderBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.supplierOrders).thenReturn(1L)

        supplierOrderBridge.root = new URI("http://localhost:3000/")
        supplierOrderBridge.init()
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

    @Test
    void 'should return parsed supplier by supplierOrder id from http request'() {
        //given
        def expected = 67890

        Mockito.when(
                http.<Supplier> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Supplier>
                )
        )
                .thenReturn(new ResponseEntity<Supplier>(
                        new JsonSlurper().parseText("{\"id\":$expected}") as Supplier, HttpStatus.OK
                ))

        assert supplierOrderBridge.getSupplierBySupplierOrderId(JWT_AR, 12345).id == expected
    }

    @Test
    void 'should return parsed supplier by supplierOrder id from cached response'() {
        //given
        def expected = 67890

        Mockito.when(
                http.<Supplier> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Supplier>
                )
        )
                .thenReturn(new ResponseEntity<Supplier>(
                        new JsonSlurper().parseText("{\"id\":$expected}") as Supplier, HttpStatus.OK
                ))

        assert supplierOrderBridge.getSupplierBySupplierOrderId(JWT_AR, 1111).id == expected
        assert supplierOrderBridge.getSupplierBySupplierOrderId(JWT_AR, 1111).id == expected

        Mockito
                .verify(http, Mockito.times(1))
                .<Supplier> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Supplier>
                )
    }

    @Test
    void 'should return parsed order by supplierOrder id from http request'() {
        //given
        def expected = 333

        Mockito.when(
                http.<Order> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Order>
                )
        )
                .thenReturn(new ResponseEntity<Order>(
                        new JsonSlurper().parseText("{\"id\":$expected}") as Order, HttpStatus.OK
                ))

        assert supplierOrderBridge.getOrderBySupplierOrderId(JWT_AR, 5005).id == expected
    }

    @Test
    void 'should return parsed order by supplierOrder id from cached response'() {
        //given
        def expected = 333

        Mockito.when(
                http.<Order> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Order>
                )
        )
                .thenReturn(new ResponseEntity<Order>(
                        new JsonSlurper().parseText("{\"id\":$expected}") as Order, HttpStatus.OK
                ))

        assert supplierOrderBridge.getOrderBySupplierOrderId(JWT_AR, 772).id == expected
        assert supplierOrderBridge.getOrderBySupplierOrderId(JWT_AR, 772).id == expected

        Mockito
                .verify(http, Mockito.times(1))
                .<Order> exchange(
                        Mockito.any(RequestEntity) as RequestEntity,
                        Mockito.any(Class) as Class<Order>
                )
    }
}
