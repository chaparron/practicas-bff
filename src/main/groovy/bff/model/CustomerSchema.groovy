package bff.model

interface CustomerUpdateResult {}

interface VerifyEmailResult {}

interface VerifyPhoneResult {}

interface ResendVerifyEmailResult {}

interface ResendVerifySMSResult {}

interface PreferredAddressResult {}

interface UpdateAddressResult {}

interface DeleteAddressResult {}

interface AddressResult {}

interface AddAddressResult {}

interface SignInResult {}

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

enum DeliveryPreference {
    MORNING,
    AFTERNOON,
    NO_PREFERENCE
}

enum SignInFailedReason {
    PHONE_ALREADY_EXIST,
    LEGAL_ID_ALREADY_EXIST,
    NAME_ALREADY_EXIST,
    USERNAME_ALREADY_EXIST,
    INVALID_ADDRESS,
    INVALID_ADDRESSES,
    INVALID_STATE,
    INVALID_PREFERRED_ADDRESS

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

class CustomerType {
    Long id
    String name
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
    CustomerType customerType
    List<Address> addresses

    WorkingDays workingDays
    RatingScore rating
    int level
    List<String> missingDocuments
    List<VerificationDocument> verificationDocuments
    String country_id

    DeliveryPreference getDeliveryPreference() {
        if (workingDays.hours) {
            def preference = workingDays.hours.collect {
                def from = getHours(workingDays.hours.first().from)
                def to = getHours(workingDays.hours.first().to)

                if (from >= 0 && to <= 13) {
                    return DeliveryPreference.MORNING
                }
                if (from > 13 && to < 23) {
                    return DeliveryPreference.AFTERNOON
                }
                return  DeliveryPreference.NO_PREFERENCE
            }

            return preference.every { it == DeliveryPreference.MORNING } ? DeliveryPreference.MORNING
                    : preference.every {  it ==  DeliveryPreference.AFTERNOON } ?  DeliveryPreference.AFTERNOON
                    : DeliveryPreference.NO_PREFERENCE

        }
        DeliveryPreference.NO_PREFERENCE
    }

    private static int getHours(String strTime) {
        String[] time = strTime.split (':')
        Integer.parseInt ( time[0].trim() )
    }
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
    Boolean acceptWhatsApp
    List<Address> address
    WorkingDays workingDays
    String deliveryComment
    List<VerificationDocument> verificationDocuments
    String accessToken
}

class WorkingDays {
    List<Day> days
    List<HourRange> hours
}

class Day {
    Integer dayIndex
    Boolean selected
}

class HourRange {
    String from
    String to
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
    Boolean acceptWhatsApp
    UserCredentialsSignInInput credentials
}

class SignInInput {
    Long id
    String name
    String legalId
    String linePhone
    SignInUserInput user
    WorkingDays workingDays
    String deliveryComment
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

class CoordinatesInput {
    BigDecimal lat
    BigDecimal lng
}

class PreferredAddressInput {
    Long addressId
    String accessToken
}

class UserDeviceInput{
    String pushToken
    String accessToken
    String os
    String appVersion
}