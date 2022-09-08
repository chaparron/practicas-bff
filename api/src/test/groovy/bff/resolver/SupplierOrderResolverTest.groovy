package bff.resolver

import bff.bridge.BnplBridge
import bff.bridge.DigitalPaymentsBridge
import bff.bridge.PaymentsBridge
import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.bnpl.BnplProvidersService
import bnpl.sdk.model.SupportedMinimumAmountResponse
import com.google.common.collect.Lists
import digitalpayments.sdk.model.Provider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import wabi2b.payments.common.model.dto.type.PaymentMethod
import wabi2b.payments.common.model.request.GetSupplierOrderPaymentRequest
import wabi2b.payments.common.model.response.GetSupplierOrderPaymentResponse
import wabi2b.payments.common.model.response.SupplierOrderPaymentResponse

import static bff.TestExtensions.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.Mockito.*

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
    @Mock
    private BnplBridge bnplBridge

    private SupplierOrderResolver sut

    private PAYMENT_BUTTON_PREFIX = "payment."

    @Before
    void setup() {
        sut = new SupplierOrderResolver(
                supplierOrderBridge: supplierOrderBridge,
                digitalPaymentsBridge: digitalPaymentsBridge,
                bnplProvidersService: bnplProvidersService,
                paymentsBridge: paymentBridge,
                bnplBridge: bnplBridge
        )
    }

    @Test
    void 'Should return only JPMorgan providers'() {
        def expectedSupportedPaymentProviders = [new JPMorganMainPaymentProvider(), new JPMorganUPIPaymentProvider()]
        def someSupplier = anySupplier()
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
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
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn([])

        def result = sut.supportedPaymentProviders(anySupplierOrder())

        assert expectedSupportedPaymentProviders == result

        verify(bnplProvidersService).creditLineProvidersFor(any())
        verify(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        verify(digitalPaymentsBridge).getPaymentProviders(any(), any())
    }

    @Test
    void 'Should return JPMorgan and Supermoney providers'() {
        def expectedSupportedPaymentProviders =
                [new JPMorganMainPaymentProvider(), new JPMorganUPIPaymentProvider(), new SupermoneyPaymentProvider()]
        def someSupplier = anySupplier()
        def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
        when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(someSupplier)
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
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
        List<Provider> jpMorganPaymentProvider = []
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PAYMENT_BUTTON_PREFIX + PaymentStatus.UNPAID.name())
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PAYMENT_BUTTON_PREFIX + PaymentStatus.PARTIALLY_PAID.name())
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PAYMENT_BUTTON_PREFIX + PaymentStatus.PARTIALLY_PAID.name())
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

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
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn([])
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PAYMENT_BUTTON_PREFIX + PaymentStatus.UNPAID.name())
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
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn([])
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.HIDDEN, PAYMENT_BUTTON_PREFIX + PaymentStatus.PARTIALLY_PAID.name())
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

        def expected = new SimpleTextButton(SimpleTextButtonBehavior.VISIBLE, PAYMENT_BUTTON_PREFIX + PaymentStatus.UNPAID.name())
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
        List<Provider> jpMorganPaymentProvider = [Provider.JP_MORGAN]
        when(digitalPaymentsBridge.getPaymentProviders(any(), any())).thenReturn(jpMorganPaymentProvider)
        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(supplierOrderPayments)

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


        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(response)

        def result = sut.payments(anySupplierOrder)
        assert expected == result
        verify(paymentBridge).getSupplierOrderPayments(request, anySupplierOrder.accessToken)
    }

    @Test
    void 'Should return empty list for supplier order without payments'() {
        def anySupplierOrder = anySupplierOrder()
        def request = new GetSupplierOrderPaymentRequest(anySupplierOrder.id)
        def response = new GetSupplierOrderPaymentResponse(randomBigDecimal(), new BigDecimal("0"), [])

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(response)

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

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(response)

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

        when(paymentBridge.getSupplierOrderPayments(request, anySupplierOrder.accessToken)).thenReturn(response)

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

    private SupplierOrder givenPayableOrder(GetSupplierOrderPaymentResponse paymentResponse,
                                            boolean supplierOnboarded = true,
                                            boolean bnplSupported = true,
                                            BigDecimal minExpected = BigDecimal.TEN,
                                            SupplierOrderStatus orderStatus = SupplierOrderStatus.CONFIRMED) {
        def supplier = anySupplier()
        def supplierOrder = anySupplierOrder()
        supplierOrder.accessToken = validAccessToken()
        def customer = anyCustomerWithIdAndAccessToken("IN")
        def order = anyOrder(OrderStatus.IN_PROGRESS, Lists.newArrayList(supplierOrder))
        order.customer = customer
        supplierOrder.status = orderStatus
        supplierOrder.total = paymentResponse.totalAmount
        when(supplierOrderBridge.getSupplierBySupplierOrderId(any(), any())).thenReturn(supplier)
        when(bnplBridge.supportedMinimumAmount(any(), any())).thenReturn(new SupportedMinimumAmountResponse(minExpected, "IN"))

        if (supplierOrder.isPayable()) {
            when(bnplBridge.isSupplierOnboarded(any(), any())).thenReturn(supplierOnboarded)
            if (supplierOnboarded) {
                when(paymentBridge.getSupplierOrderPayments(any(), any())).thenReturn(paymentResponse)
                if (bnplSupported) {
                    def creditLineProvider = new CreditLineProvider(provider: CreditProvider.SUPERMONEY)
                    when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([creditLineProvider])
                } else {
                    when(bnplProvidersService.creditLineProvidersFor(any())).thenReturn([])
                }
                when(bnplBridge.userBalance(any())).thenReturn(
                        new CreditLines(
                                credits: Lists.newArrayList(
                                        new SuperMoneyCreditLine(
                                                approvedLimit: new Money("INR", BigDecimal.TEN + BigDecimal.TEN),
                                                toRepay: new Money("INR", BigDecimal.TEN),
                                                remaining: new Money("INR", BigDecimal.TEN)
                                        )
                                )
                        )
                )
            }
        }
        return supplierOrder
    }

    @Test
    void 'payLaterButton should return a hidden SimpleTextButton when supplierNotOnboarded'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(40), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, false)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.HIDDEN
    }

    @Test
    void 'payLaterButton should return a hidden SimpleTextButton when status is not confirmed'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(40), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, true, true, BigDecimal.TEN, SupplierOrderStatus.PENDING)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.HIDDEN
    }

    @Test
    void 'payLaterButton should return a hidden SimpleTextButton when bnpl is not supported'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(40), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, true, false, BigDecimal.TEN, SupplierOrderStatus.CONFIRMED)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.HIDDEN
    }

    @Test
    void 'payLaterButton should return a hidden SimpleTextButton bnpl is not applicable'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(1), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, true, true, BigDecimal.TEN, SupplierOrderStatus.CONFIRMED)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.HIDDEN
    }

    @Test
    void 'payLaterButton should return a visible SimpleTextButton when a payableOrder is confirmed'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(40), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, true, true, BigDecimal.TEN, SupplierOrderStatus.CONFIRMED)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.VISIBLE
    }

    @Test
    void 'payLaterButton should return a disabled SimpleTextButton when customer available credit does not reach minimum allowed'() {
        def response = new GetSupplierOrderPaymentResponse(BigDecimal.valueOf(40), new BigDecimal("0"), [])
        def supplierOrder = givenPayableOrder(response, true, true, BigDecimal.valueOf(20), SupplierOrderStatus.CONFIRMED)

        def result = sut.payLaterButton(supplierOrder)
        assert result.behavior == SimpleTextButtonBehavior.DISABLE
    }

}
