package bff.mutation.wallet.result

import groovy.transform.Immutable

@Immutable
class WalletPayLaterFailure implements RegisterPayLaterResponse {
    String reason
}
