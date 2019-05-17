package bff.bridge

import bff.model.CustomerUpdateResult
import bff.model.Customer
import bff.model.CustomerInput
import bff.model.CustomerUpdateInput
import bff.model.VerifyEmailInput

interface CustomerBridge {

    Customer myProfile(CustomerInput customerInput)

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput)

    Void verifyEmail(VerifyEmailInput verifyEmailInput)
}