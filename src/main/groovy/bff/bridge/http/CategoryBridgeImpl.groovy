package bff.bridge.http

import bff.bridge.CategoryBridge
import bff.model.Category
import bff.model.FindRootsInput
import groovy.util.logging.Slf4j
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
    List<Category> findRootCategories(FindRootsInput searchInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/category/roots"))

        http.<List<Category>> exchange (
            RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .header(HttpHeaders.AUTHORIZATION, "Bearer $searchInput.accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(searchInput)
            , List).body?.collect {
            new Category(
                id: it.id,
                isLeaf: it.isLeaf,
                name: it.name,
                enabled: it.enabled,
                parentId: it.parentId
            )
        }
    }
}
