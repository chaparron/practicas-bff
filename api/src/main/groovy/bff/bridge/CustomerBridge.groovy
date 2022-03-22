package bff.bridge

import bff.model.*


interface CustomerBridge {

    Customer myProfile(String accessToken)

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput)
    CustomerUpdateResult updateProfileV2(CustomerUpdateInputV2 customerUpdateInput)

    CredentialsCustomerResponse signIn(SignInInput signInInput)
    Customer passwordlessSignUp(PasswordlessSignUpInput passwordlessSignUpInput, String remoteAddress)

    Void verifyEmail(VerifyEmailInput verifyEmailInput)

    Void resendVerifyEmail(AccessTokenInput accessTokenInput)

    List<Address> findAddresses(AccessTokenInput accessTokenInput)

    List<Address> findAddressesByCustomerAccessToken(String accessToken)

    List<VerificationDocument> findVerificationDocs(String accessToken)

    Void setPreferredAddress(PreferredAddressInput preferredAddressInput)

    Address getAddress(AddressIdInput addressIdInput)

    AddAddressResult addAddress(AddressInput addressInput)

    Void updateAddress(AddressInput addressInput)

    Void deleteAddress(AddressIdInput addressIdInput)

    Boolean customerHasOrders(AccessTokenInput accessTokenInput)

    Integer getPendingRatesCount(AccessTokenInput accessTokenInput)

    SupplierRatingsResponse getSupplierRatings(String accessToken, Long supplierId, Long page, Long size)

    List<SupplierOrder> getSupplierOrdersPendingToRate(String accessToken)

    CustomerRateSupplierResult customerRateSupplier(String accessToken, Integer supplierOrderId, Integer supplierId, String opinion, Integer score)

    CustomerReportRateResult customerReportRate(String accessToken, Integer rateId)

    PreSignedObject findCustomerLegalDocument(FindCustomerLegalDocumentInput findCustomerLegalDocumentInput)

    AddressResult getPreferredAddress(String accessToken)

    List<CustomerCancelOptionReason> getCancelOptions(String accessToken)

    Void enableWhatsApp(AccessTokenInput input)

    Void disableWhatsApp(AccessTokenInput input)

    Void userDevice(UserDeviceInput input)

    Void deleteUserDevice(AccessTokenInput input)

    List<SuppliersNameResult> getSuppliersThatHasSuggestedOrders(String accessToken)

    SuggestedOrderResult getSuggestedOrder(GetSuggestedOrderInput input)

    Void markSuggestionAsRead(String accessToken, List<Long> supplierId)

    Void acceptTc(AcceptTcInput acceptTcInput)

    List<SupplierOrder> findPendingRateSinceLastLogin(String accessToken)

    CustomerResponse getChildStores(String accessToken, Long page, Long size)

    def enableStore(String accessToken, Long storeId)

    def disableStore(String accessToken, Long storeId)
}