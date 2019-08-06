package bff.model

interface CustomerUpdateResult{}

interface VerifyEmailResult{}

interface VerifyPhoneResult{}

interface ResendVerifyEmailResult{}

interface ResendVerifySMSResult{}

interface PreferredAddressResult{}

interface UpdateAddressResult{}

interface DeleteAddressResult{}

interface AddressResult {}

interface AddAddressResult {}

interface SignInResult{}

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
    CANNOT_SET_LEGAL_ADDRESS_AS_PREFERRED,
    INVALID_DELIVERY_ADDRESS_COUNT,
    INVALID_ADDRESSES,
    INVALID_STATE

    def doThrow() {
        throw new CustomerException(customerErrorReason: this)
    }
}

enum AddressFailedReason {
    ADDRESS_NOT_FOUND

    def build() {
        return new AddressFailed(reason: this)
    }
}

enum SignInFailedReason {
    PHONE_ALREADY_EXIST,
    LEGAL_ID_ALREADY_EXIST,
    NAME_ALREADY_EXIST,
    USERNAME_ALREADY_EXIST,
    INVALID_ADDRESS,
    INVALID_ADDRESSES,
    INVALID_STATE

    def build() {
        return new SignInFailed(reason: this)
    }
}

//TODO: refactor errores según web_store: Separarlos en enums correspondientes.
class CustomerErrorFailed implements PreferredAddressResult, VerifyEmailResult, VerifyPhoneResult,
        ResendVerifyEmailResult, CustomerUpdateResult, ResendVerifySMSResult, UpdateAddressResult,
        DeleteAddressResult {
    CustomerErrorReason customerErrorReason
}


class AddressFailed implements AddressResult {
    AddressFailedReason reason
}

class Customer implements CustomerUpdateResult {
    String accessToken
    Long id
    String name
    Boolean enabled
    String legalId
    String linePhone
    CustomerStatus customerStatus
    User user
    Boolean smsVerification
    Boolean emailVerification
    DeliveryPreference deliveryPreference
    RatingScore rating
    int level
    List<String> missingDocuments
    List<VerificationDocument> verificationDocuments
    String country_id
}

class State {
    Long id
    String name
}

class VerificationDocument {
    String id
    VerificationDocumentType type
}

class Address implements AddressResult {
    Long id
    String formatted
    String placeId
    String addressName
    String addressNumber
    String city
    String postalCode
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
    State state
    Double lat
    Double lon
    String additionalInfo
    AddressMode addressType
    String accessToken
}

class AddressIdInput {
    Long address_id
    String accessToken
}

enum AddAddressFailedReason {
    INVALID_GOOGLE_ADDRESS,
    INVALID_LATITUDE,
    INVALID_LONGITUDE,
    CUSTOMER_ALREADY_HAS_LEGAL_ADDRESS,
    INVALID_STATE,
    INVALID_ADDRESSES


    def build() {
        new AddAddressFailed(reason: this)
    }
}

enum DeleteAddressFailedReason {
    NOT_ADDRESS_CUSTOMER

    def build() {
        new DeleteAddressFailed(reason: this)
    }
}

class AddAddressFailed implements AddAddressResult {
    AddAddressFailedReason reason
}

class DeleteAddressFailed implements DeleteAddressResult {
    DeleteAddressFailedReason reason
}

class SignInFailed implements SignInResult {
    SignInFailedReason reason
}

class CustomerInput {
    String accessToken
}

class VerificationDocumentInput {
    String id
    VerificationDocumentType documentType
}

class CustomerUpdateInput {
    String phone
    String username
    List<Address> address
    DeliveryPreference deliveryPreference
    List<VerificationDocument> verificationDocuments
    String accessToken
}


class UserCredentialsSignInInput {
    String password
    Boolean enabled
}


class SignInUserInput {
    long id
    String username
    String firstName
    String lastName
    String phone
    UserCredentialsSignInInput credentials
}

class SignInInput {
    Long id
    String name
    String legalId
    String linePhone
    SignInUserInput user
    DeliveryPreference deliveryPreference
    String country_id
    List<AddressInput> addresses
    List<VerificationDocument> verificationDocuments
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