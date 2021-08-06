package bff.bridge.http

import bff.bridge.SupplierHomeBridge
import bff.configuration.CacheConfigurationProperties
import bff.model.CoordinatesInput
import bff.model.PreviewHomeSupplierResponse
import bff.model.PreviewSupplier
import com.github.benmanes.caffeine.cache.CacheLoader
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct
import java.util.concurrent.TimeUnit

class SupplierHomeBridgeImpl implements SupplierHomeBridge {

    private final static String SUPPLIER_HOME_ENDPOINT = "/supplier/home"

    URI root

    RestOperations http

    @Autowired
    private CacheConfigurationProperties cacheConfiguration

    private LoadingCache<String, List<PreviewSupplier>> supplierCache

    @PostConstruct
    void init() {
        supplierCache = Caffeine.newBuilder()
                .expireAfterWrite(cacheConfiguration.suppliers, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<PreviewSupplier>>() {
                            @Override
                            List<PreviewSupplier> load(@NotNull String key) throws Exception {
                                return getUncachedHomeSupplier(new CoordinatesInput(countryId: key))
                            }
                        }
                )
    }

    @Override
    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput coordinatesInput) {
        def suppliers = []
        if (coordinatesInput.countryId) {
            suppliers = supplierCache.getIfPresent(coordinatesInput.getCountryId())
        }
        if (!suppliers) {
            suppliers = getUncachedHomeSupplier(coordinatesInput)
            if (coordinatesInput.countryId)
                supplierCache.put(coordinatesInput.countryId, suppliers)
        }
        return new PreviewHomeSupplierResponse(suppliers: suppliers)
    }

    private def getUncachedHomeSupplier(CoordinatesInput coordinatesInput) {

        def uri = UriComponentsBuilder.fromUri(root.resolve(SUPPLIER_HOME_ENDPOINT))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)
                .queryParam("countryId", coordinatesInput.countryId)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        return http.<List<PreviewSupplier>> exchange(
                request.build(),
                new ParameterizedTypeReference<List<PreviewSupplier>>() {})
                .body

    }
}
