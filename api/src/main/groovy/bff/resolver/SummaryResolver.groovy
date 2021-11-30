package bff.resolver

import bff.model.Money
import bff.model.Summary
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SummaryResolver implements GraphQLResolver<Summary> {

    @Autowired
    MoneyService moneyService

    Money valueMoney(Summary summary) {
        moneyService.getMoney(summary.accessToken, summary.value)
    }
}
