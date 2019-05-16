package bff.model

interface CustomerUpdateResult{}

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

enum CustomerUpdateReason {
    PHONE_ALREADY_EXIST

    def doThrow() {
        throw new CustomerUpdateException(customerUpdateReason: this)
    }
}

class CustomerUpdateFailed implements CustomerUpdateResult {
    CustomerUpdateReason customerUpdateReason
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

class Customer implements CustomerUpdateResult{
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

class CustomerUpdateInput {
    String phone
    String username
    List<String> address
    DeliveryPreference deliveryPreference
    List<VerificationDocument> verificationDocuments
    String accessToken
}
