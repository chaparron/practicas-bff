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
        def expected = "!!button.unknownTextKey!!"
        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "unknownTextKey")

        assert sut.text(paymentButton, "es").get() == expected
    }

    @Test
    void 'resolves message null value'() {
        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "unknownTextKey")

        assert sut.message(paymentButton, "es").get() == null
    }

    @Test
    void 'resolves message default value'() {
        def expected = "!!button.message.unknownMessageKey!!"
        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "unknownTextKey", "unknownMessageKey")

        assert sut.message(paymentButton, "es").get() == expected
    }

    @Test
    void 'resolves message with source value'() {

        String message = "Pay button info"
        def someMessageKey = "someMessageKey"
        messageSource.addMessage("button.message.$someMessageKey", locale, message)
        def paymentButton = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, "unknownTextKey", someMessageKey)

        assert sut.message(paymentButton, locale.language).get() == message
    }

}