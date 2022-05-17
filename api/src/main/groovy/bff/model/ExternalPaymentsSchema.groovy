package bff.model


import groovy.transform.Immutable

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

class ExternalPaymentInformation {
    Money amount
    TimestampOutput created
    String receiver
}

class ExternalPaymentsResult {
    String scroll
    List<ExternalPaymentInformation> externalPayments
}
