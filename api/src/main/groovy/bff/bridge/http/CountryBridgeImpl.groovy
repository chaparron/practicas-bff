package bff.bridge.http

import bff.bridge.CountryBridge
import bff.model.CountryConfigurationEntry
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.http.HttpHeaders.AUTHORIZATION

class CountryBridgeImpl implements CountryBridge {

    URI root
    RestOperations http

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/country/config/${countryId}"))
            .toUriString().toURI()


        http.exchange(
            RequestEntity.method(HttpMethod.GET, uri)
                .contentType(MediaType.APPLICATION_JSON)
                .build()
            , Map).body?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }

    }

    @Override
    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/country/config"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer $accessToken")
                        .build()
                , Map).body?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }
}
