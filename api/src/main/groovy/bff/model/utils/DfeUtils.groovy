package bff.model.utils

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment

class DfeUtils {

    static String getAccessToken(DataFetchingEnvironment dfe, String inputName = "accessToken") {
        return dfe.variables["input"][inputName]
    }

    static Boolean isOperation(DataFetchingEnvironment dfe, String operationName) {
        return dfe.getOperationDefinition().selectionSet.selections.contains {
            return it instanceof Field && it.name == operationName
        }
    }

}
