package bff.bridge.http

import bff.bridge.CategoryBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.Category
import bff.model.CoordinatesInput
import bff.model.RootCategoriesResult
import bff.service.HttpBridge
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

@Component
@Slf4j
class CategoryBridgeImpl implements CategoryBridge {

    URI root

    @Autowired
    CacheConfigurationProperties cacheConfiguration

    @Autowired
    HttpBridge httpBridge

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
        httpBridge.getList(
                UriComponentsBuilder.fromUri(root.resolve("/category/roots")).toUriString().toURI(),
                "Bearer $accessToken",
                null,
                Category)
    }

    @Override
    RootCategoriesResult previewRootCategories(CoordinatesInput coordinatesInput) {
        List<Category> response
        if(coordinatesInput.countryId) {
            response = categoryCache.get(coordinatesInput.countryId)
        } else {
            response = httpBridge.getList(
                    UriComponentsBuilder.fromUri(root.resolve("/category/roots"))
                            .queryParam("lat", coordinatesInput.lat)
                            .queryParam("lng", coordinatesInput.lng).toUriString().toURI(),
                    null,
                    null,
                    Category)
        }

        new RootCategoriesResult(
                categories: response
        )
    }

    private def getUnCachedCategories(String countryId) {
        httpBridge.getList(
                UriComponentsBuilder.fromUri(root.resolve("/category/roots"))
                        .queryParam("countryId", countryId).toUriString().toURI(),
                null,
                null,
                Category)
    }
}
