package bff.resolver

import bff.model.Money
import bff.model.OrderError
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderErrorResolver implements GraphQLResolver<OrderError> {

    @Autowired
    MoneyService moneyService

    Money prevValueMoney(OrderError orderError) {
        moneyService.getMoney(orderError.accessToken, orderError.prevValue)
    }

    Money actualValueMoney(OrderError orderError) {
        moneyService.getMoney(orderError.accessToken, orderError.actualValue)
    }
}
