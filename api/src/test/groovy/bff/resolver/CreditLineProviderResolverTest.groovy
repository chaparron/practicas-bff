package bff.resolver

import bff.service.PaymentProviderTranslationService
import org.junit.Test
import org.mockito.Mockito

import static bff.model.CreditLineProvider.buildSuperMoneyCreditLineProvider
import static org.mockito.Mockito.*

class CreditLineProviderResolverTest {

    private final def poweredByMessage = "Powered by SUPERMONEY"
    private def paymentProviderTranslationService = mock(PaymentProviderTranslationService)
    private def creditLineProvider = buildSuperMoneyCreditLineProvider()

    private def sut = new CreditLineProviderResolver(paymentProviderTranslationService: paymentProviderTranslationService)

    @Test
    void 'resolves poweredByLabel from using paymentProviderTranslationService'() {
        when(paymentProviderTranslationService.poweredByLabel(Mockito.any(), Mockito.any())).thenReturn(poweredByMessage)

        assert sut.poweredByLabel(creditLineProvider, "en").get() == poweredByMessage

        verify(paymentProviderTranslationService).poweredByLabel(creditLineProvider, "en")
    }
}
