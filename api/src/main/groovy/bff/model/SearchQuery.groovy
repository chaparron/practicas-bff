package bff.model

import bff.bridge.sdk.GroceryListing
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import graphql.language.Field
import graphql.language.InlineFragment
import graphql.language.StringValue
import graphql.schema.DataFetchingEnvironment
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static bff.support.GraphQlUtils.*
import static java.lang.Math.max
import static java.util.Optional.ofNullable

@Component
@Slf4j
class SearchQuery implements GraphQLQueryResolver {

    @Autowired
    GroceryListing groceryListing

    SearchResult search(SearchInput input) {
        groceryListing.search(input)
    }

    SearchResponse searchV2(SearchInput input, DataFetchingEnvironment environment) {
        def facets = facets(environment)
        Closure<Closure<SearchInput>> facet = { String name, Closure<SearchInput> builder ->
            { SearchInput i ->
                ofNullable(facets.find { it.name == name })
                        .map { builder(i, it) }
                        .orElse(i)
            }
        }
        groceryListing.search(
                [
                        facet("categories", { SearchInput builder, Field field ->
                            def count
                            count = { Field it, Integer acc ->
                                ofNullable(
                                        it.getSelectionSet().getSelections().findResult {
                                            if ((it as Field).name == "slices") count(it, acc + 1)
                                            else null
                                        }
                                ).orElse(acc)
                            }
                            builder.facetingByCategories(
                                    max(1, count(field, 0) as Integer),
                                    boolArgumentValue(field, "flattened", environment)
                            )
                        }),
                        facet("brands", { SearchInput builder, Field field ->
                            builder.facetingByBrands(
                                    intArgumentValue(field, "size", environment),
                                    ofNullable(enumArgumentValue(field, "sorting", environment))
                                            .map { BrandFacetSorting.valueOf(it) }
                            )
                        }),
                        facet("suppliers", { SearchInput builder, Field field ->
                            builder.facetingBySuppliers(
                                    intArgumentValue(field, "size", environment),
                                    ofNullable(enumArgumentValue(field, "sorting", environment))
                                            .map { SupplierFacetSorting.valueOf(it) }
                            )
                        }),
                        facet("features", { SearchInput builder, Field field ->
                            builder.facetingByFeatures(
                                    intArgumentValue(field, "size", environment),
                                    ofNullable(enumArgumentValue(field, "sorting", environment))
                                            .map { FeatureFacetSorting.valueOf(it) },
                                    arrayArgumentValue(
                                            field,
                                            "filtered",
                                            environment,
                                            { (it as StringValue).value }
                                    ).toSet()
                            )
                        }),
                        facet("discounts", { SearchInput builder, Field field ->
                            builder.facetingByDiscounts(
                                    intArgumentValue(field, "interval", environment)
                            )
                        })
                ].inject(input, { SearchInput i, it -> it(i) })
        )
    }

    ScrollableSearchResult scrollSearch(SearchScrollInput input) {
        groceryListing.scroll(input)
    }

    SearchResponse previewSearch(PreviewSearchInput input, DataFetchingEnvironment environment) {
        def facets = facets(environment)
        Closure<Closure<PreviewSearchInput>> facet = { String name, Closure<PreviewSearchInput> builder ->
            { PreviewSearchInput i ->
                ofNullable(facets.find { it.name == name })
                        .map { builder(i, it) }
                        .orElse(i)
            }
        }
        groceryListing.search(
                [
                        facet("categories", { SearchInput builder, Field field ->
                            def count
                            count = { Field it, Integer acc ->
                                ofNullable(
                                        it.getSelectionSet().getSelections().findResult {
                                            if ((it as Field).name == "slices") count(it, acc + 1)
                                            else null
                                        }
                                ).orElse(acc)
                            }
                            builder.facetingByCategories(
                                    max(1, count(field, 0) as Integer),
                                    boolArgumentValue(field, "flattened", environment)
                            )
                        }),
                        facet("brands", { SearchInput builder, Field field ->
                            builder.facetingByBrands(
                                    intArgumentValue(field, "size", environment),
                                    ofNullable(enumArgumentValue(field, "sorting", environment))
                                            .map { BrandFacetSorting.valueOf(it) }
                            )
                        }),
                        facet("features", { SearchInput builder, Field field ->
                            builder.facetingByFeatures(
                                    intArgumentValue(field, "size", environment),
                                    ofNullable(enumArgumentValue(field, "sorting", environment))
                                            .map { FeatureFacetSorting.valueOf(it) },
                                    arrayArgumentValue(
                                            field,
                                            "filtered",
                                            environment,
                                            { (it as StringValue).value }
                                    ).toSet()
                            )
                        }),
                        facet("discounts", { SearchInput builder, Field field ->
                            builder.facetingByDiscounts(
                                    intArgumentValue(field, "interval", environment)
                            )
                        })
                ].inject(input, { PreviewSearchInput i, it -> it(i) })
        )
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
                        "categories": { Integer size -> { PreviewSuggestInput i -> i.forCategories(size) } },
                        "suppliers" : { Integer size -> { PreviewSuggestInput i -> i.forSuppliers(size) } }
                ]
                        .collect { entry ->
                            numberOfSuggestionsFor(entry.key, dfe)
                                    .map { entry.value(it) }
                                    .orElse({ i -> i })
                        }
                        .inject(input, { PreviewSuggestInput i, it -> it(i) })
        )
    }

    List<MostSearchedTerm> mostSearchedTerms(MostSearchedTermsInput input) {
        return groceryListing.mostSearchedTerms(input)
    }

    List<MostSearchedTerm> previewMostSearchedTerms(PreviewMostSearchedTermsInput input) {
        return groceryListing.previewMostSearchedTerms(input)
    }

    private static def facets(DataFetchingEnvironment environment) {
        ofNullable(environment)
                .map {
                    (it.field.getSelectionSet().getSelections().head() as InlineFragment)
                            .getSelectionSet()
                            .getSelections()
                            .findResult {
                                def field = it as Field
                                if ((field.name == "faceting") && shouldInclude(field, environment))
                                    field.getSelectionSet().getSelections().collect { it as Field }
                                else null
                            }
                }
                .orElse([])
    }

    private static def numberOfSuggestionsFor(String field, DataFetchingEnvironment environment) {
        ofNullable(
                environment.field.getSelectionSet().getSelections()
                        .collect { it as Field }
                        .find { it.name == field }
        ).map { intArgumentValue(it, "size", environment) }
    }

}



