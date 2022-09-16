package bff.resolver

import bff.bridge.sdk.GroceryListing
import bff.model.*
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class SuggestedOrderResultResolver implements GraphQLResolver<SuggestedOrderResult> {
    @Autowired
    GroceryListing groceryListing

    List<SuggestedOrderProduct> products(SuggestedOrderResult suggestedOrderResult) {
        List<ProductSearch> productSearchList = groceryListing.getProductsByIdsAndSupplierId(
                suggestedOrderResult.accessToken,
                suggestedOrderResult.items.collect { it.productId }.toSet(),
                suggestedOrderResult.supplierId
        )
        Map<Long, Map<Long, Integer>> productUnitsAndQuantityByProductId =
                getProductUnitsAndQuantityById(suggestedOrderResult.items)
        return mapToSuggestedOrderProducts(productSearchList, productUnitsAndQuantityByProductId)
    }

    private static Map<Long, Map<Long, Integer>> getProductUnitsAndQuantityById(
            List<SuggestedOrderItem> suggestedOrderItems) {
        Map<Long, Map<Long, Integer>> productUnitsAndQuantityById = new HashMap<>()

        suggestedOrderItems.forEach {
            Map<Long, Integer> savedQuantityByUnits =
                    productUnitsAndQuantityById.getOrDefault(it.productId, new HashMap<Long, Integer>())
            savedQuantityByUnits.put(it.productUnits, it.quantity)
            productUnitsAndQuantityById.put(it.productId, savedQuantityByUnits)
        }

        return productUnitsAndQuantityById
    }

    private static List<SuggestedOrderProduct> mapToSuggestedOrderProducts(
            List<ProductSearch> productSearchList, Map<Long, Map<Long, Integer>> productUnitsAndQuantityByProductId) {
        List<SuggestedOrderProduct> suggestedOrderProducts = new ArrayList<>()

        productSearchList.collect { productSearch ->
            getPricesWithQuantityFilteredByDisplay(productSearch, productUnitsAndQuantityByProductId).forEach { priceWithQuantity ->
                suggestedOrderProducts.add(
                        new SuggestedOrderProduct(
                                id: productSearch.id,
                                ean: productSearch.ean,
                                name: productSearch.name,
                                category: productSearch.category,
                                brand: productSearch.brand,
                                images: productSearch.images,
                                price: priceWithQuantity.price,
                                quantity: priceWithQuantity.quantity
                        )
                )
            }
        }
        return suggestedOrderProducts
    }

    private static List<PriceWithQuantity> getPricesWithQuantityFilteredByDisplay(
            ProductSearch productSearch, Map<Long, Map<Long, Integer>> productUnitsAndQuantityById) {
        return productSearch.prices.findAll {
            productUnitsAndQuantityById.containsKey(productSearch.id) &&
                    productUnitsAndQuantityById.get(productSearch.id).containsKey(it.display.units.toLong())
        }.collect {
            Integer quantity = productUnitsAndQuantityById.get(productSearch.id).get(it.display.units.toLong())
            new PriceWithQuantity(it, quantity)
        }
    }
}

class PriceWithQuantity {
    Price price
    Integer quantity

    PriceWithQuantity(Price price, Integer quantity) {
        this.price = price
        this.quantity = quantity
    }
}