package bff.resolver

import bff.bridge.DigitalPaymentsBridge
import bff.bridge.PaymentsBridge
import bff.bridge.SupplierOrderBridge
import bff.model.DebitCard
import bff.model.NetBanking
import bff.model.CreditCard
import bff.model.CreditLineProvider
import bff.model.CreditProvider
import bff.model.DefaultPaymentMethod
import bff.model.DigitalPaymentPaymentData
import bff.model.DigitalWallet
import bff.model.JPMorganPaymentProvider
import bff.model.PaymentStatus
import bff.model.SimpleTextButton
import bff.model.SimpleTextButtonBehavior
import bff.model.SupermoneyPaymentProvider
import bff.model.SupplierOrderPaymentV2
import bff.model.SupplierOrderStatus
import bff.model.UPI
import bff.service.bnpl.BnplProvidersService
import digitalpayments.sdk.model.Provider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.dto.type.PaymentMethod
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse
import wabi2b.payments.common.model.response.SupplierOrderPaymentResponse

import static bff.TestExtensions.anySupplierOrder
import static bff.TestExtensions.randomLong
import static bff.TestExtensions.randomBigDecimal
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.verify
import static org.mockito.Mockito.when
import static org.mockito.Mockito.times
import static bff.TestExtensions.anySupplier

@RunWith(MockitoJUnitRunner)
class SupplierOrderResolverTest {

    @Mock
    private SupplierOrderBridge supplierOrderBridge
    @Mock
    private DigitalPaymentsBridge digitalPaymentsBridge
    @Mock
    private BnplProvidersService bnplProvidersService
    @Mock
    private PaymentsBridge paymentBridge

    private SupplierOrderResolver sut

    @Before
    void setup() {
        sut = new SupplierOrderResolver(
                supplierOrderBridge: supplierOrderBridge,
                digitalPaymentsBridge: digitalPaymentsBridge,
                bnplProvidersService: bnplProvidersService,
                paymentsBridge: paymentBridge
        )
    }

    @Test
    void 'Should return only JPMorgan provider'() {
        def expectedSupportedPaymentProviders = [new JPMorganPaymentProvider()]
        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsBridge).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return only Supermoney provider'() {
        def expectedSupportedPaymentProviders = [new SupermoneyPaymentProvider()]
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(Mono.just([]))

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsBridge).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return JPMorgan and Supermoney providers'() {
        def expectedSupportedPaymentProviders =
                [new JPMorganPaymentProvider(), new SupermoneyPaymentProvider()]
        def someSupplier = anySupplier()
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsBridge).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return empty list for none supported providers'() {
        def expectedSupportedPaymentProviders = []
        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn(null)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsBridge).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return payment button for JPMorgan and Supermoney supported providers for unpaid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("0"), [])
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)


        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PaymentStatus.UNPAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for JPMorgan and Supermoney supported providers for partially paid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("50"), somePayments)
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)


        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PaymentStatus.PARTIALLY_PAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for JPMorgan supported provider for partially paid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("50"), somePayments)
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)


        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PaymentStatus.PARTIALLY_PAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for totally paid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("100"), somePayments)
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)

        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.HIDDEN, PaymentStatus.TOTALLY_PAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for Supermoney supported provider for unpaid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("0"), [])
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)


        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(Mono.just([]))
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PaymentStatus.UNPAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for Supermoney supported provider for partially paid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
        ]
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("10"), somePayments)
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)

        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(Mono.just([]))
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.HIDDEN, PaymentStatus.PARTIALLY_PAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for JPMorgan supported provider for unpaid confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.CONFIRMED)
        def someSupplier = anySupplier()
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("0"), [])
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)

        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PaymentStatus.UNPAID.name())
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payment button for not confirmed supplier order'() {
        def anySupplierOrder = anySupplierOrder(SupplierOrderStatus.PENDING)
        def someSupplier = anySupplier()
        def supplierOrderPayments = new GetSupplierOrderPaymentResponse(new BigDecimal("100"), new BigDecimal("0"), [])
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)

        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        Mono<List<Provider>> jpMorganPaymentProvider = Mono.just([Provider.JP_MORGAN])
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(supplierOrderPayments))

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.HIDDEN, "")
        def result = sut.paymentButton(anySupplierOrder)
        assert expected == result
    }

    @Test
    void 'Should return payments for a supplier order'() {
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def anySupplierOrder = anySupplierOrder()
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)
        def response = new GetSupplierOrderPaymentResponse(randomBigDecimal(), randomBigDecimal(), somePayments)
        def expected = somePayments.collect {
            new SupplierOrderPaymentV2(
                    supplierOrderId: anySupplierOrder.id,
                    paymentId: it.paymentId,
                    paymentData: new DigitalPaymentPaymentData(paymentMethod: getPaymentMethod(it.paymentMethod)))
        }


        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(response))

        def result = sut.payments(anySupplierOrder)
        assert expected == result
        verify(paymentBridge).getSupplierOrderPayments(request, anySupplierOrder.accessToken)
    }

    @Test
    void 'Should return empty list for supplier order without payments'() {
        def anySupplierOrder = anySupplierOrder()
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)
        def response = new GetSupplierOrderPaymentResponse(randomBigDecimal(), new BigDecimal("0"), [])

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(response))

        def result = sut.payments(anySupplierOrder)

        assert [] == result
        verify(paymentBridge, times(1)).getSupplierOrderPayments(request, anySupplierOrder.accessToken)
    }

    @Test
    void 'Should return default payment amount for partially paid supplier order'() {
        def somePayments = [
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.CREDIT_CARD),
                new SupplierOrderPaymentResponse(randomLong(), PaymentMethod.UPI)
        ]
        def anySupplierOrder = anySupplierOrder()
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)
        def response = new GetSupplierOrderPaymentResponse(randomBigDecimal(), randomBigDecimal(), somePayments)
        def expected = response.totalAmount - response.lockedAmount

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(response))

        def result = sut.defaultPaymentAmount(anySupplierOrder)
        assert expected == result
        verify(paymentBridge, times(1)).getSupplierOrderPayments(request, anySupplierOrder.accessToken)
    }

    @Test
    void 'Should return default payment amount for unpaid supplier order'() {
        def anySupplierOrder = anySupplierOrder()
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)
        def response = new GetSupplierOrderPaymentResponse(randomBigDecimal(), new BigDecimal("0"), [])
        def expected = response.totalAmount - response.lockedAmount

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(Mono.just(response))

        def result = sut.defaultPaymentAmount(anySupplierOrder)
        assert expected == result
        verify(paymentBridge, times(1)).getSupplierOrderPayments(request, anySupplierOrder.accessToken)
    }

    private static bff.model.PaymentMethod getPaymentMethod(PaymentMethod dtoPaymentMethod) {
        switch (dtoPaymentMethod) {
            case PaymentMethod.NET_BANKING: new NetBanking()
                break
            case PaymentMethod.UPI: new UPI()
                break
            case PaymentMethod.CREDIT_CARD: new CreditCard()
                break
            case PaymentMethod.DIGITAL_WALLET: new DigitalWallet()
                break
            case PaymentMethod.DEBIT_CARD: new DebitCard()
                break
            default: new DefaultPaymentMethod()
        }
    }
}
