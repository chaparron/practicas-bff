package bff.resolver


import bff.model.DiscountStep
import bff.model.MinProductQuantityByProduct
import bff.model.Money
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DiscountStepResolver implements GraphQLResolver<DiscountStep> {

    @Autowired
    MoneyService moneyService

    Money value(DiscountStep step) {
        moneyService.getMoneyByCountry(step.countryId, step.value)
    }

    Money unitValue(DiscountStep step) {
        moneyService.getMoneyByCountry(step.countryId, step.unitValue)
    }

    List<MinProductQuantityByProduct> minQuantityByProducts(DiscountStep step) {
        step.minQuantityByProducts.collect {
            new MinProductQuantityByProduct(
                    product: it.key,
                    quantity: it.value
            )
        }.toList()
    }

}
