package bff.bridge.http

import bff.bridge.RecommendedOrderBridge
import bff.model.FavoriteProductInput
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class RecommendedOrderBridgeImpl implements RecommendedOrderBridge{

    RestOperations http
    URI root

    @Value('${recommended.order.url}')
    URI apiGatewayUrl


    @Override
    Boolean markProductAsFavorite(FavoriteProductInput favoriteProductInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("favoriteproducts/${favoriteProductInput.productId}")).toUriString().toURI()

        try{
            http.exchange(
                RequestEntity.method(HttpMethod.PUT, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer ${favoriteProductInput.accessToken}")
                        .build()
                , Boolean).body

        }catch(Exception e) {
            Boolean.FALSE
        }
    }

    @Override
    Boolean unmarkFavoriteProduct(FavoriteProductInput favoriteProductInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("favoriteproducts/${favoriteProductInput.productId}")).toUriString().toURI()

        try{
            http.exchange(
                    RequestEntity.method(HttpMethod.DELETE, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${favoriteProductInput.accessToken}")
                            .build()
                    , Boolean).body

        }catch(Exception e) {
            Boolean.FALSE
        }
    }

    private static Boolean favoriteProductMock(FavoriteProductInput favoriteProductInput) {
        favoriteProductInput.productId
    }

}