package bff.mutation.wallet.result

import groovy.transform.Immutable

@Immutable
class RegisterPayLaterFailure implements RegisterPayLaterResponse {
    String reason
}
