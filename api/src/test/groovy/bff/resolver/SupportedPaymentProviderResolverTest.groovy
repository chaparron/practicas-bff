package bff.resolver


import bff.model.SupportedPaymentProvider
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
        def supportedPaymentProvider = SupportedPaymentProvider.jpmMorganBuild()
        String code = titleCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert sut.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves title from message source for Supermoney'() {
        def supportedPaymentProvider = SupportedPaymentProvider.supermoneyBuild()
        String code = titleCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert sut.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for JPMorgan'() {
        def supportedPaymentProvider = SupportedPaymentProvider.jpmMorganBuild()
        String code = descriptionCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "J.P.Morgan description"
        messageSource.addMessage(code, locale, message)

        assert sut.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for Supermoney'() {
        def supportedPaymentProvider = SupportedPaymentProvider.supermoneyBuild()
        String code = descriptionCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "Supermoney description"
        messageSource.addMessage(code, locale, message)

        assert sut.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves default title'() {
        def supportedPaymentProvider = SupportedPaymentProvider.jpmMorganBuild()

        assert sut.title(supportedPaymentProvider, "es").get() == supportedPaymentProvider.configuration.code.name()
    }

    @Test
    void 'resolves poweredByLabel from message source for JPMorgan'() {
        def supportedPaymentProvider = SupportedPaymentProvider.jpmMorganBuild()
        String code = poweredByLabelCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert sut.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves poweredByLabel from message source for Supermoney'() {
        def supportedPaymentProvider = SupportedPaymentProvider.supermoneyBuild()
        String code = poweredByLabelCodePrefix + supportedPaymentProvider.configuration.code.name()
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert sut.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }
}
