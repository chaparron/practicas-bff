package bff.bridge.http

import bff.bridge.CategoryBridge
import bff.model.Address
import bff.model.Category
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Component
@Slf4j
class CategoryBridgeImpl implements CategoryBridge {

    URI root
    RestOperations http

    @Override
    List<Category> findRootCategories(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/category/roots"))

        http.<List<Category>> exchange(
            RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Category>>() {}).body
    }
}
