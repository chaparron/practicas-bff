package bff.resolver

import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class FacetingResolver implements GraphQLResolver<Faceting> {

    CategoryFacet categories(Faceting faceting, Boolean flattened) {
        faceting.categories.orElse(null)
    }

    BrandFacet brands(Faceting faceting, Integer size, BrandFacetSorting sorting) {
        faceting.brands.orElse(null)
    }

    SupplierFacet suppliers(Faceting faceting, Integer size, SupplierFacetSorting sorting) {
        faceting.suppliers.orElse(null)
    }

    List<FeatureFacet> features(Faceting faceting, Integer size, FeatureFacetSorting sorting, Set<String> features) {
        faceting.features.findAll { feature ->
            ofNullable(features)
                    .filter { !it.isEmpty() }
                    .map { it.contains(feature.id) }
                    .orElse(true)
        }
    }

    DiscountFacet discounts(Faceting faceting, Integer interval) {
        faceting.discounts.orElse(null)
    }

}