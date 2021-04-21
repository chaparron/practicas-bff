package bff.bridge.http

import bff.bridge.SupplierBridge
import bff.model.CoordinatesInput
import bff.model.PreviewHomeSupplierResponse
import bff.model.PreviewSupplier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

class SupplierBridgeImpl implements SupplierBridge {

    URI root
    RestOperations http

    @Override
    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput coordinatesInput) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/supplier/home"))
                .queryParam("lat", coordinatesInput.lat)
                .queryParam("lng", coordinatesInput.lng)
                .queryParam("countryId", coordinatesInput.countryId)

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        def suppliers = http.exchange(
                request
                        .build()
                , new ParameterizedTypeReference<List<PreviewSupplier>>() {}).body


        return new PreviewHomeSupplierResponse(suppliers: suppliers)
    }
}
