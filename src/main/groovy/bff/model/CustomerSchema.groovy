package bff.model

interface CustomerUpdateResult{}

interface VerifyEmailResult{}

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

enum    VerifyEmailReason {
    TOKEN_EXPIRED

    def doThrow() {
        throw new VerifyEmailException(verifyEmailReason: this)
    }
}

class CustomerUpdateFailed implements CustomerUpdateResult {
    CustomerUpdateReason customerUpdateReason
}

class VerifyEmailFailed implements VerifyEmailResult {
    VerifyEmailReason verifyEmailReason
}

class CustomerInput {
    String accessToken
}

class CustomerUpdateInput {
    String phone
    String username
    List<Address> address
    DeliveryPreference deliveryPreference
    List<VerificationDocument> verificationDocuments
    String accessToken
}

class VerifyEmailInput {
    Long id
    String token
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
