package bff.resolver

import bff.model.SimpleTextButton
import bff.model.SimpleTextButtonBehavior
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class SimpleTextButtonResolverTest {

    private final def codePrefix = "button."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new SimpleTextButtonResolver(messageSource: messageSource)


    @Test
    void 'resolves text from message source for simple text button'() {

        String message = "Pay"
        messageSource.addMessage(codePrefix + "someTextKey", locale, message)

        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "someTextKey")

        assert sut.text(paymentButton, "en").get() == message
    }

    @Test
    void 'resolves text default value'() {
        def expected = "button.key"
        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "unknownTextKey")

        assert sut.text(paymentButton, "es").get() == expected
    }

}