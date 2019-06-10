package bff.bridge

import bff.model.*


interface CustomerBridge {

    Customer myProfile(CustomerInput customerInput)

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput)

    Void verifyEmail(VerifyEmailInput verifyEmailInput)

    Void resendVerifyEmail(AccessTokenInput accessTokenInput)

    Void verifyPhone(VerifyPhoneInput verifyPhoneInput)

    Void resendVerifySMS(AccessTokenInput accessTokenInput)

    List<Address> findAddresses(AccessTokenInput accessTokenInput)

    List<Address> findAddressesByCustomerAccessToken(String accessToken)

    List<VerificationDocument> findVerificationDocs(String accessToken)

    Void setPreferredAddress(PreferredAddressInput preferredAddressInput)

    Address getAddress(AddressIdInput addressIdInput)

    AddAddressResult addAddress(AddressInput addressInput)

    Void updateAddress(AddressInput addressInput)

    Void deleteAddress(AddressIdInput addressIdInput)
}