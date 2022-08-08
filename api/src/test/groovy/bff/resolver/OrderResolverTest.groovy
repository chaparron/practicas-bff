package bff.resolver

import bff.bridge.OrderBridge
import bff.model.*
import bff.service.MoneyService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import static bff.TestExtensions.anyOrder
import static bff.TestExtensions.anySupplierOrder
import static bff.model.OrderStatus.PENDING
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner)
class OrderResolverTest {
    @Mock
    private OrderBridge orderBridge
    @Mock
    private MoneyService moneyService
    @Mock
    private SupplierOrderResolver supplierOrderResolver

    private OrderResolver sut

    @Before
    void setup() {
        sut = new OrderResolver(
                orderBridge: orderBridge,
                moneyService: moneyService,
                supplierOrderResolver: supplierOrderResolver
        )
    }

    @Test
    void 'Should return PAY_NOW for JPMorgan provider'() {
        def paymentType = PaymentModeType.PAY_NOW
        def providers = [SupportedPaymentProvider.jpmMorganBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder))
                .thenReturn(new SupportedPaymentProviders(providers: providers, paymentMode: new PaymentMode(paymentType: paymentType)))
        testPaymentMode(paymentType, supplierOrders)
    }

    @Test
    void 'Should return PAY_LATER for Supermoney provider'() {
        def paymentType = PaymentModeType.PAY_LATER
        def providers = [SupportedPaymentProvider.supermoneyBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder))
                .thenReturn(new SupportedPaymentProviders(providers: providers, paymentMode: new PaymentMode(paymentType: paymentType)))
        testPaymentMode(paymentType, supplierOrders)
    }

    @Test
    void 'Should return PAY_NOW_OR_LATER for JPMorgan & Supermoney providers'() {
        def providers = [SupportedPaymentProvider.jpmMorganBuild(), SupportedPaymentProvider.supermoneyBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder))
                .thenReturn(new SupportedPaymentProviders(providers: providers, paymentMode: new PaymentMode(paymentType: PaymentModeType.PAY_NOW_OR_LATER)))
        testPaymentMode(PaymentModeType.PAY_NOW_OR_LATER, supplierOrders)
    }


    @Test
    void 'Should return NONE for none supported payment providers'() {
        def paymentType = PaymentModeType.NONE
        def providers = []
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder))
                .thenReturn(new SupportedPaymentProviders(providers: providers, paymentMode: new PaymentMode(paymentType: paymentType)))
        testPaymentMode(paymentType, supplierOrders)
    }

    void testPaymentMode(PaymentModeType paymentModeType, List<SupplierOrder> supplierOrders) {
        def order = anyOrder(PENDING, supplierOrders)
        def expected = paymentModeType
        def result = sut.paymentMode(order)
        assert expected == result.paymentType
        verify(supplierOrderResolver, times(1)).supportedPaymentProviders(supplierOrders.first())
    }
}