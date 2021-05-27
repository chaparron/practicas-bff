package bff.bridge.http

import bff.bridge.CategoryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.Category
import bff.model.CoordinatesInput
import bff.model.RootCategoriesResult
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

@Component
@Slf4j
class CategoryBridgeImpl implements CategoryBridge {

    URI root
    RestOperations http

    @Autowired
    CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<Category>> categoryCache

    @PostConstruct
    void init() {
        categoryCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.categories, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<Category>>() {
                            @Override
                            List<Category> load(String key) throws Exception {
                                getUnCachedCategories(key)
                            }
                        }
                )
    }

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

    @Override
    RootCategoriesResult previewRootCategories(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/category/roots"))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)
                .queryParam("countryId", coordinatesInput.countryId)

        new RootCategoriesResult(
                categories: categoryCache.get(uri.toUriString())
        )
    }

    private def getUnCachedCategories(String uri) {
        http.<List<Category>> exchange(
                RequestEntity.method(HttpMethod.GET, uri.toURI())
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , new ParameterizedTypeReference<List<Category>>() {}).body
    }
}
