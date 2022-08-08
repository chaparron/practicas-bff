package bff.resolver

import bff.model.PaymentMode
import bff.model.PaymentModeType
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class PaymentModeResolverTest {

    private final def codePrefix = "payment.provider.mode."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new PaymentModeResolver(messageSource: messageSource)


    @Test
    void 'resolves text from message source for Pay Now'() {

        String code = codePrefix + PaymentModeType.PAY_NOW.name()
        String message = "Pay Now"
        messageSource.addMessage(code, locale, message)

        def paymentMode = new PaymentMode(paymentType: PaymentModeType.PAY_NOW)

        assert sut.text(paymentMode, "en").get() == message
    }

    @Test
    void 'resolves text from message source for Pay Later'() {
        String code = codePrefix + PaymentModeType.PAY_LATER.name()
        String message = "Pay Later"
        messageSource.addMessage(code, locale, message)

        def paymentMode = new PaymentMode(paymentType: PaymentModeType.PAY_LATER)

        assert sut.text(paymentMode, "en").get() == message
    }

    @Test
    void 'resolves text from given default value'() {
        def paymentMode = new PaymentMode(paymentType: PaymentModeType.PAY_NOW)

        assert sut.text(paymentMode, "es").get() == paymentMode.paymentType.name()
    }

}