package bff.bridge.http

import bff.bridge.ProductBridge
import bff.model.Brand
import bff.model.Category
import bff.model.Manufacturer
import bff.model.Supplier
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder


class ProductBridgeImpl implements ProductBridge {

    URI root
    RestOperations http

    @Override
    Category getCategoryByProductId(String accessToken, Integer productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/category/${productId}"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Category).body
    }

    @Override
    Manufacturer getManufacturerByProductId(String accessToken, Integer productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/manufacturer/${productId}"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , Manufacturer).body
    }

    @Override
    Brand getBrandByProductId(String accessToken, Integer productId) {
        return null
    }
}
