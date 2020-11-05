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
import graphql.language.Field
import graphql.language.Selection
import graphql.validation.ValidationError
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import java.util.concurrent.CompletableFuture

@Configuration
@Slf4j
class GraphqlConfiguration {

    @Bean
    Instrumentation tracingInstrumentation(Tracer tracer) {
        new TracingInstrumentation() {

            public static final String STEP_PARAM_NR = "step"

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
                parameters.getExecutionInput().getVariables()?.each {
                    sentParameterToNewRelic("variable-${it.key}", it.value.toString())
                }
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
                String transactionName = getNewRelicTransactionName(parameters)
                if (!transactionName.isEmpty()) {
                    NewRelic.setTransactionName(null, transactionName)
                }
                return super.beginExecuteOperation(parameters)
            }

            private String getNewRelicTransactionName(InstrumentationExecuteOperationParameters parameters) {
                List<String> nameParts = new ArrayList()
                try {
                    nameParts.add(parameters.getExecutionContext().getOperationDefinition().getOperation())
                    List<Selection> selections = parameters.getExecutionContext().getOperationDefinition().getSelectionSet().getSelections()
                    selections.each { selection ->
                        if (selection instanceof Field) {
                            Field field = (Field) selection
                            nameParts.add(field.getName())
                        } else {
                            nameParts.add(selection.getClass().getCanonicalName())
                        }
                    }
                } catch (Exception e) {
                    log.error('error building transaction Name', e)
                }
                String transactionName = nameParts.join('/')
                transactionName
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
                    sentParameterToNewRelic(STEP_PARAM_NR, "instrumentExecutionResult")
                    Map<Object, Object> currentExt = executionResult.getExtensions()
                    TracingSupport tracingSupport = parameters.getInstrumentationState()
                    Map<Object, Object> tracingMap = new LinkedHashMap<>()
                    tracingMap.putAll(currentExt == null ? Collections.emptyMap() : currentExt)
                    tracingMap.put("duration", tracingSupport.snapshotTracingData().get("duration"))
                    return CompletableFuture.completedFuture(new ExecutionResultImpl(executionResult.getData(), executionResult.getErrors(), tracingMap))
                }
            }
        }
    }

}
