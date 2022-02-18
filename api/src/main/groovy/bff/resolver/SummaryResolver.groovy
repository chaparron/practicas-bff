package bff.resolver

import bff.JwtToken
import bff.model.CartSummaryItemType
import bff.model.Money
import bff.model.Summary
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class SummaryResolver implements GraphQLResolver<Summary> {

    @Autowired
    MoneyService moneyService

    @Autowired
    MessageSource messageSource

    Money valueMoney(Summary summary) {
        moneyService.getMoney(summary.accessToken, summary.value)
    }

    String description(Summary summary, String languageTag) {
        if ((summary.type == CartSummaryItemType.SERVICE_FEE || summary.type == CartSummaryItemType.CREDITS_USED)
                && JwtToken.countryFromString(summary.accessToken) == "mx") {
            return messageSource.getMessage("orderSummary.${summary.type.name()}.mx", null, Locale.forLanguageTag(languageTag))
        }
        return messageSource.getMessage("orderSummary.${summary.type.name()}", null, Locale.forLanguageTag(languageTag))
    }
}
