package bff.model

import bff.bridge.SearchBridge
import bff.bridge.sdk.GroceryListing
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.language.Field
import graphql.language.IntValue
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static bff.JwtToken.countryFromString
import static bff.support.DataFetchingEnvironments.experimentalMode
import static java.util.Optional.ofNullable

@Component
@Slf4j
class SearchQuery implements GraphQLQueryResolver {

    @Autowired
    SearchBridge searchBridge
    @Autowired
    GroceryListing groceryListing
    @Value('${grocery.listing.countries:}')
    List<String> groceryListingEnabledCountries

    SearchResult search(SearchInput input, DataFetchingEnvironment dfe) {
        return isGroceryListingEnabled(dfe, { countryFromString(input.accessToken) })
                ? groceryListing.search(input)
                : searchBridge.search(input)
    }

    SearchResponse searchV2(SearchInput input, DataFetchingEnvironment dfe) {
        return isGroceryListingEnabled(dfe, { countryFromString(input.accessToken) })
                ? groceryListing.search(input)
                : searchBridge.searchV2(input)
    }

    ScrollableSearchResult scrollSearch(SearchScrollInput input) {
        return groceryListing.scroll(input)
    }

    SearchResponse previewSearch(PreviewSearchInput input, DataFetchingEnvironment dfe) {
        return ofNullable(input.countryId)
                .map { country -> isGroceryListingEnabled(dfe, { country }) }
                .orElse(true)
                ? groceryListing.search(input)
                : searchBridge.previewSearch(input)
    }

    ScrollableSearchResult scrollPreviewSearch(PreviewSearchScrollInput input) {
        return groceryListing.scroll(input)
    }

    Suggestions suggest(SuggestInput input, DataFetchingEnvironment dfe) {
        return groceryListing.suggest(
                [
                        "products"  : { Integer size -> { SuggestInput i -> i.forProducts(size) } },
                        "brands"    : { Integer size -> { SuggestInput i -> i.forBrands(size) } },
                        "categories": { Integer size -> { SuggestInput i -> i.forCategories(size) } },
                        "suppliers" : { Integer size -> { SuggestInput i -> i.forSuppliers(size) } }
                ]
                        .collect {
                            numberOfSuggestionsFor(it.key, dfe)
                                    .map { size -> it.value(size) }
                                    .orElse({ i -> i })
                        }
                        .inject(input, { SuggestInput i, it -> it(i) })
        )
    }

    Suggestions previewSuggest(PreviewSuggestInput input, DataFetchingEnvironment dfe) {
        return groceryListing.suggest(
                [
                        "products"  : { Integer size -> { PreviewSuggestInput i -> i.forProducts(size) } },
                        "brands"    : { Integer size -> { PreviewSuggestInput i -> i.forBrands(size) } },
                        "categories": { Integer size -> { PreviewSuggestInput i -> i.forCategories(size) } }
                ]
                        .collect {
                            numberOfSuggestionsFor(it.key, dfe)
                                    .map { size -> it.value(size) }
                                    .orElse({ i -> i })
                        }
                        .inject(input, { PreviewSuggestInput i, it -> it(i) })
        )
    }

    static def numberOfSuggestionsFor(String field, DataFetchingEnvironment dfe) {
        ofNullable(
                dfe.field.getSelectionSet().getSelections()
                        .collect { it as Field }
                        .find { it.name == field }
        ).map { (it.arguments.first().value as IntValue).value.toInteger() }
    }

    private def isGroceryListingEnabled(DataFetchingEnvironment dfe, Closure<String> country) {
        (experimentalMode(dfe) || ofNullable(groceryListingEnabledCountries).orElse([]).contains(country()))
    }

}



