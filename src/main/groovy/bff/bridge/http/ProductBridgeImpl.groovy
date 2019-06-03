package bff.bridge.http

import bff.bridge.ProductBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.Brand
import bff.model.Category
import bff.model.Feature
import bff.model.Image
import bff.model.Keyword
import bff.model.Manufacturer
import bff.model.Price
import bff.model.Prices
import bff.model.Product
import bff.model.ProductSearch
import bff.model.Supplier
import org.springframework.core.ParameterizedTypeReference
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
    Category getCategoryByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/category"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Category).body
    }

    @Override
    Product getProductById(String accessToken, Long productId) throws BadRequestErrorException, EntityNotFoundException {

        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}"))
            .toUriString().toURI()
        def r = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Product).body

        r.accessToken = accessToken
        r
    }

    @Override
    List<Feature> getFeaturesByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/features"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Feature>>() {}).body
    }

    @Override
    List<Image> getImagesByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/images"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Image>>() {}).body
    }

    @Override
    Prices getPricesByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/prices"))
            .toUriString().toURI()

        def result = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , List).body

        List<Price> prices =  result.collect {
            new Price(
                accessToken: accessToken,
                supplierId: it.supplier.id,
                value: it.value,
                enabled: it.enabled,
                minUnits: it.minUnits,
                maxUnits: it.maxUnits,
                updated: it.updated
            )
        }

        new Prices(prices: prices)

    }

    @Override
    Price getPriceFromByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/priceFrom"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Price).body
    }

    @Override
    Price getMinUnitsPriceByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/minUnitsPrice"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Price).body
    }

    @Override
    List<Keyword> getKeywordsByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/keywords"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Keyword>>() {}).body
    }

    @Override
    Supplier getSupplierById(String accessToken, Long supplierId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/supplier/${supplierId}"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Supplier).body
    }

    @Override
    Manufacturer getManufacturerByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/manufacturer"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Manufacturer).body
    }

    @Override
    Brand getBrandByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/brand"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Brand).body
    }

}
