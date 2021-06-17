package bff.bridge.http

import bff.bridge.RecommendedOrderBridge
import bff.configuration.BadRequestErrorException
import bff.model.FrequentProductResult
import bff.model.GetFrequentProductsInput
import groovy.util.logging.Slf4j
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

import static org.springframework.http.HttpHeaders.AUTHORIZATION

@Slf4j
class RecommendedOrderBridgeImpl implements RecommendedOrderBridge{

    RestOperations http

    URI apiGatewayUrl = new URI("https://tjg2esr15c.execute-api.eu-central-1.amazonaws.com/qa/")

    @Override
    List<FrequentProductResult> getFrequentProducts(GetFrequentProductsInput getFrequentProductsInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("external/recommendedorder/frequentproducts/${getFrequentProductsInput.customerId}"))
        .toUriString().toURI()

        try {
            def responseType = new ParameterizedTypeReference<List<FrequentProductResult>>() {}
            http.exchange(
                    RequestEntity.method(HttpMethod.GET, uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header(AUTHORIZATION, "Bearer ${getFrequentProductsInput.accessToken}")
                            .build()
                    , responseType).body

        } catch (BadRequestErrorException badRequestException) {
            throw new UnsupportedOperationException("Get Frequent Products  - Backend Error", badRequestException)
        }
    }
}
