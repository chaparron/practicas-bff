package bff.resolver

import bff.model.InstantPaymentProvider
import bff.model.PaymentProviderCode
import bff.service.PaymentProviderTranslationService
import org.junit.Test
import org.mockito.Mockito

import static org.mockito.Mockito.*

class InstantPaymentProviderResolverTest {

    private final def poweredByMessage = "Powered by JPMORGAN"
    private def paymentProviderTranslationService = mock(PaymentProviderTranslationService)
    private def instantPaymentProvider = new InstantPaymentProvider(providerCode: PaymentProviderCode.JPMORGAN)

    private def sut = new InstantPaymentProviderResolver(paymentProviderTranslationService: paymentProviderTranslationService)

    @Test
    void 'resolves poweredByLabel from using paymentProviderTranslationService'() {
        when(paymentProviderTranslationService.poweredByLabel(Mockito.any(), Mockito.any())).thenReturn(poweredByMessage)

        assert sut.poweredByLabel(instantPaymentProvider, "en").get() == poweredByMessage

        verify(paymentProviderTranslationService).poweredByLabel(instantPaymentProvider, "en")
    }
}
