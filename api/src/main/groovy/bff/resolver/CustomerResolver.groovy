package bff.resolver

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.model.AccessTokenInput
import bff.model.Address
import bff.model.Country
import bff.model.Customer
import bff.model.CustomerStatus
import bff.model.VerificationDocument
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CustomerResolver implements GraphQLResolver<Customer> {

    @Autowired
    CustomerBridge customerBridge
    @Autowired
    CountryBridge countryBridge

    List<VerificationDocument> verificationDocuments(Customer customer) {
        customer.verificationDocuments?:customerBridge.findVerificationDocs(customer.accessToken)
    }

    List<Address> addresses(Customer customer) {
        customer.addresses?:customerBridge.findAddressesByCustomerAccessToken(customer.accessToken)
    }

    Boolean hasOrders(Customer customer) {
        if (customer.customerStatus == CustomerStatus.APPROVED && customer.accessToken) {
            customerBridge.customerHasOrders(new AccessTokenInput(accessToken: customer.accessToken))
        }
        return false
    }

    Country country(Customer customer) {
        countryBridge.getCountry(customer.country_id)
    }
}
