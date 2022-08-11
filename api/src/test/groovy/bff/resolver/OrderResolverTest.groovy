package bff.resolver

import bff.bridge.OrderBridge
import bff.model.*
import bff.service.MoneyService
import bff.service.bnpl.BnplProvidersService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import static bff.TestExtensions.anyOrder
import static bff.TestExtensions.anySupplierOrder
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
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([paymentType], supplierOrders, order)
    }

    @Test
    void 'Should return PAY_LATER for Supermoney provider'() {
        def paymentType = PaymentModeType.PAY_LATER
        def providers = [SupportedPaymentProvider.supermoneyBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([paymentType], supplierOrders, order)
    }

    @Test
    void 'Should return both for JPMorgan & Supermoney providers'() {
        def providers = [SupportedPaymentProvider.jpmMorganBuild(), SupportedPaymentProvider.supermoneyBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([PaymentModeType.PAY_NOW, PaymentModeType.PAY_LATER], supplierOrders, order)
    }

    @Test
    void 'Should return empty list for none supported payment providers'() {
        def providers = []
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([], supplierOrders, order)
    }

    @Test
    void 'Should return only one PAY_NOW item for JPMorgan provider despite multiple suppliers supporting it'() {
        def paymentType = PaymentModeType.PAY_NOW
        def providers = [SupportedPaymentProvider.jpmMorganBuild(), SupportedPaymentProvider.jpmMorganBuild()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([paymentType], supplierOrders, order)
    }

    void testPaymentMode(List<PaymentModeType> paymentModeTypes, List<SupplierOrder> supplierOrders, Order order) {
        def expected = []
        paymentModeTypes.forEach {expected.add(new PaymentMode(it)) }
        def result = sut.paymentMode(order)
        assert result.size() == expected.size()
        assert result.containsAll(expected)
        verify(supplierOrderResolver, times(1)).supportedPaymentProviders(supplierOrders.first())
    }
}
