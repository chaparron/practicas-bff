package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.Money
import bff.model.Summary
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SummaryResolver implements GraphQLResolver<Summary> {

    @Autowired
    CountryBridge countryBridge

    Money valueMoney(Summary summary) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(summary.accessToken)).currency.code, summary.value)
    }
}
