package bff.model

import bff.InvalidToken
import bff.configuration.AccessToBackendDeniedException
import bff.configuration.BackendServerErrorException
import com.fasterxml.jackson.annotation.JsonIgnore
import com.newrelic.api.agent.NewRelic
import graphql.ErrorType
import graphql.ExceptionWhileDataFetching
import graphql.GraphQLError
import graphql.language.SourceLocation
import graphql.servlet.GraphQLErrorHandler
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException
import org.springframework.stereotype.Component

import java.util.concurrent.CompletionException

@Component
@Slf4j
class DefaultGraphQLErrorHandler implements GraphQLErrorHandler {

    @Value('${errors.expose:true}')
    boolean exposeAllErrors

    @Override
    List<GraphQLError> processErrors(List<GraphQLError> errors) {
        errors.collectMany { unwrapGraphQLError(it) }
    }


    private List<GraphQLError> unwrapGraphQLError(GraphQLError error) {
        def exceptionMessage = error.getMessage() ?: "Unknown Error"
        String errorMsg = sprintf("%s caused by: %s", error.errorType.toString(), exceptionMessage)
        NewRelic.noticeError(errorMsg)
        [error]
    }

    private List<GraphQLError> unwrapGraphQLError(ExceptionWhileDataFetching error) {
        unwrap(error.exception, error)
    }

    /**
     * Concurrent exceptions, when dealing with async resolvers for example,
     * are handled using their nested cause.
     */
    private List<GraphQLError> unwrap(CompletionException cause, ExceptionWhileDataFetching error) {
        unwrap(cause.getCause(), error)
    }

    /**
     * When silent exception is thrown, the call to NewRelic is not made.
     * This error must be always be thrown.
     */
    private List<GraphQLError> unwrap(SilentException cause, ExceptionWhileDataFetching error) {
        [GenericError.exposeGenericError(cause, error)]
    }


    private List<GraphQLError> unwrap(Throwable cause, ExceptionWhileDataFetching error) {
        NewRelic.noticeError(cause)
        if (exposeAllErrors) [GenericError.exposeGenericError(cause, error)]
        else filterUnhandledException(cause, error)
    }

    private List<GraphQLError> unwrap(InvalidBodyException cause, ExceptionWhileDataFetching error) {
        NewRelic.noticeError(cause)
        if (exposeAllErrors) [GenericError.exposeInvalidBodyException(cause, error)]
        else filterUnhandledException(cause, error)
    }

    private List<GraphQLError> unwrap(OAuth2AccessDeniedException cause, ExceptionWhileDataFetching error) {
        [GenericError.exposeGenericError(cause, error)]
    }

    private List<GraphQLError> unwrap(Unauthorized cause, ExceptionWhileDataFetching error) {
        log.debug('unauthorized request caused by', error.exception)
        [new GenericError(
                path: error.path,
                extensions: [
                        entity      : 'Credentials',
                        property    : cause.error,
                        message     : cause.description
                ]
        )]
    }

    private List<GraphQLError> unwrap(AccessToBackendDeniedException cause, ExceptionWhileDataFetching error) {
        [new GenericError(
            path: error.path,
            extensions: [
                    entity      : 'Credentials',
                    property    : 'invalid_token',
            ]
        )]
    }


    private List<GraphQLError> unwrap(BackendServerErrorException cause, ExceptionWhileDataFetching error) {
        NewRelic.noticeError(cause)
        log.debug('unauthorized request caused by', error.exception)
        [new GenericError(message: "Backend server error", path: error.path)]
    }

    private List<GraphQLError> unwrap(InvalidToken cause, ExceptionWhileDataFetching error) {
        [GenericError.exposeGenericError(cause, error)]
    }


    private static def filterUnhandledException(Throwable cause, ExceptionWhileDataFetching error) {
        log.error("filtering unhandled exception", cause)
        [new GenericError(message: "Internal server error", path: error.path)]
    }


}

@EqualsAndHashCode
@ToString
class GenericError implements GraphQLError {

    String message = 'Error executing query'
    List<Object> path
    @JsonIgnore
    List<SourceLocation> locations
    ErrorType errorType = ErrorType.DataFetchingException
    Map<String, Object> extensions

    static GenericError exposeGenericError(Throwable cause, ExceptionWhileDataFetching error) {
        new GenericError(
                path: error.path,
                extensions: [
                        message: cause.message
                ]
        )
    }

    static GraphQLError exposeInvalidBodyException(InvalidBodyException cause, ExceptionWhileDataFetching error) {
        new GenericError(
            path: error.path,
            errorType: ErrorType.ValidationError,
            extensions: [
                message: cause.error.first()?.message,
                field:cause.error.first()?.field,
                rejectedValue: cause.error.first()?.rejectedValue
            ]
        )
    }
}