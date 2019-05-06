package bff.model

enum CustomerStatus {
    PENDING,
    REJECTED,
    APPROVED
}

enum AddressType {
    LEGAL,
    DELIVERY
}

enum VerificationDocumentType {
    IB,
    AFIP,
    ID_FRONT,
    ID_BACK
}

enum DeliveryPreference {
    MORNING, AFTERNOON
}

class State {
    Long id
    String name
}

class VerificationDocument {
    String id
    VerificationDocumentType documentType
}

class Address {
    Long id
    String formatted
    String placeId
    String addressName
    String addressNumber
    String city
    String postalCode
    State state
    Double lat
    Double lon
    String additionalInfo
    Boolean preferred
    AddressType addressType
    Boolean enabled
}

class CustomerInput {
    String accessToken
}

class Customer {
    Long id
    String name
    Boolean enabled
    String legalId
    String linePhone
    CustomerStatus customerStatus
    User user
    Boolean smsVerification
    Boolean emailVerification
    List<Address> addresses
    List<VerificationDocument> verificationDocuments
    DeliveryPreference deliveryPreference
    RatingScore rating
    int level
    List<String> missingDocuments
}
