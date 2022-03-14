package bff.bridge.http

import bff.bridge.ValidationsBridge
import bff.model.PhoneInput
import bff.model.PhoneStatus
import bff.model.PhoneStatusResult
import bff.model.PreSignUpInput
import bff.model.PreSignUpResponse
import bff.model.TooManyRequestException
import bff.model.ValidateInput
import bff.model.ValidateUsernameInput
import groovy.util.logging.Slf4j
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class ValidationsBridgeImpl implements ValidationsBridge {

    URI root
    RestOperations http

    @Value('${jwt.private.key:NZYExzz5lB5gmlgMWhP1z2JXEw7lg3j73cZ}')
    private String jwtSecret

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

    @Override
    PhoneStatusResult getPhoneStatus(PhoneInput input, String remoteIp) {
        def url = UriComponentsBuilder.fromUri(root.resolve("/validate/phone"))
                .queryParam("country_calling_code", input.countryCode)
                .queryParam("phone", input.phone).toUriString()

        def uri = url.toURI()

        String jwtToken = Jwts.builder()
                .setSubject("wabi2b-bff")
                .claim("authorities", Collections.singletonList("VALIDATE_PHONE"))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact()
        try {
            return http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer $jwtToken")
                            .header("Address-Received-In-Bff", remoteIp)
                            .body(input)
                    , PhoneStatus).body
        } catch (TooManyRequestException tooManyRequest) {
            return tooManyRequest.build()
        }
    }
}