package bff.service.bnpl

import bff.bridge.SupplierOrderBridge
import bff.model.*
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.SupportedLimitedAmountResponse
import org.junit.Test
import org.mockito.Mockito
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.CheckSupportedProvidersResponse
import wabi2b.payments.common.model.response.SupplierWalletResponse
import wabi2b.payments.common.model.response.WalletResponse
import wabi2b.payments.sdk.client.WalletSdk

import static bff.model.OrderStatus.*
import static java.math.BigDecimal.ONE
import static java.math.BigDecimal.ZERO
import static java.util.Collections.singletonList
import static org.mockito.Mockito.*

class BnplProvidersServiceTest {

    private final def indianToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6NDQ0NCwidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjo0NDQ0LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJpbiIsIm5hbWUiOiJJbmRpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.E9SeldZQQ6vVE_ayGuE5qS0hxco1DUq8WCDlRLzPC5c"
    private final def russianToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6MjQ1NywidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjoyNDU3LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.XN1Uuy89PYEcxxTSWvrvKe0VH5yPV16clwWl6llx2WM"
    private final def supplier = new Supplier(id: 0000)
    private final def walletProvider = WalletProvider.@Companion.buyNowPayLater()
    private final def bnplSupplierWallet = new SupplierWalletResponse(supplier.id.toString(), "AA11AA", walletProvider.value)
    private final def bnplCustomerWallet = new WalletResponse("4444", "AA22AA", walletProvider.value)
    private final def checkProvidersSupportedResponse = new CheckSupportedProvidersResponse(bnplCustomerWallet, singletonList(bnplSupplierWallet))
    private final def wabiPayWalletProvider = WalletProvider.@Companion.wabiPay()
    private final def wabipaySupplierWallet = new SupplierWalletResponse(supplier.id.toString(), "AA44AA", wabiPayWalletProvider.value)
    private final def wabipayCustomerWallet = new WalletResponse("4444", "AA44AA", wabiPayWalletProvider.value)
    private final def wabiPayCheckProvidersSupportedResponse = new CheckSupportedProvidersResponse(wabipayCustomerWallet, singletonList(wabipaySupplierWallet))

    private final def enabledCountries = ["in"]
    private final def walletSdk = mock(WalletSdk)
    private final def bnplSdk = mock(BnPlSdk)
    private final supplierOrderBridge = mock(SupplierOrderBridge)
    private def sut = new BnplProvidersService(supplierOrderBridge: supplierOrderBridge, enabledCountries: enabledCountries, walletSdk: walletSdk, bnPlSdk: bnplSdk)

    @Test
    void 'bnpl provider is null for not enabled user country by supplier'() {
        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: russianToken)

        def orderSummary = bff.TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])
        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        Mockito.verifyZeroInteractions(walletSdk)
        Mockito.verifyZeroInteractions(bnplSdk)
    }

    @Test
    void 'bnpl provider is null if the amount of the order is less than the limited amount in bnpl'() {
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(ONE, "in")))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = bff.TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])
        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        verify(walletSdk).getWallet(walletProvider, indianToken)
        verify(bnplSdk).supportedLimitedAmount("in", supplierWithAccessToken.accessToken)

    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(null)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(BigDecimal.TEN, "in")))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = bff.TestExtensions.anyOrderSummary(
                new Money("ARS", BigDecimal.TEN),
                supplierWithAccessToken,
                [new Summary(
                        accessToken: supplierWithAccessToken.accessToken,
                        type: CartSummaryItemType.ORDER_TOTAL,
                        value: BigDecimal.TEN,
                        valueMoney: new Money("ARS", BigDecimal.TEN)
                )])

        assert sut.creditLineProvidersFor(orderSummary, new Money("ARS", BigDecimal.TEN)) == null

        verify(walletSdk).getWallet(walletProvider, supplierWithAccessToken.accessToken)
        verify(bnplSdk).supportedLimitedAmount("in", supplierWithAccessToken.accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(wabipayCustomerWallet)
        when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(BigDecimal.TEN, "in")))

        def supplierWithAccessToken = new Supplier(id: 0000, accessToken: indianToken)

        def orderSummary = bff.TestExtensions.anyOrderSummary(
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

        verify(walletSdk).getWallet(walletProvider, supplierWithAccessToken.accessToken)
        verify(walletSdk).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), supplierWithAccessToken.accessToken)
        verify(bnplSdk).supportedLimitedAmount("in", supplierWithAccessToken.accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for not enabled user country by supplier and order'() {
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(PENDING), accessToken: russianToken)
        assert sut.creditLineProvidersFor(supplierOrder) == null

        Mockito.verifyZeroInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for finished order'() {
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(FINISHED), accessToken: indianToken, payment_pending: ZERO)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(supplierOrder.payment_pending, "in")))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        Mockito.verifyZeroInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier and order'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(null)
        when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(PENDING), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(supplierOrder.payment_pending, "in")))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        verify(walletSdk).getWallet(walletProvider, indianToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and pending order'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(PENDING), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(supplierOrder.payment_pending, "in")))

        assert sut.creditLineProvidersFor(supplierOrder) == [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        verify(walletSdk).getWallet(walletProvider, indianToken)
        verify(walletSdk).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)

        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null if the supplier has not bnpl wallet'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new CheckSupportedProvidersResponse(bnplCustomerWallet, Collections.emptyList()))
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(PENDING), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(supplierOrder.payment_pending, "in")))

        assert sut.creditLineProvidersFor(supplierOrder) == null

        verify(walletSdk).getWallet(walletProvider, indianToken)
        verify(walletSdk).getSupportedProvidersBetween(singletonList(supplier.id.toString()), wabipayCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)

        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and in progress order'() {
        when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(bnplCustomerWallet)
        when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(checkProvidersSupportedResponse)
        def supplierOrder = new SupplierOrder(id: 1L, order: anyOrder(IN_PROGRESS), accessToken: indianToken, payment_pending: ONE)
        when(supplierOrderBridge.getOrderBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplierOrder.order)
        when(supplierOrderBridge.getSupplierBySupplierOrderId(indianToken, supplierOrder.id)).thenReturn(supplier)
        when(bnplSdk.supportedLimitedAmount(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new SupportedLimitedAmountResponse(supplierOrder.payment_pending, "in")))

        assert sut.creditLineProvidersFor(supplierOrder) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        verify(walletSdk).getWallet(walletProvider, indianToken)
        verify(walletSdk).getSupportedProvidersBetween(singletonList(supplier.id.toString()), bnplCustomerWallet.userId, WalletProvider.@Companion.buyNowPayLater(), indianToken)
        verifyNoMoreInteractions(walletSdk)
    }

    private final Order anyOrder(OrderStatus status) {
        new Order(id: 007, status: status)
    }
}
