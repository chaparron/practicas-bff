package bff.bridge.http

import bff.bridge.PhoneNotifierBridge
import bff.model.Category
import bff.model.Challenge
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Slf4j
class PhoneNotifierBridgeImpl implements PhoneNotifierBridge {

    URI root
    RestOperations http

    @Override
    Boolean isValidPhone(String countryCode, String phone) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("./phone/verify"))
                .toUriString().toURI()

        try {
            def statusCode =  http.exchange(
                    RequestEntity.method(HttpMethod.POST, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(["phoneNumberTo": countryCode.concat(phone)])
                    , Void).statusCode

            return statusCode == HttpStatus.OK
        }catch(Exception ex) {
            log.warn "Error parsing response", ex
            return false
        }




    }
}
