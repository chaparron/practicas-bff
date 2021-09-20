package bff.model

import bff.bridge.SearchBridge
import bff.bridge.sdk.GroceryListing
import bff.support.DataFetchingEnvironments
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
@Slf4j
class SearchQuery implements GraphQLQueryResolver {

    @Autowired
    SearchBridge searchBridge
    @Autowired
    GroceryListing groceryListing
    @Value('${grocery.listing.enabled:false}')
    Boolean groceryListingEnabled

    SearchResult search(SearchInput searchInput, DataFetchingEnvironment dfe) {
        return bridge(dfe).search(searchInput)
    }

    SearchResponse searchV2(SearchInput searchInput, DataFetchingEnvironment dfe) {
        return bridge(dfe).searchV2(searchInput)
    }

    SearchResponse previewSearch(PreviewSearchInput searchInput, DataFetchingEnvironment dfe) {
        return bridge(dfe).previewSearch(searchInput)
    }

    private SearchBridge bridge(DataFetchingEnvironment dfe) {
        return (groceryListingEnabled || DataFetchingEnvironments.experimentalMode(dfe))
                ? groceryListing
                : searchBridge
    }

}



