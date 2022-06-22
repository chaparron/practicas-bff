package bff.resolver

import bff.model.CartSummaryItemType
import bff.model.CreditLineProvider
import bff.model.Money
import bff.model.OrderSummary
import bff.service.BnplProvidersService
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderSummaryResolver implements GraphQLResolver<OrderSummary> {

    @Autowired
    MoneyService moneyService

    @Autowired
    BnplProvidersService bnplProvidersService

    Money totalProducts(OrderSummary os) {
        moneyService.getMoney(os.summary.first().accessToken,
                os.summary.find { it.type == CartSummaryItemType.PRODUCTS_TOTAL }.value)
    }

    List<CreditLineProvider> creditLineProviders(OrderSummary os) {
        bnplProvidersService.creditLineProvidersFor(os.summary.first().accessToken)
    }
}
