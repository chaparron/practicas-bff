package bff.service

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
@Slf4j
class HttpBridge {

    @Autowired
    private RestTemplate http;

    def get(URI uri, String accessToken) {
        this.get(uri, accessToken, null)
    }

    def get(URI uri, String accessToken, Map<String, String> otherHeaders) {
        return get(uri, accessToken, otherHeaders, Map)
    }

    def <T> T get(URI uri, String accessToken, Map<String, String> otherHeaders, Class<T> responseType) {
        try {
            RequestEntity.BodyBuilder bb = RequestEntity.method(HttpMethod.GET, uri).contentType(MediaType.APPLICATION_JSON)
            otherHeaders?.each {
                bb.header(it.key, it.value)
            }
            if (accessToken) {
                http.exchange(
                        bb.header(HttpHeaders.AUTHORIZATION, "${accessToken}").build(), responseType).body
            } else {
                http.exchange(bb.build(), responseType).body
            }
        } catch (HttpClientErrorException e) {
            return new ResponseEntity(e.getResponseBodyAsString(), e.statusCode)
        }
    }

    def getToken() {
        OAuth2Authentication oAuth2Authentication = SecurityContextHolder.getContext().getAuthentication() as OAuth2Authentication
        OAuth2AuthenticationDetails a = oAuth2Authentication.getDetails() as OAuth2AuthenticationDetails
        return a.getTokenValue()
    }
}
