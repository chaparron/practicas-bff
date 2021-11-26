package bff.configuration

import brave.Tracer
import com.newrelic.api.agent.NewRelic
import graphql.ExecutionResult
import graphql.ExecutionResultImpl
import graphql.execution.instrumentation.DeferredFieldInstrumentationContext
import graphql.execution.instrumentation.Instrumentation
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.parameters.*
import graphql.execution.instrumentation.tracing.TracingInstrumentation
import graphql.execution.instrumentation.tracing.TracingSupport
import graphql.language.Document
import graphql.language.IntValue
import graphql.language.OperationDefinition
import graphql.language.StringValue
import graphql.schema.*
import graphql.validation.ValidationError
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sun.util.locale.LanguageTag

import java.util.concurrent.CompletableFuture

import static java.util.Optional.of

@Configuration
@Slf4j
class GraphqlConfiguration {

    @Bean
    Instrumentation tracingInstrumentation(Tracer tracer) {
        new TracingInstrumentation() {

            private static final String STEP_PARAM_NR = "step"
            private static final Map<String, List<String>> HIDDEN_ATTR_KEYS_BY_TRANSACTION = [
                    "mutation/login"               : ["password"],
                    "mutation/resetPasswordConfirm": ["password"],
                    "mutation/changePassword"      : ["currentPassword", "newPassword"],
            ]

            private void sentParameterToNewRelic(String name, String value) {
                try {
                    NewRelic.addCustomParameter(name, value)
                } catch (Exception e) {
                    log.error('error sending the parameter {} to new relic', name, e)
                }
            }

            @Override
            InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginExecution")
                sentParameterToNewRelic("query", parameters.getExecutionInput().getQuery())
                sentParameterToNewRelic("trace", tracer.currentSpan().context().traceIdString())
                return super.beginExecution(parameters)
            }

            @Override
            InstrumentationContext<Document> beginParse(InstrumentationExecutionParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginParse")
                return super.beginParse(parameters)
            }

            @Override
            InstrumentationContext<List<ValidationError>> beginValidation(InstrumentationValidationParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginValidation")
                return super.beginValidation(parameters)
            }

            @Override
            InstrumentationContext<ExecutionResult> beginExecuteOperation(InstrumentationExecuteOperationParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginExecuteOperation")
                String transactionName = getNewRelicTransactionName(parameters.getExecutionContext().getOperationDefinition())
                if (!transactionName.isEmpty()) {
                    List<String> transactionHiddenAttrs = HIDDEN_ATTR_KEYS_BY_TRANSACTION.get(transactionName.toLowerCase())
                    parameters.getExecutionContext().getVariables()?.each {
                        def value = transactionHiddenAttrs ? ConfigUtils.getHiddenValueByKey(transactionHiddenAttrs, it) : it.value
                        sentParameterToNewRelic("variable-${it.key}", value.toString())
                    }
                    NewRelic.setTransactionName(null, transactionName)
                }
                return super.beginExecuteOperation(parameters)
            }

            private static String getNewRelicTransactionName(OperationDefinition operationDefinition) {
                try {
                    ConfigUtils.getNewRelicTransactionName(operationDefinition)
                } catch (Exception e) {
                    log.error('error building transaction Name', e)
                }
            }

            @Override
            InstrumentationContext<ExecutionResult> beginField(InstrumentationFieldParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginField-" + parameters.getField().getName())
                return super.beginField(parameters)
            }

            @Override
            InstrumentationContext<Object> beginFieldFetch(InstrumentationFieldFetchParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginFieldFetch-" + parameters.getField().getName())
                return super.beginFieldFetch(parameters)
            }

            @Override
            InstrumentationContext<ExecutionResult> beginFieldComplete(InstrumentationFieldCompleteParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginFieldComplete-" + parameters.getField().getName())
                return super.beginFieldComplete(parameters)
            }

            @Override
            DeferredFieldInstrumentationContext beginDeferredField(InstrumentationDeferredFieldParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginDeferredField-" + parameters.getField().getName())
                return super.beginDeferredField(parameters)
            }

            @Override
            InstrumentationContext<ExecutionResult> beginFieldListComplete(InstrumentationFieldCompleteParameters parameters) {
                sentParameterToNewRelic(STEP_PARAM_NR, "beginFieldListComplete-" + parameters.getField().getName())
                return super.beginFieldListComplete(parameters)
            }

            @Override
            CompletableFuture<ExecutionResult> instrumentExecutionResult(
                    ExecutionResult executionResult,
                    InstrumentationExecutionParameters parameters
            ) {

                if (parameters.operation == '__trace')
                    return super.instrumentExecutionResult(executionResult, parameters)
                else {
                    def errors = executionResult.getErrors()
                    sentParameterToNewRelic(STEP_PARAM_NR, "instrumentExecutionResult")
                    Map<Object, Object> currentExt = executionResult.getExtensions()
                    TracingSupport tracingSupport = parameters.getInstrumentationState()
                    Map<Object, Object> tracingMap = new LinkedHashMap<>()
                    tracingMap.putAll(currentExt == null ? Collections.emptyMap() : currentExt)
                    tracingMap.put("duration", tracingSupport.snapshotTracingData().get("duration"))
                    // error logging disabled until we find a way to filtering sensitive input variables
                    if (!errors.isEmpty() && false)
                        log.error(
                                "Error executing graphQl request for query {} and variables {}: [{}]",
                                parameters.query,
                                parameters.variables,
                                errors.collect { it.message }.join(",")
                        )
                    return CompletableFuture.completedFuture(new ExecutionResultImpl(executionResult.getData(), errors, tracingMap))
                }
            }
        }
    }

    @Bean
    GraphQLScalarType nonEmptyString() {
        Scalars.nonEmptyString
    }

    @Bean
    GraphQLScalarType languageTag() {
        Scalars.languageTag
    }

    @Bean
    GraphQLScalarType posIntEqualsOrLessThan10() {
        Scalars.posIntEqualsOrLessThan10
    }

}

