package bff.model


import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component

@Component
@Slf4j
class PaymentGatewaysQuery implements GraphQLQueryResolver {

    PaymentGatewayDataResult findPaymentGatewayData(FindPaymentGatewayDataInput input) {
        switch (input.paymentProviderCode) {
            case PaymentProviderCode.JPMORGAN:
                new JpMorganPaymentGatewayData(
                        bankId: "000004",
                        merchantId: "101000000000781",
                        terminalId: "10100781",
                        encData: "N8EbKkM3TF+xqs6HTrEMoU64y64FcUQMC8KzKen3OJhrWCDiCH3KM9goK6ZzMiYPxKf67KC0kwP7Exm/KM5HCX9Pa08gTCKyrxJQbFprTVDDCZnPHFNRvYxUnlX1kdmWE/BEle1//zze8bRTycnOE6hLkYj4Z12p0iAwF3T8DId0Riwd/z6tI1GTcjWwsSQibgPfHDIY//Vpz7jrP/pHEhZHlUWG2XIhhppDL+Tax5wtiZA9ac6L5HwiXudguQ0bngKcUEd+M/l6EvCZHL/ThA8ZT3r00RY5eqp2MBlji3m+WgM5PloxHfvsE1nQLuvOgvagfLnXyYZXjful+7Lct/9uu5J+ytlJxSO8pHc4shrb9eH64h/DzKZEmWW9PrMj6/2ujl9h+/rfdochuqjPAwn40ilZIqtHxDVWr68r1kdwkt82ZSeyRc3KHWGkzLIgUGsvuoBJAKbe8fePdQcTrA=="
                )
                break
            default:
                new PaymentGatewayDataFailed(reason: PaymentGatewayDataFailedReason.GATEWAY_NOT_SUPPORTED)
                break
        }
    }
}
