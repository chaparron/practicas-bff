package bff.resolver

import bff.model.InstantPaymentProvider
import bff.model.PaymentProviderCode
import bff.service.PaymentProviderTranslationService
import org.junit.Before
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class PaymentProviderTranslationServiceTest {

    private final def code = "paymentProvider.poweredBy.label"
    private final def poweredByMessage = "Powered by {0}"
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def providerCode = PaymentProviderCode.JPMORGAN
    private def instantPaymentProvider = new InstantPaymentProvider(providerCode: providerCode)
    private def sut = new PaymentProviderTranslationService(messageSource: messageSource)

    @Before
    void setup() {
        messageSource.addMessage(code, locale, poweredByMessage)
    }

    @Test
    void 'resolves poweredByLabel from message source'() {
        assert sut.poweredByLabel(instantPaymentProvider, "en") == "Powered by $providerCode.poweredBy"
    }

    @Test
    void 'resolves poweredByLabel from given default value'() {
        assert sut.poweredByLabel(instantPaymentProvider, "es") == providerCode.poweredBy
    }
}
