package bff.bridge

import bff.model.Customer
import bff.model.CustomerInput

interface CustomerBridge {

    Customer myProfile(CustomerInput customerInput)

}