package bff.bridge.http

import bff.bridge.StateBridge
import bff.model.OrderItem
import bff.model.State
import bff.model.Supplier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

class StateBridgeImpl implements StateBridge
{

    URI root
    RestOperations http

    @Override
    List<State> getByCountryId(String countryId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/state/country/${countryId}"))
            .toUriString().toURI()

        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , new ParameterizedTypeReference<List<State>>() {}).body
    }
}
