package bff.resolver

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.DeliveryZone
import bff.model.Money
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeliveryZoneResolver implements GraphQLResolver<DeliveryZone> {

    @Autowired
    CountryBridge countryBridge

    Money minAmountMoney(DeliveryZone deliveryZone) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(deliveryZone.accessToken)).currency.code, deliveryZone.minAmount)
    }

    Money maxAmountMoney(DeliveryZone deliveryZone) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(deliveryZone.accessToken)).currency.code, deliveryZone.maxAmount)
    }

    Money deliveryCostMoney(DeliveryZone deliveryZone) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(deliveryZone.accessToken)).currency.code, deliveryZone.deliveryCost)
    }
}
