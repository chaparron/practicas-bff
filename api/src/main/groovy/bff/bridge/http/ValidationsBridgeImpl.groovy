package bff.bridge.http

import bff.bridge.ValidationsBridge
import bff.model.PreSignUpInput
import bff.model.PreSignUpResponse
import bff.model.ValidateInput
import bff.model.ValidateUsernameInput
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

    @Override
    PreSignUpResponse validatePreSignUp(PreSignUpInput input) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/validate/preSignUp")).toUriString().toURI()
        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body([
                                countryCode    : input.countryCode,
                                phone          : input.phone,
                                captchaResponse: input.recaptchaResponse,
                                email          : input.email
                        ])
                , PreSignUpResponse).body
    }
}