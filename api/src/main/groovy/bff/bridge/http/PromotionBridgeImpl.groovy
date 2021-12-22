package bff.bridge.http

import bff.bridge.PromotionBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.*
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

import static bff.JwtToken.countryFromString

@Slf4j
class PromotionBridgeImpl implements PromotionBridge {

    private static final String PROMOTION_ENDPOINT = "/promotion/"

    private static final String PROMOTION_LANDING_ENDPOINT = "/promotion/landing"

    URI root

    RestOperations http

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<Promotion>> promotionCache

    private LoadingCache<String, Promotion> promotionLandingCache

    @PostConstruct
    void init() {
        promotionCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.promotions, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, List<Promotion>>() {
                            @Override
                            List<Promotion> load(String countryId) throws Exception {
                                getUncachedPromotions(new PromotionInput(country_id: countryId))
                            }
                        }
                )

        promotionLandingCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.promotions, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<String, Promotion>() {
                            @Override
                            Promotion load(String countryId) throws Exception {
                                getUncachedLandingPromotions(new GetLandingPromotionInput(country_id: countryId))
                            }
                        }
                )
    }

    @Override
    PromotionResponse getAll(PromotionInput promotionInput) {
        def promotions = promotionCache.get(countryFromString(promotionInput.accessToken))
        return new PromotionResponse(content: promotions)
    }

    @Override
    PromotionResponse previewPromotions(CoordinatesInput coordinatesInput) {
        def promotions
        if (coordinatesInput.countryId) {
            promotions = promotionCache.getIfPresent(coordinatesInput.countryId)
            if (!promotions) {
                // If not preview promotions by countryId in cache, search by coordinates and save it
                promotions = getUncachedCoordinatesPromotions(coordinatesInput)
                promotionCache.put(coordinatesInput.countryId, promotions)
            }
        } else {
            // Make http call, no countryId present so no cache operation
            promotions = getUncachedCoordinatesPromotions(coordinatesInput)
        }
        return new PromotionResponse(content: promotions)
    }

    @Override
    Promotion getLandingPromotion(GetLandingPromotionInput promotionInput) {
        return promotionLandingCache.get(promotionInput.country_id)
    }

    @Override
    Promotion previewLandingPromotion(CoordinatesInput coordinatesInput) {
        def promotion
        if (coordinatesInput.countryId) {
            promotion = promotionLandingCache.get(coordinatesInput.countryId)
            if (!promotion) {
                // If not landing promotion by countryId in cache, search by coordinates and save it
                promotion = getUncachedCoordinatesLandingPromotions(coordinatesInput)
                promotionLandingCache.put(coordinatesInput.countryId, promotion)
            }
        } else {
            // Make http call, no countryId present so no cache operation
            promotion = getUncachedCoordinatesLandingPromotions(coordinatesInput)
        }
        return promotion
    }

    private def getUncachedPromotions(PromotionInput promotionInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve(PROMOTION_ENDPOINT))
                .queryParam("country_id", promotionInput.country_id)
                .queryParam("enable", true)

        def request = RequestEntity.method(
                HttpMethod.GET,
                uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        return http.<PaginatedResponse<Promotion>> exchange(
                request.build(),
                new ParameterizedTypeReference<PaginatedResponse<Promotion>>() {})
                .body
                .content
    }

    private def getUncachedCoordinatesPromotions(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve(PROMOTION_ENDPOINT))
                .queryParam("enable", true)
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)

        if (coordinatesInput.countryId)
            uri.queryParam("country_id", coordinatesInput.countryId)

        def request = RequestEntity.method(
                HttpMethod.GET,
                uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)


        return http.<PaginatedResponse<Promotion>> exchange(
                request.build(),
                new ParameterizedTypeReference<PaginatedResponse<Promotion>>() {})
                .body
                .content
    }

    private def getUncachedLandingPromotions(GetLandingPromotionInput promotionInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve(PROMOTION_LANDING_ENDPOINT))
                .queryParam("country_id", promotionInput.country_id)

        def request = RequestEntity.method(
                HttpMethod.GET,
                uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        if (promotionInput.accessToken)
            request.header(HttpHeaders.AUTHORIZATION, "Bearer $promotionInput.accessToken")

        return http.exchange(
                request.build(),
                Promotion.class)
                .body
    }

    private def getUncachedCoordinatesLandingPromotions(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder
                .fromUri(root.resolve(PROMOTION_LANDING_ENDPOINT))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)

        if (coordinatesInput.countryId)
            uri.queryParam("country_id", coordinatesInput.countryId)

        def request = RequestEntity.method(
                HttpMethod.GET,
                uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        return http.exchange(
                request.build(),
                Promotion.class)
                .body
    }

}
