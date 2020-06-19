package bff.bridge.http

import bff.bridge.PromotionBridge
import bff.model.CoordinatesInput
import bff.model.GetLandingPromotionInput
import bff.model.Promotion
import bff.model.PromotionInput
import bff.model.PromotionResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

class PromotionBridgeImpl implements PromotionBridge {

    URI root
    RestOperations http


    @Override
    PromotionResponse getAll(PromotionInput promotionInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/promotion/"))
                .queryParam("country_id", promotionInput.country_id)
                .queryParam("enable", true)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        request.header(HttpHeaders.AUTHORIZATION, "Bearer $promotionInput.accessToken")

        return http.exchange(request.build(), PromotionResponse).body
    }

    @Override
    PromotionResponse previewPromotions(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/promotion/"))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)
                .queryParam("enable", true)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        return http.exchange(request.build(), PromotionResponse).body
    }

    @Override
    Promotion getLandingPromotion(GetLandingPromotionInput promotionInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/promotion/landing"))
                .queryParam("country_id", promotionInput.country_id)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        request.header(HttpHeaders.AUTHORIZATION, "Bearer $promotionInput.accessToken")

        http.exchange(request.build(), Promotion).body
    }

    @Override
    Promotion previewLandingPromotion(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/promotion/landing"))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)


        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        http.exchange(request.build(), Promotion).body
    }
}
