package bff.resolver

import bff.model.DigitalPaymentFailed
import bff.model.DigitalPaymentFailedReason
import org.junit.Test
import org.springframework.context.support.StaticMessageSource
import org.springframework.stereotype.Component

@Component
class DigitalPaymentFailedResolverTest {
  private final def codePrefix = "digital.payment.error.label."
  private final def locale = Locale.forLanguageTag("en")
  private def messageSource = new StaticMessageSource()
  private def sut = new DigitalPaymentFailedResolver(messageSource: messageSource)

    @Test
    void 'resolves text from message source for digital payment failed'() {

        String message = "The amount you are trying to pay is not valid."
        DigitalPaymentFailedReason reason = DigitalPaymentFailedReason.INVALID_AMOUNT
        messageSource.addMessage(codePrefix + reason.name(), locale, message)

        def failure = new DigitalPaymentFailed(reason: reason, message: "")

        assert sut.text(failure, "en").get() == message
    }

    @Test
    void 'resolves default text for digital payment failed'() {
        String message = "digital.payment.error.label.INVALID_AMOUNT"
        DigitalPaymentFailedReason reason = DigitalPaymentFailedReason.INVALID_AMOUNT

        def failure = new DigitalPaymentFailed(reason: reason, message: "")

        assert sut.text(failure, "en").get() == message
    }
}