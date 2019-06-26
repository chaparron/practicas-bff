package bff.bridge.http

import bff.bridge.PromotionBridge
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
                .queryParam("enabled", true)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        request.header(HttpHeaders.AUTHORIZATION, "Bearer $promotionInput.accessToken")

        return http.exchange(request.build(), PromotionResponse).body
    }
}
