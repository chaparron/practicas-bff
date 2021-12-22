package bff.bridge

import bff.bridge.data.PromotionBridgeImplTestData
import bff.bridge.http.PromotionBridgeImpl
import bff.configuration.CacheConfigurationProperties
import bff.model.*
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
class PromotionBridgeImplTest extends PromotionBridgeImplTestData {

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private PromotionBridgeImpl promotionBridge = new PromotionBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.promotions).thenReturn(1L)
        promotionBridge.root = new URI("http://localhost:3000/")
        promotionBridge.init()
    }

    @Test
    void testPromotionAllCache() {

        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void testPromotionNoCountryNoCache() {

        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.previewPromotions(COORD_INPUT_AR_NO_COUNTRY_ID)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR_NO_COUNTRY_ID)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void testPromotionPreviewCache() {
        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }

    @Test
    void testPromotionPreviewCacheNoCountryId() {
        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.previewPromotions(COORD_INPUT_AR_NO_COUNTRY_ID)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR_NO_COUNTRY_ID)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(2))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))
    }


    @Test
    void testPromotionPreviewAndPromotion() {

        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)


        promotions = promotionBridge.previewPromotions(NO_COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))

    }

    @Test
    void testPromotionPreviewAndPromotionReverse() {

        Mockito.when(
                http.exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<PaginatedResponse<Promotion>>(new JsonSlurper().parseText(promotionJsonResponse) as PaginatedResponse<Promotion>, HttpStatus.OK))

        def promotions = promotionBridge.previewPromotions(NO_COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID, accessToken: JWT_AR))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (ParameterizedTypeReference) Mockito.any(ParameterizedTypeReference.class))

    }

    @Test
    void testLandingPromotion() {

        Mockito.when(
                http.<Promotion> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
        )
                .thenReturn(new ResponseEntity<Promotion>(singlePromotion, HttpStatus.OK))

        def promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
    }

    @Test
    void testLandingPreviewPromotion() {

        Mockito.when(
                http.<Promotion> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
        )
                .thenReturn(new ResponseEntity<Promotion>(singlePromotion, HttpStatus.OK))

        def promotion = promotionBridge.previewLandingPromotion(COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.previewLandingPromotion(NO_COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
    }

    @Test
    void testLandingPromotionPreviewAndLandingPromotion() {

        Mockito.when(
                http.<Promotion> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
        )
                .thenReturn(new ResponseEntity<Promotion>(singlePromotion, HttpStatus.OK))

        def promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.previewLandingPromotion(COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.previewLandingPromotion(NO_COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
    }

    @Test
    void testLandingPromotionPreviewAndLandingPromotionReverse() {

        Mockito.when(
                http.<Promotion> exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
        )
                .thenReturn(new ResponseEntity<Promotion>(singlePromotion, HttpStatus.OK))


        def promotion = promotionBridge.previewLandingPromotion(COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.previewLandingPromotion(NO_COORD_INPUT_AR)
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        promotion = promotionBridge.getLandingPromotion(new GetLandingPromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotion)
        Assert.assertEquals(TARGET_COUNTRY_ID, promotion.country_id)

        Mockito.verify(http, Mockito.times(1))
                .exchange(
                        (RequestEntity) Mockito.any(RequestEntity.class),
                        (Class) Mockito.any(Class.class))
    }

}
