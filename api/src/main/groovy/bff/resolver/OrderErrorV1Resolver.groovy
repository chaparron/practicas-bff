package bff.resolver

import bff.model.Money
import bff.model.OrderErrorV1
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderErrorV1Resolver implements GraphQLResolver<OrderErrorV1> {

    @Autowired
    MoneyService moneyService

    Money prevTotalPriceMoney(OrderErrorV1 orderError) {
        moneyService.getMoney(orderError.accessToken, orderError.prevTotalPrice)
    }

    Money actualTotalPriceMoney(OrderErrorV1 orderError) {
        moneyService.getMoney(orderError.accessToken, orderError.actualTotalPrice)
    }

}
