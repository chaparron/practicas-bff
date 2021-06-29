package bff.bridge.http

import bff.bridge.RecommendedOrderBridge
import bff.configuration.BadRequestErrorException
import bff.configuration.CacheConfigurationProperties
import bff.model.FavoriteProductResult
import bff.model.FrequentProductResult
import bff.model.GetFavoriteProductsInput
import bff.model.GetFrequentProductsInput
import bff.model.ProductToMarkAsFavoriteInput
import bff.model.ProductToUnmarkAsFavoriteInput
import bff.model.Void
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class RecommendedOrderBridgeImpl implements RecommendedOrderBridge{

    RestOperations http

    @Value('${recommended.order.url}')
    URI apiGatewayUrl

    @Autowired
    CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<FavoriteProductResult>> favoritesCache

    @PostConstruct
    void init(){
        favoritesCache = Caffeine.newBuilder()
        .expireAfterWrite(cacheConfiguration.favorites, TimeUnit.HOURS)
        .build(
                new CacheLoader<String, List<FavoriteProductResult>>() {

                    @Override
                    List<FavoriteProductResult> load(String key) throws Exception {
                        getUncachedFavoriteProducts(key)
                    }
                }
        )
    }

    @Override
    List<FavoriteProductResult> getFavoriteProducts(GetFavoriteProductsInput getFavoriteProductsInput) {
        favoritesCache.get(getFavoriteProductsInput.accessToken)
    }


    private List<FavoriteProductResult> getUncachedFavoriteProducts(String accessToken) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("favoriteproducts"))
                .toUriString().toURI()

        try {
            def responseType = new ParameterizedTypeReference<List<FavoriteProductResult>>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${accessToken}")
                            .build()
                    , responseType).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Get Favorite Products  - Backend Error", badRequestException)
        }
    }

    @Override
    Void markProductAsFavorite(ProductToMarkAsFavoriteInput productToMarkAsFavoriteInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("favoriteproducts"))
                .toUriString().toURI()

        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.PUT, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${productToMarkAsFavoriteInput.accessToken}")
                    .body(productToMarkAsFavoriteInput.getProduct())
                    , Map).body
            favoritesCache.invalidate(productToMarkAsFavoriteInput.accessToken)
            Void.SUCCESS

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Add Favorite Product  - Backend Error", badRequestException)
        }
    }

    @Override
    Void unmarkProductAsFavorite(ProductToUnmarkAsFavoriteInput productToUnmarkAsFavoriteInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("favoriteproducts/${productToUnmarkAsFavoriteInput.productId}"))
                .toUriString().toURI()

        try {
            http.exchange(
                    RequestEntity.method(HttpMethod.DELETE, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${productToUnmarkAsFavoriteInput.accessToken}")
                            .build()
                    , Map).body
            favoritesCache.invalidate(productToUnmarkAsFavoriteInput.accessToken)
            Void.SUCCESS

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Unmark Favorite Product  - Backend Error", badRequestException)
        }
    }

    @Override
    List<FrequentProductResult> getFrequentProducts(GetFrequentProductsInput getFrequentProductsInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("frequentproducts"))
                .toUriString().toURI()

        try {
            def responseType = new ParameterizedTypeReference<List<FrequentProductResult>>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${getFrequentProductsInput.accessToken}")
                            .build()
                    , responseType).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Get Frequent Products  - Backend Error", badRequestException)
        }
    }

}
