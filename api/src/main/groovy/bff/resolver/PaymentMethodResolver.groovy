package bff.resolver

import bff.model.DebitCard
import bff.model.NetBanking
import bff.model.BuyNowPayLaterPaymentMethod
import bff.model.CreditCard
import bff.model.DefaultPaymentMethod
import bff.model.DigitalWallet
import bff.model.UPI
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
class PaymentMethodResolver<T> {

    @Autowired
    MessageSource messageSource

    CompletableFuture<String> paymentMethodText(T paymentMethod, String languageTag) {
        Mono.just(messageSource.getMessage(
                "payment.method.${paymentMethod.getClass().getSimpleName()}",
                null,
                "payment.method",
                Locale.forLanguageTag(languageTag)
        )).toFuture()
    }
}

@Component
class CreditCardResolver extends PaymentMethodResolver<CreditCard> implements GraphQLResolver<CreditCard> {
    CreditCardResolver() {
        super()
    }
}

@Component
class BankTransferResolver extends PaymentMethodResolver<NetBanking> implements GraphQLResolver<NetBanking> {
    BankTransferResolver() {
        super()
    }
}

@Component
class UPIResolver extends PaymentMethodResolver<UPI> implements GraphQLResolver<UPI> {
    UPIResolver() {
        super()
    }
}

@Component
class DigitalWalletResolver extends PaymentMethodResolver<DigitalWallet> implements GraphQLResolver<DigitalWallet> {
    DigitalWalletResolver() {
        super()
    }
}

@Component
class DebitCardResolver extends PaymentMethodResolver<DebitCard> implements GraphQLResolver<DebitCard> {
    DebitCardResolver() {
        super()
    }
}

@Component
class BuyNowPayLaterPaymentMethodResolver extends PaymentMethodResolver<BuyNowPayLaterPaymentMethod> implements GraphQLResolver<BuyNowPayLaterPaymentMethod> {
    BuyNowPayLaterPaymentMethodResolver() {
        super()
    }
}

@Component
class DefaultPaymentMethodResolver extends PaymentMethodResolver<DefaultPaymentMethod> implements GraphQLResolver<DefaultPaymentMethod> {
    DefaultPaymentMethodResolver() {
        super()
    }
}