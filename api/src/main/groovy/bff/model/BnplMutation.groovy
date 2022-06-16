package bff.model

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
        def request = new PaymentRequest(input.orderId, input.customerId, input.supplierId,
                input.invoiceCode, input.money.currency, input.money.amount)
        bnPlSdk.payWithLoan(request, input.accessToken)
                .map { response ->
                    fromSdk(response)
                }
                .toFuture()
    }
}
