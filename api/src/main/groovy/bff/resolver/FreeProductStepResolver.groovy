package bff.resolver

import bff.model.FreeProductStep
import bff.model.MinProductQuantityByProduct
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

import static java.lang.Integer.MAX_VALUE
import static java.util.Optional.ofNullable

@Component
class FreeProductStepResolver implements GraphQLResolver<FreeProductStep> {

    @Deprecated
    Integer to(FreeProductStep step) {
        ofNullable(step.to).orElse(MAX_VALUE)
    }

    Integer maybeTo(FreeProductStep step) {
        step.to
    }

    List<MinProductQuantityByProduct> minQuantityByProducts(FreeProductStep step) {
        step.minQuantityByProducts.collect {
            new MinProductQuantityByProduct(
                    product: it.key,
                    quantity: it.value
            )
        }.toList()
    }

}
