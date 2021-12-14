package bff.model


import bff.bridge.sdk.GroceryListing
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.language.Field
import graphql.language.IntValue
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class SearchQuery implements GraphQLQueryResolver {

    @Autowired
    GroceryListing groceryListing

    SearchResult search(SearchInput input) {
        groceryListing.search(input)
    }

    SearchResponse searchV2(SearchInput input) {
        groceryListing.search(input)
    }

    ScrollableSearchResult scrollSearch(SearchScrollInput input) {
        groceryListing.scroll(input)
    }

    SearchResponse previewSearch(PreviewSearchInput input) {
        groceryListing.search(input)
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
                        .collect { entry ->
                            numberOfSuggestionsFor(entry.key, dfe)
                                    .map { entry.value(it) }
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
                        .collect { entry ->
                            numberOfSuggestionsFor(entry.key, dfe)
                                    .map { entry.value(it) }
                                    .orElse({ i -> i })
                        }
                        .inject(input, { PreviewSuggestInput i, it -> it(i) })
        )
    }

    private static def numberOfSuggestionsFor(String field, DataFetchingEnvironment dfe) {
        ofNullable(
                dfe.field.getSelectionSet().getSelections()
                        .collect { it as Field }
                        .find { it.name == field }
        ).map { (it.arguments.first().value as IntValue).value.toInteger() }
    }

}



