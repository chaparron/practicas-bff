package bff.service

import bff.model.PaymentProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class PaymentProviderTranslationService {

    @Autowired
    private MessageSource messageSource

    String poweredByLabel(PaymentProvider paymentProvider, String languageTag) {
        messageSource.getMessage(
                "paymentProvider.poweredBy.label",
                [paymentProvider.providerCode.poweredBy].toArray(),
                paymentProvider.providerCode.poweredBy,
                Locale.forLanguageTag(languageTag)
        )
    }
}
