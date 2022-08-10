package bff.resolver

import bff.model.PaymentButton
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class PaymentButtonResolverTest {

    private final def code = "supplierOrder.payment.button"
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new PaymentButtonResolver(messageSource: messageSource)


    @Test
    void 'resolves text from message source for payment button'() {

        String message = "Pay"
        messageSource.addMessage(code, locale, message)

        def paymentButton = new PaymentButton()

        assert sut.text(paymentButton, "en").get() == message
    }

    @Test
    void 'resolves text from given default value'() {
        def paymentButton = new PaymentButton()

        assert sut.text(paymentButton, "es").get() == ""
    }

}