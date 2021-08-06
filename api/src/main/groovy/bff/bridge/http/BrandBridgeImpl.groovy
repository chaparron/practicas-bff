package bff.bridge.http

import bff.bridge.BrandBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.Brand
import bff.model.CoordinatesInput
import bff.model.GetHomeBrandsResult
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class BrandBridgeImpl implements BrandBridge {

    private static final String BRAND_ENDPOINT = "/brand/home/"

    URI root

    RestOperations http

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<Brand>> brandCache

    @PostConstruct()
    void init() {
        brandCache = Caffeine
                .newBuilder()
                .expireAfterWrite(cacheConfiguration.brands, TimeUnit.HOURS)
                .build(new CacheLoader<String, List<Brand>>() {
                    @Override
                    List<Brand> load(@NotNull String key) throws Exception {
                        getUnCachedBrands(new CoordinatesInput(countryId: key))
                    }
                })
    }

    @Override
    GetHomeBrandsResult getHome(String accessToken, String countryId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve(BRAND_ENDPOINT))
                .queryParam("country_id", countryId)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        if (accessToken) {
            request.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }

        def brands = http.exchange(
                request.build(),
                new ParameterizedTypeReference<List<Brand>>() {})
                .body

        return new GetHomeBrandsResult(brands: brands)
    }

    @Override
    GetHomeBrandsResult previewHomeBrands(CoordinatesInput coordinatesInput) {
        def brands = coordinatesInput.countryId
                ? brandCache.get(coordinatesInput.countryId)
                : getUnCachedBrands(coordinatesInput)

        return new GetHomeBrandsResult(brands: brands)
    }

    private def getUnCachedBrands(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve(BRAND_ENDPOINT))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)
                .queryParam("country_id", coordinatesInput.countryId)

        def request = RequestEntity
                .method(
                        HttpMethod.GET,
                        uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        return http.exchange(
                request.build(),
                new ParameterizedTypeReference<List<Brand>>() {})
                .body

    }
}
