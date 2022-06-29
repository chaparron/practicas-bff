package bff.service.bnpl

import bff.model.CreditLineProvider
import bff.model.CreditProvider
import bff.model.Order
import bff.model.OrderStatus
import bff.model.Supplier
import org.junit.Test
import org.mockito.Mockito
import reactor.core.publisher.Mono
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.WalletResponse
import wabi2b.payments.sdk.client.WalletSdk

import static bff.model.OrderStatus.*

class BnplProvidersServiceTest {

    private final def indianToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6NDQ0NCwidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjo0NDQ0LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJpbiIsIm5hbWUiOiJJbmRpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.E9SeldZQQ6vVE_ayGuE5qS0hxco1DUq8WCDlRLzPC5c"
    private final def anotherToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb21hbi1wcm9rQHlhbmRleC5ydSIsInNjb3BlIjpbImFsbCJdLCJ0b3MiOnsidXNlciI6eyJpZCI6MjQ1NywidXNlcm5hbWUiOm51bGwsImZpcnN0TmFtZSI6bnVsbCwibGFzdE5hbWUiOm51bGwsInBob25lIjpudWxsLCJjcmVkZW50aWFscyI6bnVsbCwicHJvZmlsZXMiOm51bGwsImNvdW50cmllcyI6bnVsbCwiY3JlYXRlZCI6bnVsbCwiYWNjZXB0V2hhdHNBcHAiOnRydWV9LCJhY2NlcHRlZCI6MTYxMzgwOTkwOTAwMH0sImVudGl0eUlkIjoiMTU1ODUiLCJzdGF0ZSI6bnVsbCwiZXhwIjoxNjIwOTU3NTE3LCJ1c2VyIjp7ImlkIjoyNDU3LCJ1c2VybmFtZSI6InJvbWFuLXByb2tAeWFuZGV4LnJ1IiwicHJvZmlsZXMiOlt7ImlkIjo4LCJuYW1lIjoiRkVfQ1VTVE9NRVIiLCJhdXRob3JpdGllcyI6bnVsbH1dLCJmaXJzdE5hbWUiOiLQotC10YHRgiIsImxhc3ROYW1lIjoi0KLQtdGB0YLQvtCy0YvQuSIsImNvdW50cmllcyI6W3siaWQiOiJydSIsIm5hbWUiOiJSdXNpYSJ9XX0sImF1dGhvcml0aWVzIjpbIkZFX1dFQiJdLCJqdGkiOiI0YTA2YjU0MS1hODhhLTRkMTMtODU4MS1kYjc1OTAzNWIxZGEiLCJjbGllbnRfaWQiOiJpbnRlcm5hbF9hcGkifQ.XN1Uuy89PYEcxxTSWvrvKe0VH5yPV16clwWl6llx2WM"
    private final def supplier = new Supplier(id: 0000)
    private final def walletProvider = WalletProvider.@Companion.buyNowPayLater()
    private final def wallet = new WalletResponse("4444", "AA44AA", walletProvider.value)

    private def enabledCountries = ["in"]
    private def walletSdk = Mockito.mock(WalletSdk)
    private def sut = new BnplProvidersService(enabledCountries: enabledCountries, walletSdk: walletSdk)

    @Test
    void 'bnpl provider is null for not enabled user country by supplier'() {
        assert sut.creditLineProvidersFor(supplier, anotherToken) == null

        Mockito.verifyZeroInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier'() {
        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(Mono.empty())

        assert sut.creditLineProvidersFor(supplier, indianToken) == null

        Mockito.verify(walletSdk).getWallet(walletProvider, indianToken)
        Mockito.verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier'() {
        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(Mono.just(wallet))

        assert sut.creditLineProvidersFor(supplier, indianToken) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        Mockito.verify(walletSdk).getWallet(walletProvider, indianToken)
        Mockito.verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for not enabled user country by supplier and order'() {
        assert sut.creditLineProvidersFor(supplier, anyOrder(PENDING), anotherToken) == null

        Mockito.verifyZeroInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for finished order'() {
        assert sut.creditLineProvidersFor(supplier, anyOrder(FINISHED), indianToken) == null

        Mockito.verifyZeroInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is null for user without wallet by supplier and order'() {
        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(Mono.empty())

        assert sut.creditLineProvidersFor(supplier, anyOrder(PENDING), indianToken) == null

        Mockito.verify(walletSdk).getWallet(walletProvider, indianToken)
        Mockito.verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and pending order'() {
        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(Mono.just(wallet))

        assert sut.creditLineProvidersFor(supplier, anyOrder(PENDING), indianToken) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        Mockito.verify(walletSdk).getWallet(walletProvider, indianToken)
        Mockito.verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'bnpl provider is supermoney for user with wallet by supplier and in progress order'() {
        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(Mono.just(wallet))

        assert sut.creditLineProvidersFor(supplier, anyOrder(IN_PROGRESS), indianToken) ==
                [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)]

        Mockito.verify(walletSdk).getWallet(walletProvider, indianToken)
        Mockito.verifyNoMoreInteractions(walletSdk)
    }

    private final Order anyOrder(OrderStatus status) {
        new Order(id: 007, status: status)
    }
}