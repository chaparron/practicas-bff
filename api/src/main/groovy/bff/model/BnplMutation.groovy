package bff.model

import bff.JwtToken
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.requests.PaymentRequest
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.CompletableFuture

import static bff.model.LoanPayment.fromSdk

@Component
class BnplMutation implements GraphQLMutationResolver {

    @Autowired
    private BnPlSdk bnPlSdk;

    CompletableFuture<LoanPaymentResult> loanPayment(LoanPaymentRequestInput input) {
        def customerIdUserId = JwtToken.userIdFromToken(input.getAccessToken())
        def request = new PaymentRequest(input.orderId, customerIdUserId, input.supplierId, input.invoiceCode, input.amount)
        bnPlSdk.payWithLoan(request, input.accessToken)
                .map { response ->
                    fromSdk(response)
                }
                .toFuture()
    }
}
