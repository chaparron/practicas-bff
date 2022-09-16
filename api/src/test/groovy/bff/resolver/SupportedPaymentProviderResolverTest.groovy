package bff.resolver

import bff.model.JPMorganMainPaymentProvider
import bff.model.PaymentProviderLogoSize
import bff.model.SupermoneyPaymentProvider
import bff.service.ImageService
import org.junit.Test
import org.springframework.context.support.StaticMessageSource
import org.springframework.test.util.ReflectionTestUtils

class SupportedPaymentProviderResolverTest {

    private final def titleCodePrefix = "payment.provider.title."
    private final def descriptionCodePrefix = "payment.provider.description."
    private final def poweredByLabelCodePrefix = "payment.provider.poweredByLabel."
    private final def locale = Locale.forLanguageTag("en")
    private def messageSource = new StaticMessageSource()
    private ImageService imageService = new ImageService()

    @Test
    void 'resolves title from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganMainPaymentProvider()
        def resolver = new JPMorganMainPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = titleCodePrefix + "JPMorganMainPaymentProvider"
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert resolver.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves title from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        def resolver = new SupermoneyPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = titleCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert resolver.title(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganMainPaymentProvider()
        def resolver = new JPMorganMainPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = descriptionCodePrefix + "JPMorganMainPaymentProvider"
        String message = "J.P.Morgan description"
        messageSource.addMessage(code, locale, message)

        assert resolver.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves description from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        def resolver = new SupermoneyPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = descriptionCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney description"
        messageSource.addMessage(code, locale, message)

        assert resolver.description(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves default title'() {
        def supportedPaymentProvider = new JPMorganMainPaymentProvider()
        def resolver = new JPMorganMainPaymentProviderResolver(messageSource: messageSource, imageService: imageService)

        assert resolver.title(supportedPaymentProvider, "es").get() == "payment.provider.title"
    }

    @Test
    void 'resolves poweredByLabel from message source for JPMorgan'() {
        def supportedPaymentProvider = new JPMorganMainPaymentProvider()
        def resolver = new JPMorganMainPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = poweredByLabelCodePrefix + "JPMorganMainPaymentProvider"
        String message = "J.P.Morgan"
        messageSource.addMessage(code, locale, message)

        assert resolver.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'resolves poweredByLabel from message source for Supermoney'() {
        def supportedPaymentProvider = new SupermoneyPaymentProvider()
        def resolver = new SupermoneyPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        String code = poweredByLabelCodePrefix + "SupermoneyPaymentProvider"
        String message = "Supermoney"
        messageSource.addMessage(code, locale, message)

        assert resolver.poweredByLabel(supportedPaymentProvider, "en").get() == message
    }

    @Test
    void 'given an imageId and a image size, return the correct url'(){
        //Given
        def resolver = new SupermoneyPaymentProviderResolver(messageSource: messageSource, imageService: imageService)
        ReflectionTestUtils.setField(imageService, "resizerUrl", "https://resizer-qa.wabi2b.com/")
        ReflectionTestUtils.setField(resolver, "upiLogoId", "b47a777d-e6a4-4538-94f7-d7fe5d9ed38a.png")
        ReflectionTestUtils.setField(resolver, "dpLogoId", "6da1c9dc-cd9b-414a-b5b9-097945537b97a.png")
        ReflectionTestUtils.setField(resolver, "bnplLogoId", "1bfd3538-1bd6-4f03-aba0-282c4b249ea6.png")
        URI expectedUrl = "https://resizer-qa.wabi2b.com/fit-in/30x20/1bfd3538-1bd6-4f03-aba0-282c4b249ea6.png".toURI()

        //When
        URI resultUrl = resolver.logo(new SupermoneyPaymentProvider(), PaymentProviderLogoSize.SIZE_30x20)

        //Then
        assert resultUrl == expectedUrl
    }

}
