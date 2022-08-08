package bff.resolver

import bff.model.CreditLineProvider
import bff.model.CreditProvider
import org.junit.Before
import org.junit.Test
import org.springframework.context.support.StaticMessageSource

class CreditLineProviderResolverTest {

    private final def code = "bnpl.creditProvider.poweredBy.label"
    private final def poweredByMessage = "Powered by {0}"
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private def sut = new CreditLineProviderResolver(messageSource: messageSource)

    @Before
    void setup() {
        messageSource.addMessage(code, locale, poweredByMessage)
    }

    @Test
    void 'resolves poweredByLabel from message source'() {
        def provider = CreditProvider.SUPERMONEY
        def creditLineProvider = new CreditLineProvider(provider: provider)

        assert sut.poweredByLabel(creditLineProvider, "en").get() == "Powered by $provider.poweredBy"
    }

    @Test
    void 'resolves poweredByLabel from given default value'() {
        def provider = CreditProvider.SUPERMONEY
        def creditLineProvider = new CreditLineProvider(provider: provider)

        assert sut.poweredByLabel(creditLineProvider, "es").get() == provider.poweredBy
    }
}
