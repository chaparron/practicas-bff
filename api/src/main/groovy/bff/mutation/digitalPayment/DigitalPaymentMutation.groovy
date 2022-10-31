package bff.mutation.digitalPayment

import bff.model.CreateDigitalPaymentInput
import bff.model.CreateDigitalPaymentResult
import bff.model.DigitalPayment
import bff.model.DigitalPaymentFailedReason
import bff.model.FinalizeDigitalPaymentInput
import bff.model.FinalizeDigitalPaymentResult
import bff.model.GetSupplierPaymentOptionInput
import bff.model.SupplierPaymentOption
import bff.model.SupplierPaymentOptionResult
import bff.model.JpMorganCreateDigitalPayment
import bff.model.UpsertSupplierPaymentOptionInput
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import digitalpayments.sdk.DigitalPaymentsSdk
import digitalpayments.sdk.model.CreatePaymentRequest
import digitalpayments.sdk.model.PaymentOption
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
        def request = new CreatePaymentRequest(input.supplierOrderId, input.amount, input.invoiceId)
        digitalPaymentsSdk.createPayment(request, input.accessToken)
                .map {response ->
                    JpMorganCreateDigitalPayment.fromSdk(response)
                }
                .onErrorResume(CustomSdkException) {
                    Mono.just(DigitalPaymentFailedReason.findByName(it.error.reason).build(it.error.detail))
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
                    Mono.just(DigitalPaymentFailedReason.findByName(it.error.reason).build(it.error.detail))
                }
                .toFuture()

    }

    CompletableFuture<SupplierPaymentOptionResult> getSupplierPaymentOption(GetSupplierPaymentOptionInput input) {
        digitalPaymentsSdk.getSupplierPaymentOption(input.supplierId, input.accessToken)
            .map {
                SupplierPaymentOption.fromSdk(it)
            }
            .onErrorResume(CustomSdkException) {
                Mono.just(DigitalPaymentFailedReason.findByName(it.error.reason).build(it.error.detail))
            }
            .toFuture()
    }

    CompletableFuture<SupplierPaymentOptionResult> upsertSupplierPaymentOption(UpsertSupplierPaymentOptionInput input) {
        def sdkRequest = new digitalpayments.sdk.model.SupplierPaymentOption(
                input.supplierId,
                input.paymentOptions.collect {po -> PaymentOption.valueOf(po.name())}.toSet()
        )
        digitalPaymentsSdk.upsertSupplierPaymentOption(sdkRequest, input.accessToken)
            .map {
                SupplierPaymentOption.fromSdk(it)
            }
            .onErrorResume(CustomSdkException) {
                Mono.just(DigitalPaymentFailedReason.findByName(it.error.reason).build(it.error.detail))
            }
            .toFuture()
    }
}
