package bff.configuration

import graphql.language.Field
import graphql.language.OperationDefinition
import graphql.language.Selection

class ConfigUtils {
    private static String HIDDEN_ATTR_VALUE = "*****"

    static String getNewRelicTransactionName(OperationDefinition operationDefinition) throws Exception {
        List<String> nameParts = new ArrayList()
        nameParts.add(operationDefinition.getOperation())
        List<Selection> selections = operationDefinition.getSelectionSet().getSelections()
        selections.each { selection ->
            if (selection instanceof Field) {
                Field field = (Field) selection
                nameParts.add(field.getName())
            } else {
                nameParts.add(selection.getClass().getCanonicalName())
            }
        }
        String transactionName = nameParts.join('/')
        transactionName
    }

    static Object getHiddenValueByKey(List<String> transactionHiddenAttrs, Map.Entry<String, Object> variable) {
        def value = variable.value
        if (transactionHiddenAttrs.contains(variable.key)) {
            value = HIDDEN_ATTR_VALUE
        } else if (variable.value instanceof Map<String, Object>) {
            value = [:] as LinkedHashMap<String, Object>
            variable.value.each { value.put(it.key, getHiddenValueByKey(transactionHiddenAttrs, it)) }
        }
        value
    }
}
