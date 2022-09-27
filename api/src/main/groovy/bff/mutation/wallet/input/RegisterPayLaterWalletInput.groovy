package bff.mutation.wallet.input

import groovy.transform.Immutable
import wabi2b.payments.common.model.request.RegisterPayLaterWalletRequest

@Immutable
class RegisterPayLaterWalletInput {
    String apiClientToken
    List<RegisterPayLaterWalletRequest> walletRequests
}
