package bff.resolver

import bff.model.NetBanking
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class PaymentMethodResolverTest {

    private final def codePrefix = "payment.method."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new PaymentMethodResolver(messageSource: messageSource)

    @Test
    void 'resolves payment method text from message source'() {
        def paymentMethod = new NetBanking()

        String message = "Net Banking"
        messageSource.addMessage(codePrefix + "NetBanking", locale, message)

        assert sut.paymentMethodText(paymentMethod, "en").get() == message
    }

}
