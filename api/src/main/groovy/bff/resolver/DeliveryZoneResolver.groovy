package bff.resolver

import bff.model.DeliveryZone
import bff.model.Money
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeliveryZoneResolver implements GraphQLResolver<DeliveryZone> {

    @Autowired
    MoneyService moneyService

    Money minAmountMoney(DeliveryZone deliveryZone) {
        moneyService.getMoney(deliveryZone.accessToken, deliveryZone.minAmount)
    }

    Money maxAmountMoney(DeliveryZone deliveryZone) {
        moneyService.getMoney(deliveryZone.accessToken, deliveryZone.maxAmount)
    }

    Money deliveryCostMoney(DeliveryZone deliveryZone) {
        moneyService.getMoney(deliveryZone.accessToken, deliveryZone.deliveryCost)
    }
}
