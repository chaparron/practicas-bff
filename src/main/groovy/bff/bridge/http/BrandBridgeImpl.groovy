package bff.bridge.http

import bff.bridge.BrandBridge
import bff.model.Brand
import bff.model.GetHomeBrandsResult
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Component
class BrandBridgeImpl implements BrandBridge {

    URI root
    RestOperations http

    @Override
    GetHomeBrandsResult getHome(String accessToken, String countryId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/brand/home/"))
            .queryParam("country_id", countryId)

        def brands = http.exchange(
            RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<Brand>>() {}).body

        return new GetHomeBrandsResult(brands: brands)
    }

}
