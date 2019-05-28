package bff.bridge

import bff.model.*

import java.lang.Void

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

    Void addAddress(AddressInput addressInput)

    Void updateAddress(AddressInput addressInput)

    Void deleteAddress(AddressIdInput addressIdInput)
}