package bff.model

import bff.bridge.*
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException

/**
 * TODO: Representa todas las queries a graphql, tener en cuenta de dividirlo en mas de un resolver
 */
@Component
@Slf4j
class Query implements GraphQLQueryResolver {

    @Autowired
    AuthServerBridge authServerBridge

    @Autowired
    CustomerBridge customerBridge

    Void testPhoneNumber(PhoneInput phoneInput) {
        authServerBridge.testPhoneNumber(phoneInput.phone)
        Void.SUCCESS
    }

    Customer myProfile(CustomerInput customerInput) {
        customerBridge.myProfile(customerInput)
    }

    VerifyEmailResult verifyEmail(VerifyEmailInput verifyEmailInput) {
        try {
            customerBridge.verifyEmail(verifyEmailInput)
            Void.SUCCESS
        } catch (VerifyExpiredException verifyExpiredException) {
            verifyExpiredException.build()
        }
    }
}

