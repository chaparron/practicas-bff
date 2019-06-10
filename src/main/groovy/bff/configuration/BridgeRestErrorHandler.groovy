package bff.configuration

import graphql.ErrorType
import graphql.GraphQLError
import graphql.language.SourceLocation
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpResponse
import org.springframework.lang.Nullable
import org.springframework.util.FileCopyUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.UnknownHttpStatusCodeException

import java.nio.charset.Charset

class BridgeRestTemplateResponseErrorHandler implements ResponseErrorHandler {

    static <T> T responseMap(String s) {
        return new JsonSlurper().parseText(s)
    }

    @Override
    boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode())
        return (statusCode != null && hasError(statusCode))
    }

    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
            statusCode.series() == HttpStatus.Series.SERVER_ERROR)
    }


    @Override
    void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode())
        if (statusCode == null) {
            throw new BridgeUnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(),
                response.getHeaders(), getResponseBody(response), getCharset(response))
        }
        handleError(response, statusCode)
    }

    protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        def innerResponse = response.body.with { new JsonSlurper().parse(it) }

        switch (statusCode.series()) {
            case HttpStatus.Series.CLIENT_ERROR:

                if (statusCode == HttpStatus.UNAUTHORIZED || statusCode == HttpStatus.FORBIDDEN) {
                    throw new AccessToBackendDeniedException(response.getStatusText(), new BridgeHttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response)))


                } else if (statusCode == HttpStatus.BAD_REQUEST) {
                    BadRequestErrorException badRequestErrorException = new BadRequestErrorException(response.getStatusText(), new BridgeHttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response)))
                    badRequestErrorException.innerResponse = innerResponse?.message
                    if (!badRequestErrorException.innerResponse && innerResponse?.error instanceof List) {
                        badRequestErrorException.innerResponse = innerResponse?.error?.first()
                    }
                    throw badRequestErrorException

                } else if (statusCode == HttpStatus.CONFLICT) {
                    ConflictErrorException conflictErrorException = new ConflictErrorException(response.getStatusText(), new BridgeHttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response)))
                    conflictErrorException.innerResponse = innerResponse?.error
                    throw conflictErrorException
                } else if (statusCode == HttpStatus.NOT_FOUND) {
                    EntityNotFoundException entityNotFoundException = new EntityNotFoundException(response.getStatusText(), new BridgeHttpServerErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response)))
                    entityNotFoundException.innerResponse = innerResponse?.error
                    throw entityNotFoundException
                } else {
                    throw new BridgeHttpClientErrorException(statusCode, response.getStatusText(),
                        response.getHeaders(), getResponseBody(response), getCharset(response))
                }

            case HttpStatus.Series.SERVER_ERROR:
                throw new BackendServerErrorException(response.getStatusText(), new BridgeHttpServerErrorException(statusCode, response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response)))

            default:
                throw new BackendServerErrorException(response.getStatusText(), new BridgeUnknownHttpStatusCodeException(statusCode.value(), response.getStatusText(),
                    response.getHeaders(), getResponseBody(response), getCharset(response)))
        }
    }

    protected byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody())
        }
        catch (IOException ex) {
            // ignore
        }
        return new byte[0]
    }

    @Nullable
    protected Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders()
        MediaType contentType = headers.getContentType()
        return (contentType != null ? contentType.getCharset() : null)
    }

}

interface MappedResponse {
    def <T> T getResponseBody()
}

@InheritConstructors
class AccessToBackendDeniedException extends RuntimeException implements GraphQLError {
    @Override
    String getMessage() {
        return "INVALID_TOKEN"
    }

    @Override
    List<SourceLocation> getLocations() {
        return [new SourceLocation(0, 0, "accessToken")]
    }

    @Override
    ErrorType getErrorType() {
        return ErrorType.DataFetchingException
    }
}

@InheritConstructors
class BackendServerErrorException extends RuntimeException {
}

@InheritConstructors
class BadRequestErrorException extends RuntimeException {
    def innerResponse
}

@InheritConstructors
class NotSupplierFoundException extends RuntimeException {
    def innerResponse
}

@InheritConstructors
class EntityNotFoundException extends RuntimeException {
    def innerResponse
}

@InheritConstructors
class ConflictErrorException extends RuntimeException {
    def innerResponse
}

@InheritConstructors
class EmptyResultDataAccessErrorException extends RuntimeException {
}

@InheritConstructors
class BridgeHttpClientErrorException extends HttpClientErrorException implements MappedResponse {

    @Override
    <T> T getResponseBody() {
        return BridgeRestTemplateResponseErrorHandler.responseMap(this.getResponseBodyAsString())
    }
}

@InheritConstructors
class BridgeHttpServerErrorException extends HttpServerErrorException implements MappedResponse {

    @Override
    <T> T getResponseBody() {
        return BridgeRestTemplateResponseErrorHandler.responseMap(this.getResponseBodyAsString())
    }
}

@InheritConstructors
class BridgeUnknownHttpStatusCodeException extends UnknownHttpStatusCodeException implements MappedResponse {

    @Override
    <T> T getResponseBody() {
        return BridgeRestTemplateResponseErrorHandler.responseMap(this.getResponseBodyAsString())
    }
}