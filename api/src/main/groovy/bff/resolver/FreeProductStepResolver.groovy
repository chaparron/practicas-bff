package bff.resolver


import bff.model.FreeProductStep
import bff.model.MinProductQuantityByProduct
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component

@Component
class FreeProductStepResolver implements GraphQLResolver<FreeProductStep> {

    List<MinProductQuantityByProduct> minQuantityByProducts(FreeProductStep step) {
        step.minQuantityByProducts.collect {
            new MinProductQuantityByProduct(
                    product: it.key,
                    quantity: it.value
            )
        }.toList()
    }

}
