package bff.bridge

import bnpl.sdk.model.SupportedMinimumAmountResponse

interface BnplBridge {
    SupportedMinimumAmountResponse supportedMinimumAmount(String country, String accessToken)
}