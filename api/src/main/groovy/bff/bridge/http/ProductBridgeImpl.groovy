package bff.bridge.http

import bff.bridge.ProductBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.EntityNotFoundException
import bff.model.*
import groovy.util.logging.Log4j
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder


@Slf4j
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
    Product getProductByEan(String accessToken, String ean) throws BadRequestErrorException, EntityNotFoundException {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/ean/${ean}"))
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
    List<Price> getPricesByProductId(String accessToken, Long productId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/${productId}/prices"))
                .toUriString().toURI()

        def prices = http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , new ParameterizedTypeReference<List<Price>>() {}).body

        prices.collect {
            it.accessToken = accessToken
            it
        }
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
    Cart refreshCart(String accessToken, List<Integer> products) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/product/cart"))
                .toUriString().toURI()
        if (products.size() == 0) {
            throw new BadRequestErrorException(innerResponse: CartFailedReason.EMPTY_PRODUCTS.name())
        }
        def cart = http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([products_ids: products])
                , Cart).body

        cart.availableProducts = cart.products
        cart.availableProducts.each {
            it.product.accessToken = accessToken
            it.supplierPrices = it.suppliers
        }
        cart.suppliers.each {
            it.accessToken = accessToken
        }
        cart
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
