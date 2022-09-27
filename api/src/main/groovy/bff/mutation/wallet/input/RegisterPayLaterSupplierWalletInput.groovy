package bff.mutation.wallet.input

import groovy.transform.Immutable
import wabi2b.payments.common.model.request.RegisterPayLaterSupplierWalletRequest

@Immutable
class RegisterPayLaterSupplierWalletInput {
    String apiClientToken
    List<RegisterPayLaterSupplierWalletRequest> walletSupplierRequests
}
