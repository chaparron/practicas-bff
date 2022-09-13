package bff.bridge

import bff.model.CreditLines
import bnpl.sdk.model.SupportedMinimumAmountResponse

interface BnplBridge {
    SupportedMinimumAmountResponse supportedMinimumAmount(String country, String accessToken)

    CreditLines userBalance(String accessToken)

    Boolean isSupplierOnboarded(Long supplierId, String accessToken)

}