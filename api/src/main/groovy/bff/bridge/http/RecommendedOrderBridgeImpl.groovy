package bff.bridge.http

import bff.bridge.RecommendedOrderBridge
import bff.configuration.BadRequestErrorException
import bff.model.FrequentProductResult
import bff.model.GetFrequentProductsInput
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
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

    @Value('${recommended.order.url}')
    URI apiGatewayUrl

    @Override
    List<FrequentProductResult> getFrequentProducts(GetFrequentProductsInput getFrequentProductsInput) {
        URI uri = UriComponentsBuilder.fromUri(apiGatewayUrl.resolve("frequentproducts"))
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
