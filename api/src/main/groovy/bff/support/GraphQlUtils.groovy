package bff.support

import graphql.execution.ConditionalNodes
import graphql.language.*
import graphql.schema.DataFetchingEnvironment

class GraphQlUtils {

    static <T> T argumentValue(Field field,
                               String argument,
                               DataFetchingEnvironment environment,
                               Closure<T> mapper) {
        field.getArguments().findResult {
            if (it.name == argument) {
                def value = it.value
                switch (value) {
                    case { value instanceof VariableReference }:
                        return environment.getVariables().get((value as VariableReference).name) as T
                    default: mapper(value)
                }
            } else null
        }
    }

    static Integer intArgumentValue(Field field, String argument, DataFetchingEnvironment environment) {
        argumentValue(field, argument, environment, { (it as IntValue).value })
    }

    static Boolean boolArgumentValue(Field field, String argument, DataFetchingEnvironment environment) {
        argumentValue(field, argument, environment, { (it as BooleanValue).value })
    }

    static String enumArgumentValue(Field field, String argument, DataFetchingEnvironment environment) {
        argumentValue(field, argument, environment, { (it as EnumValue).name })
    }

    static <T> List<T> arrayArgumentValue(Field field,
                                          String argument,
                                          DataFetchingEnvironment environment,
                                          Closure<T> mapper) {
        argumentValue(
                field,
                argument,
                environment,
                { (it as ArrayValue).values.collect { mapper(it) } }
        )
    }

    static Boolean shouldInclude(Field field, DataFetchingEnvironment environment) {
        new ConditionalNodes().shouldInclude(environment.variables, field.directives)
    }

}