class Scalars {

    public static final GraphQLScalarType nonEmptyString =
            GraphQLScalarType
                    .newScalar()
                    .name("NonEmptyString")
                    .description("Built-in Non Empty String")
                    .coercing(
                            new Coercing<String, String>() {
                                @Override
                                String serialize(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingSerializeException("Expected a Non Empty String but was '$input'.")
                                    }
                                }

                                @Override
                                String parseValue(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseValueException("Expected a Non Empty String but was '$input'.")
                                    }
                                }

                                @Override
                                String parseLiteral(Object input) {
                                    if (!(input instanceof StringValue)) {
                                        throw new CoercingParseLiteralException("Expected AST type 'StringValue' but was '${input.class.simpleName}'.")
                                    }
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseLiteralException("Expected a Non Empty String but was '$input'.")
                                    }
                                }

                                private String convert(input) {
                                    def value = input.toString()
                                    if (value.trim().isEmpty()) throw new IllegalArgumentException()
                                    return value
                                }
                            }
                    )
                    .build()

    public static final GraphQLScalarType languageTag =
            GraphQLScalarType
                    .newScalar()
                    .name("LanguageTag")
                    .description("Built-in IETF BCP 47 language tag")
                    .coercing(
                            new Coercing<LanguageTag, LanguageTag>() {
                                @Override
                                LanguageTag serialize(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingSerializeException("Expected a Language-Tag but was '$input'.")
                                    }
                                }

                                @Override
                                LanguageTag parseValue(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseValueException("Expected a Language-Tag but was '$input'.")
                                    }
                                }

                                @Override
                                LanguageTag parseLiteral(Object input) {
                                    if (!(input instanceof StringValue)) {
                                        throw new CoercingParseLiteralException("Expected AST type 'StringValue' but was '${input.class.simpleName}'.")
                                    }
                                    try {
                                        return convert(((StringValue) input).getValue())
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseLiteralException("Expected a Language-Tag but was '$input'.")
                                    }
                                }

                                private LanguageTag convert(input) {
                                    of(LanguageTag.parse(input.toString(), null))
                                            .filter { !it.toString().trim().isEmpty() }
                                            .orElseThrow { throw new IllegalArgumentException() }
                                }
                            }
                    )
                    .build()

    public static final GraphQLScalarType posIntEqualsOrLessThan10 =
            GraphQLScalarType
                    .newScalar()
                    .name("PosIntEqualsOrLessThan10")
                    .description("Built-in positive integer less than 10")
                    .coercing(
                            new Coercing<Integer, Integer>() {
                                @Override
                                Integer serialize(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingSerializeException("Expected a PosIntEqualsOrLessThan10 but was '$input'.")
                                    }
                                }

                                @Override
                                Integer parseValue(Object input) {
                                    try {
                                        return convert(input)
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseValueException("Expected a PosIntEqualsOrLessThan10 but was '$input'.")
                                    }
                                }

                                @Override
                                Integer parseLiteral(Object input) {
                                    if (!(input instanceof IntValue)) {
                                        throw new CoercingParseLiteralException("Expected AST type 'IntValue' but was '${input.class.simpleName}'.")
                                    }
                                    try {
                                        return convert(((IntValue) input).getValue())
                                    }
                                    catch (IllegalArgumentException ignored) {
                                        throw new CoercingParseLiteralException("Expected a Language-Tag but was '$input'.")
                                    }
                                }

                                private Integer convert(input) {
                                    try {
                                        def number = (input as Integer)
                                        if (number < 1 || number > 10) throw new IllegalArgumentException()
                                        return number
                                    } catch (ClassCastException ignored) {
                                        throw new IllegalAccessException()
                                    }
                                }
                            }
                    )
                    .build()

}
