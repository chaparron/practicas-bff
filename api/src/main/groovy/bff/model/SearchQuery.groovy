package bff.model

import bff.bridge.SearchBridge
import bff.bridge.sdk.GroceryListing
import bff.support.DataFetchingEnvironments
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.language.Field
import graphql.language.IntValue
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

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
        return (groceryListingEnabled || DataFetchingEnvironments.experimentalMode(dfe))
                ? groceryListing.search(searchInput)
                : searchBridge.search(searchInput)
    }

    SearchResponse searchV2(SearchInput searchInput, DataFetchingEnvironment dfe) {
        return (groceryListingEnabled || DataFetchingEnvironments.experimentalMode(dfe))
                ? groceryListing.search(searchInput)
                : searchBridge.searchV2(searchInput)
    }

    SearchResponse previewSearch(PreviewSearchInput searchInput, DataFetchingEnvironment dfe) {
        return (groceryListingEnabled || DataFetchingEnvironments.experimentalMode(dfe))
                ? groceryListing.search(searchInput)
                : searchBridge.previewSearch(searchInput)
    }

    Suggestions suggest(SuggestInput input, DataFetchingEnvironment dfe) {
        def numberOfSuggestionsFor = { String field ->
            ofNullable(
                    dfe.field.getSelectionSet().getSelections()
                            .collect { it as Field }
                            .find { it.name == field }
            ).map { (it.arguments.first().value as IntValue).value.toInteger() }
        }
        return groceryListing.suggest(
                [
                        "products"  : { Integer size -> { SuggestInput i -> i.forProducts(size) } },
                        "brands"    : { Integer size -> { SuggestInput i -> i.forBrands(size) } },
                        "categories": { Integer size -> { SuggestInput i -> i.forCategories(size) } },
                        "suppliers" : { Integer size -> { SuggestInput i -> i.forSuppliers(size) } }
                ]
                        .collect {
                            numberOfSuggestionsFor(it.key)
                                    .map { size -> it.value(size) }
                                    .orElse({ i -> i })
                        }
                        .inject(input, { SuggestInput i, it -> it(i) })
        )
    }

}



