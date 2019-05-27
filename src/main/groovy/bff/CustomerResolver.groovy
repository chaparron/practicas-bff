package bff

import bff.bridge.CustomerBridge
import bff.model.Address
import bff.model.Customer
import bff.model.VerificationDocument
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class CustomerResolver implements GraphQLResolver<Customer> {

    @Autowired
    CustomerBridge customerBridge

    List<VerificationDocument> verificationDocuments(Customer customer) {
        def verificationDocs = customerBridge.findVerificationDocs(customer.accessToken)
        verificationDocs
    }

    List<Address> addresses(Customer customer) {
        def addresses = customerBridge.findAddressesByCustomerAccessToken(customer.accessToken)
        addresses
    }


}
