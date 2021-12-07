package bff.model.utils

import graphql.language.Field
import graphql.schema.DataFetchingEnvironment

class DfeUtils {

    static String getAccessToken(DataFetchingEnvironment dfe, String inputName = "accessToken") {
        if (dfe.variables["input"]) {
            dfe.variables["input"][inputName]
        }
        return null
    }

    static Boolean isOperation(DataFetchingEnvironment dfe, String operationName) {
        return dfe.getOperationDefinition().selectionSet.selections.contains {
            return it instanceof Field && it.name == operationName
        }
    }

}
