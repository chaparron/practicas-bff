package bff.model

import bff.JwtToken
import bnpl.sdk.BnPlSdk
import bnpl.sdk.model.requests.PaymentRequest
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import wabi.sdk.impl.CustomSdkException

import java.util.concurrent.CompletableFuture

import static bff.model.LoanPayment.fromSdk

@Component
class BnplMutation implements GraphQLMutationResolver {

    @Autowired
    private BnPlSdk bnPlSdk;

    CompletableFuture<LoanPaymentResult> loanPayment(LoanPaymentRequestInput input) {
        def customerIdUserId = JwtToken.userIdFromToken(input.getAccessToken()).toLong()
        def request = new PaymentRequest(input.supplierOrderId, customerIdUserId, input.supplierId, input.invoice.code, input.invoice.fileId, input.amount)
        bnPlSdk.payWithLoan(request, input.accessToken)
                .map { response ->
                    fromSdk(response)
                }
                .onErrorResume(CustomSdkException) {
                    Mono.just(LoanPaymentFailedReason.findByName(it.error.reason).build(it.error.detail))
                }
                .toFuture()
    }
}
