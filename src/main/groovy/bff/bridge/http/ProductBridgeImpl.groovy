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
        def uri = UriComponentsBuilder.fromUri(root.resolve("/customer/me/product/${productId}/supplier"))
            .toUriString().toURI()

        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Supplier).body

        r.accessToken = accessToken
        r
    }

    @Override
    Manufacturer getManufacturerByProductId(String accessToken, Integer productId) {
        return null
    }

    @Override
    Brand getBrandByProductId(String accessToken, Integer productId) {
        return null
    }
}
