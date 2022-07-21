package bff.bridge

import wabi2b.payments.common.model.request.WalletProvider
import wabi2b.payments.common.model.response.CheckSupportedProvidersResponse
import wabi2b.payments.common.model.response.WalletResponse

interface WalletBridge {

    WalletResponse getWallet(Long userId, WalletProvider walletProvider, String accessToken)

    CheckSupportedProvidersResponse getSupportedProvidersBetween(List<String> suppliersId, String userId, WalletProvider walletProvider, String accessToken)
}