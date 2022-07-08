package bff.bridge

import bff.TestExtensions
import bff.bridge.http.WalletBridgeImpl
import bff.configuration.CacheConfigurationProperties
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.CheckSupportedProvidersResponse
import wabi2b.payments.sdk.client.WalletSdk

import static org.mockito.Mockito.verify
import static org.mockito.Mockito.verifyNoMoreInteractions

@RunWith(MockitoJUnitRunner.class)
class WalletBridgeImplTest {

    @Mock
    CacheConfigurationProperties cacheConfiguration

    @Mock
    WalletSdk walletSdk

    @InjectMocks
    private WalletBridgeImpl walletBridge = new WalletBridgeImpl()

    @Before
    void init() {
        Mockito.when(cacheConfiguration.wallets).thenReturn(1L)
        walletBridge.init()
    }

    @Test
    void 'should return parsed WalletResponse from cached response'() {
        def walletProvider = WalletProvider.@Companion.buyNowPayLater()
        def accessToken = "token"
        def userId = 1L
        def expected = TestExtensions.anyWalletResponse(userId.toString(), "walletId", walletProvider)
        def anotherToken = UUID.randomUUID().toString()

        Mockito.when(walletSdk.getWallet(Mockito.any(), Mockito.any())).thenReturn(expected)

        assert walletBridge.getWallet(userId, walletProvider, accessToken) == expected
        assert walletBridge.getWallet(userId, walletProvider, accessToken) == expected
        assert walletBridge.getWallet(userId, walletProvider, anotherToken) == expected

        verify(walletSdk).getWallet(walletProvider, accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'should return parsed WalletResponse from uncached response'() {
        def bnplProvider = WalletProvider.@Companion.buyNowPayLater()
        def wabipayProvider = WalletProvider.@Companion.wabiPay()
        def accessToken = "token"
        def userId = 1L
        def firstExpected = TestExtensions.anyWalletResponse(userId.toString(), "walletId", bnplProvider)
        def secondExpected = TestExtensions.anyWalletResponse(userId.toString(), "walletId", wabipayProvider)

        Mockito.when(walletSdk.getWallet(bnplProvider, accessToken)).thenReturn(firstExpected)
        Mockito.when(walletSdk.getWallet(wabipayProvider, accessToken)).thenReturn(secondExpected)

        assert walletBridge.getWallet(userId, bnplProvider, accessToken) == firstExpected
        assert walletBridge.getWallet(userId, wabipayProvider, accessToken) == secondExpected

        verify(walletSdk).getWallet(bnplProvider, accessToken)
        verify(walletSdk).getWallet(wabipayProvider, accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'should return parsed CheckSupportedProvidersResponse from cached response'() {
        def accessToken = "token"
        def bnplProvider = WalletProvider.@Companion.buyNowPayLater()
        def supplierWalletResponse = TestExtensions.anySupplierWalletResponse("supplierId", "walletId", bnplProvider)
        def walletResponse = TestExtensions.anyWalletResponse("userId", "walletId", bnplProvider)

        def expected = new CheckSupportedProvidersResponse(walletResponse, [supplierWalletResponse])

        Mockito.when(walletSdk.getSupportedProvidersBetween(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(expected)

        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken) == expected
        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken) == expected

        verify(walletSdk).getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'should return parsed CheckSupportedProvidersResponse from cached response (same key, different accessToken)'() {
        def accessToken = "token"
        def anotherToken = UUID.randomUUID().toString()
        def bnplProvider = WalletProvider.@Companion.buyNowPayLater()
        def supplierWalletResponse = TestExtensions.anySupplierWalletResponse("supplierId", "walletId", bnplProvider)
        def walletResponse = TestExtensions.anyWalletResponse("userId", "walletId", bnplProvider)

        def expected = new CheckSupportedProvidersResponse(walletResponse, [supplierWalletResponse])

        Mockito.when(walletSdk.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken)).thenReturn(expected)

        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken) == expected
        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken) == expected
        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, anotherToken) == expected

        verify(walletSdk).getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

    @Test
    void 'should return parsed CheckSupportedProvidersResponse from uncached response'() {
        def accessToken = "token"
        def bnplProvider = WalletProvider.@Companion.buyNowPayLater()
        def supplierWalletResponse = TestExtensions.anySupplierWalletResponse("supplierId", "walletId", bnplProvider)
        def secondSupplierWalletResponse = TestExtensions.anySupplierWalletResponse("supplierId2", "walletId", bnplProvider)
        def walletResponse = TestExtensions.anyWalletResponse("userId", "walletId", bnplProvider)

        def firstExpected = new CheckSupportedProvidersResponse(walletResponse, [supplierWalletResponse])
        def secondExpected = new CheckSupportedProvidersResponse(walletResponse, [secondSupplierWalletResponse])

        Mockito.when(walletSdk.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken)).thenReturn(firstExpected)
        Mockito.when(walletSdk.getSupportedProvidersBetween(["supplierId2"], "userId", bnplProvider, accessToken)).thenReturn(secondExpected)

        assert walletBridge.getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken) == firstExpected
        assert walletBridge.getSupportedProvidersBetween(["supplierId2"], "userId", bnplProvider, accessToken) == secondExpected

        verify(walletSdk).getSupportedProvidersBetween(["supplierId"], "userId", bnplProvider, accessToken)
        verify(walletSdk).getSupportedProvidersBetween(["supplierId2"], "userId", bnplProvider, accessToken)
        verifyNoMoreInteractions(walletSdk)
    }

}
