package bff.bridge

import bff.model.Customer
import bff.model.CustomerInput
import bff.model.CustomerUpdateInput
import bff.model.CustomerUpdateResult

interface CustomerBridge {

    Customer myProfile(CustomerInput customerInput)

    CustomerUpdateResult updateProfile(CustomerUpdateInput customerUpdateInput)

}