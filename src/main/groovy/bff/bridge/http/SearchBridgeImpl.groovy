package bff.bridge.http

import bff.bridge.SearchBridge
import bff.model.SearchInput
import bff.model.SearchResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

class SearchBridgeImpl implements SearchBridge {

    URI root
    RestOperations http

    @Override
    SearchResult search(SearchInput searchInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product"))
            .queryParam("address_id", searchInput.addressId)
            .queryParam("keyword", searchInput.keyword)
            .queryParam("sort", searchInput.sort)
            .queryParam("sort_direction", searchInput.sortDirection?.name())
            .queryParam("category", searchInput.category)
            .queryParam("page", searchInput.page)
            .queryParam("size", searchInput.size)
            .queryParam("brand", searchInput.brand)
            .toUriString().toURI()

        def search = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $searchInput.accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(searchInput)
            , SearchResult).body

        search.products.forEach {
            it.accessToken = searchInput.accessToken
            it.priceFrom.accessToken = searchInput.accessToken
            it.minUnitsPrice.accessToken = searchInput.accessToken
            it.prices.forEach { pr ->
                pr.accessToken = searchInput.accessToken
            }
        }
        search
    }
}
