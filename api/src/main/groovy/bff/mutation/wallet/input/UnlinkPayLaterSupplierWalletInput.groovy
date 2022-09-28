package bff.mutation.wallet.input

import groovy.transform.Immutable

@Immutable
class UnlinkPayLaterSupplierWalletInput {
    String apiClientToken
    long supplierId
}
