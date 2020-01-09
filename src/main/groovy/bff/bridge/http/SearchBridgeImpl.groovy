package bff.bridge.http

import bff.bridge.SearchBridge
import bff.model.*
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
            .queryParam("supplier", searchInput.supplier)
            .queryParam("tag", searchInput.tag)

        searchInput.features?.each {
            uri.queryParam("feature_${it.id}", it.value)
        }

        def search = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .header(HttpHeaders.AUTHORIZATION, "Bearer $searchInput.accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .body(searchInput)
            , SearchResultMapper).body


        def result = new SearchResult(
            products: search.products,
            breadcrumb: search.breadcrumb,
            sort: search.sort,
            header: search.header,
            facets: search.facets
        )

        result.products.forEach {
            it.accessToken = searchInput.accessToken
            it.priceFrom?.accessToken = searchInput.accessToken
            it.minUnitsPrice?.accessToken = searchInput.accessToken
            if (it.highlightedPrice) {
                it.highlightedPrice?.accessToken = searchInput.accessToken
            }

            it.prices.forEach { pr ->
                pr.accessToken = searchInput.accessToken
            }
        }
        result.filters = search.filters.collect {
            if (it.value instanceof List) {
              return new Filter(key: it.key, values: it.value.collect { fi -> new FilterItem(id: fi.id, name: fi.name ) })
            }
            if (it.value instanceof Map ) {
                return new Filter(key: it.key, values: [new FilterItem(id: it.value.id, name: it.value.name )])
            }
            return new Filter(key: it.key, value: it.value)
        }

        result

    }
}
