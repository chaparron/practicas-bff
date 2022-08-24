package bff.resolver

import bff.model.JPMorganPaymentProvider
import bff.model.SupermoneyPaymentProvider
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class SupportedPaymentProviderResolverTest {

    private final def titleCodePrefix = "payment.provider.title."
    private final def descriptionCodePrefix = "payment.provider.description."
    private final def poweredByLabelCodePrefix = "payment.provider.poweredByLabel."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new SupportedPaymentProviderResolver(messageSource: messageSource)

    @Test
    void 'resolves title from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganPaymentProvider()
        String code = titleCodePrefix + "JPMorganPaymentProvider"
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert sut.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves title from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        String code = titleCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert sut.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganPaymentProvider()
        String code = descriptionCodePrefix + "JPMorganPaymentProvider"
        String message = "J.P.Morgan description"
        messageSource.addMessage(code, locale, message)

        assert sut.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        String code = descriptionCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney description"
        messageSource.addMessage(code, locale, message)

        assert sut.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves default title'() {
        def supportedPaymentProvider = new JPMorganPaymentProvider()

        assert sut.title(supportedPaymentProvider, "es").get() == "payment.provider.title"
    }

    @Test
    void 'resolves poweredByLabel from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganPaymentProvider()
        String code = poweredByLabelCodePrefix + "JPMorganPaymentProvider"
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert sut.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves poweredByLabel from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        String code = poweredByLabelCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert sut.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }
}
