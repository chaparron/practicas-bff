package bff.model

interface CustomerUpdateResult{}

interface VerifyEmailResult{}

interface VerifyPhoneResult{}

interface ResendVerifyEmailResult{}

interface ResendVerifySMSResult{}

interface PreferredAddressResult{}


enum CustomerStatus {
    PENDING,
    REJECTED,
    APPROVED
}

enum AddressMode {
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
    MORNING,
    AFTERNOON,
    NO_PREFERENCE
}

enum CustomerErrorReason {
    PHONE_ALREADY_EXIST,
    TOKEN_EXPIRED,
    NO_VERIFICATION_EMAIL_PENDING,
    NO_VERIFICATION_SMS_PENDING,
    NOT_ADDRESS_CUSTOMER,
    CANNOT_SET_LEGAL_ADDRESS_AS_PREFERRED

    def doThrow() {
        throw new CustomerException(customerErrorReason: this)
    }
}


class CustomerErrorFailed implements PreferredAddressResult, VerifyEmailResult, VerifyPhoneResult,
        ResendVerifyEmailResult, CustomerUpdateResult, ResendVerifySMSResult {
    CustomerErrorReason customerErrorReason
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
    AddressMode addressType
    Boolean enabled
}


class AddressInput {
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
    AddressMode addressType
    Boolean enabled
    String accessToken
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

class VerifyPhoneInput {
    String token
    String accessToken
}

class AccessTokenInput {
    String accessToken
}

class PreferredAddressInput {
    Long addressId
    String accessToken
}