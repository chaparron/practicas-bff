package bff.support

import graphql.schema.DataFetchingEnvironment

class DataFetchingEnvironments {

    final static String EXPERIMENTAL = '_experimental'

    static boolean experimentalMode(DataFetchingEnvironment dfe) {
        dfe?.executionContext?.operationDefinition?.name == EXPERIMENTAL
    }
}

