package bff.resolver

import bff.bridge.BnplBridge
import bff.bridge.OrderBridge
import bff.bridge.SupplierOrderBridge
import bff.model.*
import bff.service.MoneyService
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import static bff.TestExtensions.*
import static org.mockito.ArgumentMatchers.any
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner)
class OrderResolverTest {
    @Mock
    private OrderBridge orderBridge
    @Mock
    private MoneyService moneyService
    @Mock
    private SupplierOrderResolver supplierOrderResolver
    @Mock
    private SupplierOrderBridge supplierOrderBridge
    @Mock
    private BnplBridge bnplBridge

    private OrderResolver sut

    @Before
    void setup() {
        sut = new OrderResolver(
                orderBridge,
                supplierOrderBridge,
                moneyService,
                supplierOrderResolver,
                bnplBridge
        )
    }

    @Test
    void 'Should return PAY_NOW for JPMorgan provider'() {
        def paymentType = PaymentModeType.PAY_NOW
        def providers = [new JPMorganMainPaymentProvider()]
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
        def providers = [new SupermoneyPaymentProvider()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([paymentType], supplierOrders, order)
    }

    @Test
    void 'Should return both for JPMorgan & Supermoney providers'() {
        def providers = [new SupermoneyPaymentProvider(), new JPMorganMainPaymentProvider()]
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
        def providers = [new JPMorganMainPaymentProvider(), new JPMorganMainPaymentProvider()]
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        when(supplierOrderResolver.supportedPaymentProviders(supplierOrder)).thenReturn(providers)
        when(orderBridge.getSupplierOrders(order.accessToken, order)).thenReturn(supplierOrders)

        testPaymentMode([paymentType], supplierOrders, order)
    }

    void testPaymentMode(List<PaymentModeType> paymentModeTypes, List<SupplierOrder> supplierOrders, Order order) {
        def expected = []
        paymentModeTypes.forEach { expected.add(new PaymentMode(it)) }
        def result = sut.paymentMode(order)
        assert result.size() == expected.size()
        assert result.containsAll(expected)
        verify(supplierOrderResolver, times(1)).supportedPaymentProviders(supplierOrders.first())
    }

    @Test
    void 'should return null as long as customer has already BnPL support'() {
        def order = givenAnOrderForACustomerWithBNPLSupport()

        def box = sut.payLaterMessageBox(order)

        Assertions.assertThat(box).isNull()
    }

    private Order givenAnOrderForACustomerWithBNPLSupport() {
        doReturn(new BnPlCustomerStatus(1, true)).when(bnplBridge).customerStatus(any())
        def order = anyOrder(OrderStatus.PENDING, [])

        return order
    }

    @Test
    void 'should return payLaterMessageBox as long as customer do not have BnPL support but provider has it'() {
        def order = givenAnOrderWithoutBNPLSupportDueToCustomer()

        def box = sut.payLaterMessageBox(order)

        Assertions.assertThat(box).isEqualTo(new MessageBox("anIcon", "order.bnpl.messageBoxInfo.title", "order.bnpl.messageBoxInfo.description"))
    }

    private Order givenAnOrderWithoutBNPLSupportDueToCustomer() {
        doReturn(new BnPlCustomerStatus(1, false)).when(bnplBridge).customerStatus(any())
        def supplierOrder1 = anySupplierOrder()
        def supplierOrder2 = anySupplierOrder()
        def supplier1 = anySupplier()
        def supplier2 = anySupplier()
        supplierOrder1.id = 1
        supplierOrder2.id = 2
        supplier1.id = 1
        supplier2.id = 2
        def supplierOrders = [supplierOrder1, supplierOrder2]
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)
        doReturn(supplierOrders).when(orderBridge).getSupplierOrders(any(), eq(order))
        doReturn(supplier1).when(supplierOrderBridge).getSupplierBySupplierOrderId(any(), eq(supplierOrder1.id))
        doReturn(supplier2).when(supplierOrderBridge).getSupplierBySupplierOrderId(any(), eq(supplierOrder2.id))
        doReturn(true).when(bnplBridge).isSupplierOnboarded(eq(supplier1.id), any())
        doReturn(false).when(bnplBridge).isSupplierOnboarded(eq(supplier2.id), any())

        return order
    }

    @Test
    void 'should return null as long as neither customer nor provider has BnPL support'() {
        def order = givenAnOrderWithoutBNPLSupportDueToSupplier()

        def box = sut.payLaterMessageBox(order)

        Assertions.assertThat(box).isNull()
    }

    private Order givenAnOrderWithoutBNPLSupportDueToSupplier() {
        doReturn(new BnPlCustomerStatus(1, false)).when(bnplBridge).customerStatus(any())
        def supplierOrder = anySupplierOrder()
        def supplierOrders = [supplierOrder]
        doReturn(anySupplier()).when(supplierOrderBridge).getSupplierBySupplierOrderId(any(), any())
        doReturn(false).when(bnplBridge).isSupplierOnboarded(any(), any())
        def order = anyOrder(OrderStatus.PENDING, supplierOrders)

        return order
    }
}
