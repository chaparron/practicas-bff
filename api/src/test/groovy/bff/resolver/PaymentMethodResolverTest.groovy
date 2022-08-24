package bff.resolver

import bff.model.BankTransfer
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class PaymentMethodResolverTest {

    private final def codePrefix = "payment.method."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new PaymentMethodResolver(messageSource: messageSource)

    @Test
    void 'resolves payment method text from message source'() {
        def paymentMethod = new BankTransfer()

        String message = "Bank Transfer"
        messageSource.addMessage(codePrefix + "BankTransfer", locale, message)

        assert sut.paymentMethodText(paymentMethod, "en").get() == message
    }

}
