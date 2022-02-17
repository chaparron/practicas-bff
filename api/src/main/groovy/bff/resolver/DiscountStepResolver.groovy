package bff.resolver


import bff.model.DiscountStep
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
        moneyService.getMoney(step.accessToken, step.value)
    }

    Money unitValue(DiscountStep step) {
        moneyService.getMoney(step.accessToken, step.unitValue)
    }

}
