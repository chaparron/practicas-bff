package bff.bridge.http

import bff.bridge.MarketingBridge
import groovy.util.logging.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

@Slf4j
class MarketingBridgeImpl implements MarketingBridge{

    URI root
    RestOperations http

    @Override
    String getTrackingId(String accessToken, String username) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("./tracking-id"))
                .toUriString().toURI()

        http.exchange(
                RequestEntity.method(HttpMethod.POST, uri)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(username)
                , String).body.replace("\"","")

    }
}
