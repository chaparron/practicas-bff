package bff.resolver

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
        customerBridge.findVerificationDocs(customer.accessToken)
    }

    List<Address> addresses(Customer customer) {
        customerBridge.findAddressesByCustomerAccessToken(customer.accessToken)
    }

}
