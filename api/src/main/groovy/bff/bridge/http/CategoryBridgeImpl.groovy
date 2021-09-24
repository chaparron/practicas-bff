package bff.bridge.http

import bff.JwtToken
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
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<Category>> categoryCache

    @PostConstruct
    void init() {
        categoryCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.categories, TimeUnit.MINUTES)
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
        String countryId = JwtToken.countryFromString(accessToken)
        return categoryCache.get(countryId)
    }

    @Override
    RootCategoriesResult previewRootCategories(CoordinatesInput coordinatesInput) {
        List<Category> response
        if(coordinatesInput.countryId) {
            response = categoryCache.get(coordinatesInput.countryId)
        } else {
            def uri = UriComponentsBuilder.fromUri(root.resolve("/category/roots"))
                    .queryParam("lat", coordinatesInput.lat)
                    .queryParam("lng", coordinatesInput.lng).toUriString().toURI()

            response = http.<List<Category>> exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .build()
                    , new ParameterizedTypeReference<List<Category>>() {}).body
        }

        new RootCategoriesResult(
                categories: response
        )
    }

    private def getUnCachedCategories(String countryId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/category/roots"))
                .queryParam("countryId", countryId).toUriString().toURI()

        http.<List<Category>> exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .build()
                , new ParameterizedTypeReference<List<Category>>() {}).body
    }
}
