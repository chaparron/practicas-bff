package bff.bridge.http

import bff.bridge.ValidationsBridge
import bff.model.*
import com.sun.org.apache.xpath.internal.operations.Bool
import groovy.util.logging.Slf4j
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Slf4j
class ValidationsBridgeImpl implements ValidationsBridge {

    URI root
    RestOperations http

    @Override
    boolean validateUsername(ValidateUsernameInput input) {

        def uri = UriComponentsBuilder.fromUri(root.resolve("/validate/userUsername"))
                .queryParam("id", input.id)
                .queryParam("value", input.username)
                .toUriString().toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(input)
                , Boolean).body
    }

    @Override
    boolean validate(ValidateInput input) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/validate/${input.validationType.name}"))
                .queryParam("id", input.id)
                .queryParam("value", input.value)
                .queryParam("country_id", input.country_id)
                .toUriString().toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.GET, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(input)
                , Boolean).body
    }
}