package bff.service.bnpl

import bff.TestExtensions
import bff.bridge.BnplBridge
import bff.bridge.OrderBridge
import bff.bridge.SupplierOrderBridge
import bff.bridge.WalletBridge
import bff.model.*
import bnpl.sdk.model.SupportedMinimumAmountResponse
import org.junit.Test
import org.mockito.Mockito
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.CheckSupportedProvidersResponse
import wabi2b.payments.common.model.response.SupplierWalletResponse
import wabi2b.payments.common.model.response.WalletResponse

import static bff.model.OrderStatus.*
import static java.math.BigDecimal.ONE
import static java.math.BigDecimal.ZERO
import static java.util.Collections.emptyList
import static java.util.Collections.singletonList
import static org.mockito.Mockito.*

class BnplProvidersServiceTest {

    private final def indianToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6NDQ0NCwidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjo0NDQ0LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJpbiIsIm5hbWUiOiJJbmRpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.E9SeldZQQ6vVE_ayGuE5qS0hxco1DUq8WCDlRLzPC5c"
    private final def indianUserId = 4444L
    private final def russianToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6MjQ1NywidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjoyNDU3LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.XN1Uuy89PYEcxxTSWvrvKe0VH5yPV16clwWl6llx2WM"
    private final def russianUserId = 2457L
    private final def supplier = new Supplier(id: 0000)
    private final def walletProvider = WalletProvider.@Companion.buyNowPayLater()
    private final def bnplSupplierWallet = new SupplierWalletResponse(supplier.id.toString(), "AA11AA", walletProvider.value)
    private final def bnplCustomerWallet = new WalletResponse("4444", "AA22AA", walletProvider.value)
    private final def checkProvidersSupportedResponse = new CheckSupportedProvidersResponse(bnplCustomerWallet, singletonList(bnplSupplierWallet))
    private final def wabiPayWalletProvider = WalletProvider.@Companion.wabiPay()
    private final def wabipayCustomerWallet = new WalletResponse("4444", "AA44AA", wabiPayWalletProvider.value)
    private final def enabledCountries = ["in"]
    private final def walletBridge = mock(WalletBridge)
    private final def bnplBridge = mock(BnplBridge)
    private final supplierOrderBridge = mock(SupplierOrderBridge)
    private final orderBridge = mock(OrderBridge)
    private def sut = new BnplProvidersService(
            supplierOrderBridge: supplierOrderBridge,
            enabledCountries: enabledCountries,
            walletBridge: walletBridge,
            bnplBridge: bnplBridge,
            orderBridge: orderBridge
    )

    @Test
    void 'bnpl provider is null for not enabled user country by supplier'() {
        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: russianToken)

        def orderSummary = TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])
        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        verifyZeroInteractions(walletBridge)
        verifyZeroInteractions(bnplBridge)
    }

    @Test
    void 'bnpl provider is null if the amount of the order is less than the minimum amount in bnpl'() {
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(ONE, "in"))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])
        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        verify(walletBridge).getWallet(4444L, walletProvider, indianToken)
        verify(bnplBridge).supportedMinimumAmount("in", supplierWithAccessToken.accessToken)

    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null)
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(BigDecimal.TEN, "in"))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])

        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        verify(walletBridge).getWallet(indianUserId, walletProvider, supplierWithAccessToken.accessToken)
        verify(bnplBridge).supportedMinimumAmount("in", supplierWithAccessToken.accessToken)
        verifyNoMoreInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(wabipayCustomerWallet)
        when(walletBridge.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(BigDecimal.TEN, "in"))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])
        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        verify(walletBridge).getWallet(indianUserId, walletProvider, supplierWithAccessToken.accessToken)
        verify(walletBridge).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), supplierWithAccessToken.accessToken)
        verify(bnplBridge).supportedMinimumAmount("in", supplierWithAccessToken.accessToken)
        verifyNoMoreInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is null for not enabled user country by supplier and order'() {
        def supplierOrder = new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(PENDING, emptyList()), accessToken: russianToken)
        def supplier = new Supplier(id: 2L)
        when(supplierOrderBridge.getOrderBySupplierOrderId(russianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(russianToken, supplierOrder.id)).thenReturn(supplier)
        assert sut.creditLineProvidersFor(supplierOrder) == null

        verifyZeroInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is null for finished order'() {
        def supplierOrder = new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(FINISHED, emptyList()), accessToken: indianToken, payment_pending: ZERO)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(supplierOrder.payment_pending, "in"))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        verifyZeroInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier and order'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(null)
        when(walletBridge.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order:TestExtensions. anyOrder(PENDING, emptyList()), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(supplierOrder.payment_pending, "in"))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        verify(walletBridge).getWallet(indianUserId, walletProvider, indianToken)
        verifyNoMoreInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and pending order'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletBridge.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(PENDING,
                [new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(PENDING, emptyList()), accessToken: indianToken, payment_pending: ONE)]), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(orderBridge.getSupplierOrders(indianToken, supplierOrder.order.id)).thenReturn(singletonList(supplierOrder))
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(supplierOrder.payment_pending, "in"))

        assert sut.creditLineProvidersFor(supplierOrder) == [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        verify(walletBridge).getWallet(indianUserId, walletProvider, indianToken)
        verify(walletBridge).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)

        verifyNoMoreInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is null if the supplier has not bnpl wallet'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletBridge.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new CheckSupportedProvidersResponse(bnplCustomerWallet, emptyList()))
        def supplierOrder = new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(PENDING,
                [new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(PENDING, emptyList()), accessToken: indianToken, payment_pending: ONE)]), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(orderBridge.getSupplierOrders(indianToken, supplierOrder.order.id)).thenReturn(singletonList(supplierOrder))
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(supplierOrder.payment_pending, "in"))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        verify(walletBridge).getWallet(indianUserId, walletProvider, indianToken)
        verify(walletBridge).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)

        verifyNoMoreInteractions(walletBridge)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and in progress order'() {
        when(walletBridge.getWallet(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletBridge.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(IN_PROGRESS,
                [new SupplierOrder(id: 1L, order: TestExtensions.anyOrder(IN_PROGRESS, []), accessToken: indianToken, payment_pending: ONE)]),
                accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(orderBridge.getSupplierOrders(indianToken, supplierOrder.order.id)).thenReturn(singletonList(supplierOrder))
        when(bnplBridge.supportedMinimumAmount(Mockito.any(), Mockito.any())).thenReturn(new SupportedMinimumAmountResponse(supplierOrder.payment_pending, "in"))

        assert sut.creditLineProvidersFor(supplierOrder) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        verify(walletBridge).getWallet(indianUserId, walletProvider, indianToken)
        verify(walletBridge).getSupportedProvidersBetween(singletonList(supplier.id.toString()), bnplCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)
        verifyNoMoreInteractions(walletBridge)
    }

}
