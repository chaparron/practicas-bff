package bff.model


import bff.bridge.sdk.ExternalPayments
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class ExternalPaymentsQuery implements GraphQLQueryResolver {

    @Autowired
    ExternalPayments ep

    ExternalPaymentsResult findMyExternalPayments(ExternalPaymentsInput input) {
        ep.getMyExternalPayments(input)
    }

    RequestForExternalPaymentResult requestForExternalPayment(RequestForExternalPaymentInput input) {
        ep.generateExternalPaymentUrl(input)
    }
}



