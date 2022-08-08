package bff.resolver

import bff.bridge.SupplierOrderBridge
import bff.model.CreditLineProvider
import bff.model.CreditProvider
import bff.model.PaymentMode
import bff.model.PaymentModeType
import bff.model.SupportedPaymentProvider
import bff.model.SupportedPaymentProviders
import bff.service.bnpl.BnplProvidersService
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.Provider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono

import static bff.TestExtensions.anySupplierOrder
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static bff.TestExtensions.anySupplier

@RunWith(MockitoJUnitRunner)
class SupplierOrderResolverTest {

    @Mock
    private SupplierOrderBridge supplierOrderBridge
    @Mock
    private DigitalPaymentsSdk digitalPaymentsSdk
    @Mock
    private BnplProvidersService bnplProvidersService

    private SupplierOrderResolver sut


    @Before
    void setup() {
        sut = new SupplierOrderResolver(
                supplierOrderBridge: supplierOrderBridge,
                digitalPaymentsSdk: digitalPaymentsSdk,
                bnplProvidersService: bnplProvidersService
        )
    }

    @Test
    void 'Should return only JPMorgan provider'() {
        def expectedSupportedPaymentProviders = new SupportedPaymentProviders(
                providers: [SupportedPaymentProvider.jpmMorganBuild()],
                paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_NOW)
        )

        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsSdk.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsSdk).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return only Supermoney provider'() {
        def expectedSupportedPaymentProviders = new SupportedPaymentProviders(
                providers: [SupportedPaymentProvider.supermoneyBuild()],
                paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_LATER)
        )

        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        when(digitalPaymentsSdk.getPaymentProviders(any(), any())).thenReturn(Mono.just([]))

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsSdk).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return JPMorgan and Supermoney providers'() {
        def expectedSupportedPaymentProviders = new SupportedPaymentProviders(
                providers: [SupportedPaymentProvider.jpmMorganBuild(), SupportedPaymentProvider.supermoneyBuild()],
                paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_NOW_OR_LATER)
        )

        def someSupplier = anySupplier()
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsSdk.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsSdk).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return empty list for none supported providers'() {
        def expectedSupportedPaymentProviders = new SupportedPaymentProviders(
                providers: [],
                paymentMode: new PaymentMode(paymentType: PaymentModeType.NONE)
        )

        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([])
        when(digitalPaymentsSdk.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsSdk).getPaymentProviders(any(), any())
    }
}
