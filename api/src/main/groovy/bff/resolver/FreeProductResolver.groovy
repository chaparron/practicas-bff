package bff.resolver

import bff.model.Display
import bff.model.FixedQuantityFreeProduct
import bff.model.FreeProduct
import bff.model.Product
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

import static java.util.Optional.of

@Component
class FreeProductResolver implements GraphQLResolver<FreeProduct> {

    String label(FreeProduct freeProduct, LanguageTag languageTag) {
        freeProduct.label.call(languageTag)
    }

    Integer from(FreeProduct freeProduct) {
        freeProduct.steps.min { it.from }.from
    }

    Integer to(FreeProduct freeProduct) {
        of(freeProduct.steps.max { it.to }.to).orElse(null)
    }

    Product product(FreeProduct freeProduct) {
        (reward(freeProduct) as FixedQuantityFreeProduct).product
    }

    Display display(FreeProduct freeProduct) {
        reward(freeProduct).display
    }

    Integer quantity(FreeProduct freeProduct) {
        reward(freeProduct).quantity
    }

    private FixedQuantityFreeProduct reward(FreeProduct freeProduct) {
        freeProduct.steps.head().rewards.head().items.head() as FixedQuantityFreeProduct
    }

}
