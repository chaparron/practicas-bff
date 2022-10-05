package bff.resolver

import bff.model.SuperMoneyCreditLine
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class SuperMoneyCreditLineResolver implements GraphQLResolver<SuperMoneyCreditLine> {

    private MessageSource messageSource

    SuperMoneyCreditLineResolver(MessageSource messageSource) {
        this.messageSource = messageSource
    }

    String approvedLimitTooltip(SuperMoneyCreditLine creditLine, String languageTag) {
        return messageSource.getMessage("bnpl.creditLine.approvedLimit.tooltip", null, Locale.forLanguageTag(languageTag))
    }

    String toRepayTooltip(SuperMoneyCreditLine creditLine, String languageTag) {
        return messageSource.getMessage("bnpl.creditLine.toRepay.tooltip", null, Locale.forLanguageTag(languageTag))
    }

    String remainingTooltip(SuperMoneyCreditLine creditLine, String languageTag) {
        return messageSource.getMessage("bnpl.creditLine.remaining.tooltip", null, Locale.forLanguageTag(languageTag))
    }

}
