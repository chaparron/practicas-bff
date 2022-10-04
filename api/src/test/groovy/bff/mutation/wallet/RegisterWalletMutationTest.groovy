package bff.mutation.wallet


import bff.mutation.wallet.input.RegisterPayLaterSupplierWalletInput
import bff.mutation.wallet.input.RegisterPayLaterWalletInput
import bff.mutation.wallet.input.UnlinkPayLaterSupplierWalletInput
import bff.mutation.wallet.input.UnlinkPayLaterWalletInput
import bff.mutation.wallet.result.WalletPayLaterFailure

import org.junit.Test
import wabi2b.payments.common.model.request.RegisterPayLaterSupplierWalletRequest
import wabi2b.payments.common.model.request.RegisterPayLaterWalletRequest
import wabi2b.payments.sdk.client.AdminWalletSdk

import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class RegisterWalletMutationTest {

    def adminWalletSdk = mock(AdminWalletSdk)
    def registerWalletMutation = new RegisterWalletMutation(adminWalletSdk)


    @Test
    void 'Register Pay later wallets nominal case'() {
        def token = 'token'
        def registerPayLaterWallets = [new RegisterPayLaterWalletRequest('userId1', 'walletId1'),
                                       new RegisterPayLaterWalletRequest('userId2', 'walletId2'),
                                       new RegisterPayLaterWalletRequest('userId3', 'walletId3')]
        def registerPayLaterWalletInput = [apiClientToken: token, walletRequests: registerPayLaterWallets] as RegisterPayLaterWalletInput

        when(adminWalletSdk.registerPayLaterWallet(eq(registerPayLaterWallets), eq(token)))
        registerWalletMutation.registerPayLaterWallets(registerPayLaterWalletInput)
    }

    @Test
    void 'Register Pay later wallets handle exceptions'() {
        def exceptionMessage = 'generic exception'
        def token = 'token'
        def registerPayLaterWallets = [new RegisterPayLaterWalletRequest('userId1', 'walletId1'),
                                       new RegisterPayLaterWalletRequest('userId2', 'walletId2'),
                                       new RegisterPayLaterWalletRequest('userId3', 'walletId3')]
        def registerPayLaterWalletInput = [apiClientToken: token, walletRequests: registerPayLaterWallets] as RegisterPayLaterWalletInput

        when(adminWalletSdk.registerPayLaterWallet(eq(registerPayLaterWallets), eq(token))).thenThrow(new RuntimeException(exceptionMessage))
        def response = registerWalletMutation.registerPayLaterWallets(registerPayLaterWalletInput) as WalletPayLaterFailure
        assert response.reason == exceptionMessage
    }

    @Test
    void 'Register Pay later supplier wallets nominal case'() {
        def token = 'token'
        def registerPayLaterSupplierWallets = [new RegisterPayLaterSupplierWalletRequest('supplierId1', 'walletId1'),
                                               new RegisterPayLaterSupplierWalletRequest('supplierId2', 'walletId2'),
                                               new RegisterPayLaterSupplierWalletRequest('supplierId3', 'walletId3')]
        def registerPayLaterWalletInput = [apiClientToken: token, walletSupplierRequests: registerPayLaterSupplierWallets] as RegisterPayLaterSupplierWalletInput

        when(adminWalletSdk.registerPayLaterSupplierWallet(eq(registerPayLaterSupplierWallets), eq(token)))
        registerWalletMutation.registerPayLaterSupplierWallets(registerPayLaterWalletInput)
    }

    @Test
    void 'Register Pay later supplier wallets handle exceptions'() {
        def exceptionMessage = 'generic exception'
        def token = 'token'
        def registerPayLaterSupplierWallets = [new RegisterPayLaterSupplierWalletRequest('supplierId1', 'walletId1'),
                                               new RegisterPayLaterSupplierWalletRequest('supplierId2', 'walletId2'),
                                               new RegisterPayLaterSupplierWalletRequest('supplierId3', 'walletId3')]
        def registerPayLaterWalletInput = [apiClientToken: token, walletSupplierRequests: registerPayLaterSupplierWallets] as RegisterPayLaterSupplierWalletInput

        when(adminWalletSdk.registerPayLaterSupplierWallet(eq(registerPayLaterSupplierWallets), eq(token))).thenThrow(new RuntimeException(exceptionMessage))
        def response = registerWalletMutation.registerPayLaterSupplierWallets(registerPayLaterWalletInput) as WalletPayLaterFailure
        assert response.reason == exceptionMessage
    }

    @Test
    void 'Unlink Pay later wallets nominal case'() {
        def token = 'token'
        def userId = 123456L
        def unlinkPayLaterSupplierWalletInput = [apiClientToken: token, userId: userId] as UnlinkPayLaterWalletInput
        when(adminWalletSdk.unlinkCustomerWallet(eq(userId), eq(token)))
        registerWalletMutation.unlinkPayLaterWallet(unlinkPayLaterSupplierWalletInput)
    }

    @Test
    void 'Unlink Pay later wallets handle exceptions'() {
        def exceptionMessage = 'generic exception'
        def token = 'token'
        def userId = 123456L
        def unlinkPayLaterSupplierWalletInput = [apiClientToken: token, userId: userId] as UnlinkPayLaterWalletInput
        when(adminWalletSdk.unlinkCustomerWallet(eq(userId), eq(token))).thenThrow(new RuntimeException(exceptionMessage))
        def response = registerWalletMutation.unlinkPayLaterWallet(unlinkPayLaterSupplierWalletInput) as WalletPayLaterFailure
        assert response.reason == exceptionMessage
    }

    @Test
    void 'Unlink Pay later supplier wallets nominal case'() {
        def token = 'token'
        def supplierId = 123456L
        def unlinkPayLaterSupplierWalletInput = [apiClientToken: token, supplierId: supplierId] as UnlinkPayLaterSupplierWalletInput
        when(adminWalletSdk.unlinkSupplierWallet(eq(supplierId), eq(token)))
        registerWalletMutation.unlinkPayLaterSupplierWallet(unlinkPayLaterSupplierWalletInput)
    }

    @Test
    void 'Unlink Pay later supplier wallets handle exceptions'() {
        def exceptionMessage = 'generic exception'
        def token = 'token'
        def supplierId = 123456L
        def unlinkPayLaterSupplierWalletInput = [apiClientToken: token, supplierId: supplierId] as UnlinkPayLaterSupplierWalletInput
        when(adminWalletSdk.unlinkSupplierWallet(eq(supplierId), eq(token))).thenThrow(new RuntimeException(exceptionMessage))
        def response = registerWalletMutation.unlinkPayLaterSupplierWallet(unlinkPayLaterSupplierWalletInput) as WalletPayLaterFailure
        assert response.reason == exceptionMessage
    }

}
