package bff.bridge.http


import bff.bridge.SearchBridge
import bff.configuration.BadRequestErrorException
import bff.model.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static java.util.Optional.ofNullable

class SearchBridgeImpl implements SearchBridge {

    URI root
    RestOperations http

    @Override
    SearchResult search(SearchInput input) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product"))
                .queryParam("address_id", input.addressId)
                .queryParam("keyword", input.keyword)
                .queryParam("sort", input.sort)
                .queryParam("sort_direction", input.sortDirection?.name())
                .queryParam("category", input.category)
                .queryParam("page", input.page)
                .queryParam("size", input.size)
                .queryParam("brand", input.brand)
                .queryParam("supplier", input.supplier)
                .queryParam("tag", input.tag)

        input.features?.each {
            uri.queryParam("feature_${it.id}", it.value)
        }

        def search = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $input.accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(input)
                , SearchResultMapper).body

        def result = new SearchResult(
                products: search.products,
                breadcrumb: search.breadcrumb,
                sort: search.sort,
                header: search.header,
                facets: search.facets,
                filters: transformFilters(search.filters)
        )

        result.products.forEach {
            it.accessToken = input.accessToken
            it.priceFrom?.accessToken = input.accessToken
            it.minUnitsPrice?.accessToken = input.accessToken
            it.highlightedPrice?.accessToken = input.accessToken

            it.prices?.forEach { pr ->
                pr.accessToken = input.accessToken
            }
            it.favorite = ofNullable(it.favorite).orElse(false)
        }
        result

    }

    @Override
    SearchResponse searchV2(SearchInput input) {
        try {
            return search(input)
        } catch (BadRequestErrorException ex) {
            return SearchFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    @Override
    SearchResponse previewSearch(PreviewSearchInput input) {
        try {
            def uri = UriComponentsBuilder.fromUri(root.resolve("/product"))
                    .queryParam("keyword", input.keyword)
                    .queryParam("sort", input.sort)
                    .queryParam("sort_direction", input.sortDirection?.name())
                    .queryParam("category", input.category)
                    .queryParam("page", input.page)
                    .queryParam("size", input.size)
                    .queryParam("brand", input.brand)
                    .queryParam("tag", input.tag)
                    .queryParam("lat", input.lat)
                    .queryParam("lng", input.lng)
                    .queryParam("countryId", input.countryId)

            input.features?.each {
                uri.queryParam("feature_${it.id}", it.value)
            }

            def response = http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(input)
                    , PreviewSearchResultMapper).body

            def result = new PreviewSearchResult(
                    products: response.products,
                    breadcrumb: response.breadcrumb,
                    sort: response.sort,
                    header: response.header,
                    facets: response.facets
            )

            result.products.forEach {
                it.totalNumberOfSuppliers = it.suppliers.size()
            }
            result.filters = transformFilters(result.filters)
            result
        } catch (BadRequestErrorException ex) {
            return SearchFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    private static transformFilters(def filters) {
        filters.collect {
            if (it.value instanceof List) {
                return new Filter(key: it.key, values: it.value.collect { fi -> new FilterItem(id: fi.id, name: fi.name) })
            }
            if (it.value instanceof Map) {
                return new Filter(key: it.key, values: [new FilterItem(id: it.value.id, name: it.value.name)])
            }
            return new Filter(key: it.key, value: it.value)
        }
    }
}
