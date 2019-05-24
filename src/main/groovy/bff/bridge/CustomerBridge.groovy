package bff.bridge

import bff.model.AccessTokenInput
import bff.model.AddressInput
import bff.model.CustomerUpdateResult
import bff.model.Customer
import bff.model.CustomerInput
import bff.model.CustomerUpdateInput
import bff.model.PreferredAddressInput
import bff.model.VerifyEmailInput
import bff.model.VerifyPhoneInput

interface CustomerBridge {

    Customer myProfile(CustomerInput customerInput)

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput)

    Void verifyEmail(VerifyEmailInput verifyEmailInput)

    Void resendVerifyEmail(AccessTokenInput accessTokenInput)

    Void verifyPhone(VerifyPhoneInput verifyPhoneInput)

    Void resendVerifySMS(AccessTokenInput accessTokenInput)

    Void setPreferredAddress(PreferredAddressInput preferredAddressInput)

    Void addAddress(AddressInput addressInput)

    Void updateAddress(AddressInput addressInput)

}