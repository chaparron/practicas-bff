package bff.bridge

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
class PromotionBridgeImplTest {

    private static final String JWT_AR = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
    private static final CoordinatesInput COORD_INPUT_AR = new CoordinatesInput(lat: 1, lng: 1, countryId: TARGET_COUNTRY_ID)
    private static final CoordinatesInput NO_COORD_INPUT_AR = new CoordinatesInput(countryId: TARGET_COUNTRY_ID)
    private static final CoordinatesInput COORD_INPUT_AR_NO_COUNTRY_ID = new CoordinatesInput(lat: 1, lng: 1)
    public static final String TARGET_COUNTRY_ID = "ar"

    @Mock
    RestOperations http

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @InjectMocks
    private PromotionBridgeImpl promotionBridge = new PromotionBridgeImpl()

    private static final Promotion singlePromotion = new Promotion(
            id: 1,
            banner: "8697dc0a-d14d-4caa-8835-3441a155cae2.png",
            banner_mobile: "8c2a0cd7-de6a-41af-81ef-fe0c05f5abba.png",
            tag: "test_tag",
            country_id: TARGET_COUNTRY_ID
    )

    private static final String promotionJsonResponse = "{\n" +
            "  \"headers\": {\n" +
            "    \"page\": 1,\n" +
            "    \"page_size\": 200,\n" +
            "    \"total\": 2,\n" +
            "    \"sort\": {}\n" +
            "  },\n" +
            "  \"content\": [\n" +
            "    {\n" +
            "      \"id\": 8,\n" +
            "      \"position\": 1,\n" +
            "      \"to_date\": \"2021-11-30\",\n" +
            "      \"created\": \"2019-09-04T20:57:06+0000\",\n" +
            "      \"banner\": \"8697dc0a-d14d-4caa-8835-3441a155cae2.png\",\n" +
            "      \"banner_mobile\": \"8c2a0cd7-de6a-41af-81ef-fe0c05f5abba.png\",\n" +
            "      \"number_of_products\": 5,\n" +
            "      \"tag\": \"juanpi\",\n" +
            "      \"country_id\": \"ar\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": 20,\n" +
            "      \"position\": 1,\n" +
            "      \"to_date\": \"2022-03-05\",\n" +
            "      \"created\": \"2021-03-22T20:24:52+0000\",\n" +
            "      \"banner\": \"289fa29f-9d19-4b0b-a14f-ba65b46671ca.jpeg\",\n" +
            "      \"banner_mobile\": \"2d0cc49d-4461-48f3-9060-2b2103d17ce5.jpeg\",\n" +
            "      \"number_of_products\": 6,\n" +
            "      \"tag\": \"prueba\",\n" +
            "      \"country_id\": \"ar\"\n" +
            "    }\n" +
            "  ]\n" +
            "}"

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

        def promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID))
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

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID))
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.previewPromotions(COORD_INPUT_AR)
        Assert.assertNotNull(promotions)
        Assert.assertFalse(promotions.content.empty)
        Assert.assertTrue(promotions.content.size() == 2)

        promotions = promotionBridge.getAll(new PromotionInput(country_id: TARGET_COUNTRY_ID))
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
