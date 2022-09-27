package bff.mutation.wallet

import bff.mutation.wallet.input.RegisterPayLaterSupplierWalletInput
import bff.mutation.wallet.input.RegisterPayLaterWalletInput
import bff.mutation.wallet.result.RegisterPayLaterFailure
import bff.mutation.wallet.result.RegisterPayLaterResponse
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import wabi2b.payments.sdk.client.AdminWalletSdk

@Component
@Slf4j
class RegisterWalletMutation implements GraphQLMutationResolver {

    private final AdminWalletSdk adminWalletSdk

    RegisterWalletMutation(AdminWalletSdk adminWalletSdk) {
        this.adminWalletSdk = adminWalletSdk
    }

    RegisterPayLaterResponse registerPayLaterWallets(RegisterPayLaterWalletInput input) {
        try {
            adminWalletSdk.registerPayLaterWallet(input.walletRequests, input.apiClientToken)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            new RegisterPayLaterFailure(reason: ex.getMessage())
        }
    }

    RegisterPayLaterResponse registerPayLaterSupplierWallets(RegisterPayLaterSupplierWalletInput input) {
        try {
            adminWalletSdk.registerPayLaterSupplierWallet(input.walletSupplierRequests, input.apiClientToken)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            new RegisterPayLaterFailure(reason: ex.getMessage())
        }
    }


}
