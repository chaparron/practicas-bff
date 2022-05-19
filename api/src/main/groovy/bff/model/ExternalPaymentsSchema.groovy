package bff.model


import groovy.transform.Immutable

interface RequestForExternalPaymentResult {}

@Immutable
class ExternalPaymentsInput {
    String accessToken
    ScrollInput scrollInput
}

@Immutable
class ScrollInput {
    String scroll
    Integer size
}

@Immutable
class RequestForExternalPaymentInput {
    String accessToken
    String target
    MoneyInput amount
}

class RequestForExternalPayment implements RequestForExternalPaymentResult {
    String url
}

class RequestForExternalPaymentFailed implements RequestForExternalPaymentResult {
    CreateExternalPaymentFailureReason reason
}


class ExternalPaymentInformation {
    Money amount
    TimestampOutput created
    String receiver
}

class ExternalPaymentsResult {
    String scroll
    List<ExternalPaymentInformation> externalPayments
}

enum CreateExternalPaymentFailureReason {
    TARGET_WALLET_NOT_FOUND
}

