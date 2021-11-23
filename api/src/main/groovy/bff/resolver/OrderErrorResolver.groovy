package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.Money
import bff.model.OrderError
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderErrorResolver implements GraphQLResolver<OrderError> {

    @Autowired
    CountryBridge countryBridge

    Money prevValueMoney(OrderError orderError) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(orderError.accessToken)).currency.code, orderError.prevValue)
    }

    Money actualValueMoney(OrderError orderError) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(orderError.accessToken)).currency.code, orderError.actualValue)
    }
}
