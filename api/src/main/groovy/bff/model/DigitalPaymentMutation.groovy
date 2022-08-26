package bff.model

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.CreatePaymentRequest
import digitalpayments.sdk.model.UpdatePaymentRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import wabi.sdk.impl.CustomSdkException
import java.util.concurrent.CompletableFuture

@Component
class DigitalPaymentMutation implements GraphQLMutationResolver {

    @Autowired
    private DigitalPaymentsSdk digitalPaymentsSdk

    CompletableFuture<CreateDigitalPaymentResult> createDigitalPayment(CreateDigitalPaymentInput input) {
        def request = new CreatePaymentRequest(input.supplierOrderId, input.amount)
        digitalPaymentsSdk.createPayment(request, input.accessToken)
                .map {response ->
                    JpMorganCreateDigitalPayment.fromSdk(response)
                }
                .onErrorResume(CustomSdkException) {
                    Mono.just(CreateDigitalPaymentFailedReason.SDK_ERROR.build())
                }
                .toFuture()
    }

    CompletableFuture<FinalizeDigitalPaymentResult> finalizeDigitalPayment(FinalizeDigitalPaymentInput input) {
        def request = new UpdatePaymentRequest(input.encData)
        digitalPaymentsSdk.updatePayment(request, input.accessToken)
                .map {response ->
                    DigitalPayment.fromSdk(response)
                }
                .onErrorResume(CustomSdkException) {
                    Mono.just(new DigitalPaymentFailed())
                }
                .toFuture()

    }
}
