package bff.model

import groovy.transform.ToString
import org.apache.commons.lang3.StringUtils

interface CustomerUpdateResult {}

interface VerifyEmailResult {}

interface VerifyPhoneResult {}

interface ResendVerifyEmailResult {}

interface PreferredAddressResult {}

interface UpdateAddressResult {}

interface DeleteAddressResult {}

interface AddressResult {}

interface AddAddressResult {}

interface SignInResult {}

interface PasswordlessSignUpResult {}

interface AddBranchOfficeResult {}

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
    INVALID_PREFERRED_ADDRESS,
    INVALID_POSTAL_CODE

    def build() {
        return new SignInFailed(reason: this)
    }
}
enum PasswordlessSignUpFailedReason {
    PHONE_ALREADY_EXIST,
    LEGAL_ID_ALREADY_EXIST,
    NAME_ALREADY_EXIST,
    USERNAME_ALREADY_EXIST,
    INVALID_ADDRESS,
    INVALID_ADDRESSES,
    INVALID_STATE,
    INVALID_PREFERRED_ADDRESS,
    INVALID_POSTAL_CODE,
    EMAIL_ALREADY_EXIST,
    INVALID_COUNTRY,
    INVALID_CAPTCHA,
    INVALID_LEGAL_ID

    def build() {
        return new PasswordlessSignUpFailed(reason: this)
    }
}

//TODO: refactor errores según web_store: Separarlos en enums correspondientes.
class CustomerErrorFailed implements PreferredAddressResult, VerifyEmailResult, VerifyPhoneResult,
        ResendVerifyEmailResult, CustomerUpdateResult, UpdateAddressResult,
        DeleteAddressResult {
    CustomerErrorReason customerErrorReason
}


class AddressFailed implements AddressResult {
    AddressFailedReason reason
}

class CustomerType {
    String id
    String code
    String name
}

enum StoreType {
    MAIN_OFFICE, BRANCH_OFFICE
}

class Customer implements CustomerUpdateResult, PasswordlessSignUpResult, AddBranchOfficeResult {
    String accessToken
    String id
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
    boolean marketingEnabled
    WorkingDays workingDays
    RatingScore rating
    int level
    List<String> missingDocuments
    List<VerificationDocument> verificationDocuments
    String country_id
    Country country
    StoreType storeType
    String storeOwnerId
    Boolean permissionOnBranchOffice
    Boolean legalAsDelivery

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
                return DeliveryPreference.NO_PREFERENCE
            }

            return preference.every { it == DeliveryPreference.MORNING } ? DeliveryPreference.MORNING
                    : preference.every { it == DeliveryPreference.AFTERNOON } ? DeliveryPreference.AFTERNOON
                    : DeliveryPreference.NO_PREFERENCE

        }
        DeliveryPreference.NO_PREFERENCE
    }

    Address preferredDeliveryAddress() {
        addresses.find { it.preferred && it.addressType == AddressMode.DELIVERY }
    }

    private static int getHours(String strTime) {
        if (!StringUtils.isEmpty(strTime)) {
            String[] time = strTime.split(':')
            return Integer.parseInt(time[0].trim())
        }
        -1
    }

}

class State {
    String id
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
    State state
    String postalCode
}


class AddressInput {
    Long id
    String formatted
    State state
    Double lat
    Double lon
    String postalCode
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

enum AddBranchOfficeFailedReason {
    INVALID_COUNTRY,
    PHONE_ALREADY_EXIST,
    EMAIL_ALREADY_EXIST,
    INVALID_CUSTOMER_TYPE

    static AddBranchOfficeFailedReason valueFor(String message){
        if (message.contains(INVALID_COUNTRY.name())){
            return INVALID_COUNTRY
        }else if (message.contains(PHONE_ALREADY_EXIST.name())){
            return PHONE_ALREADY_EXIST
        }else if (message.contains(EMAIL_ALREADY_EXIST.name())){
            return EMAIL_ALREADY_EXIST
        }else if (message.contains("No such customer type")){
            return INVALID_CUSTOMER_TYPE
        }
        return null
    }

    AddBranchOfficeFailed build() {
        new AddBranchOfficeFailed(reason: this)
    }
}

class AddAddressFailed implements AddAddressResult {
    AddAddressFailedReason reason
}

class DeleteAddressFailed implements DeleteAddressResult {
    DeleteAddressFailedReason reason
}

class AddBranchOfficeFailed implements AddBranchOfficeResult {
    AddBranchOfficeFailedReason reason
}

class SignInFailed implements SignInResult {
    SignInFailedReason reason
}
class PasswordlessSignUpFailed implements PasswordlessSignUpResult {
    PasswordlessSignUpFailedReason reason
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
    boolean marketingEnabled
}
class CustomerUpdateInputV2 {
    String username
    Boolean acceptWhatsApp
    List<Address> address
    WorkingDays workingDays
    String deliveryComment
    List<VerificationDocument> verificationDocuments
    String accessToken
    boolean marketingEnabled
}

class UpdateBranchOfficeProfileInput{
    String accessToken
    String branchOfficeId
    Boolean acceptWhatsApp
    WorkingDays workingDays
    List<VerificationDocument> verificationDocuments
    boolean marketingEnabled
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
class PasswordlessSignUpUserInput {
    long id
    String firstName
    String lastName
    String countryCode
    String phone
    String email
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
    boolean marketingEnabled
}
class PasswordlessSignUpInput {
    Long id
    String name
    String legalId
    String linePhone
    PasswordlessSignUpUserInput user
    WorkingDays workingDays
    String deliveryComment
    String country_id
    List<AddressInput> addresses
    List<VerificationDocument> verificationDocuments
    boolean marketingEnabled
    String captchaToken
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

@ToString
class CoordinatesInput {
    BigDecimal lat
    BigDecimal lng
    String countryId
}

class PreferredAddressInput {
    Long addressId
    String accessToken
}

class UserDeviceInput {
    String pushToken
    String accessToken
    String os
    String appVersion
}

class DeleteUserDeviceInput {
    String pushToken
    String accessToken
}

class SuppliersNameResult{
    Long id
    Long supplierId
    String supplierName
    String supplierAvatar
    Long productsQuantity
    String readDate
    String lastUpdate
}

class GetSuggestedOrderInput {
    String accessToken
    Long supplierId
}

class SuggestedOrderResult {
    String accessToken
    Long id
    Long customerId
    Long supplierId
    String supplierName
    String readDate
    String lastUpdate
    @Deprecated List<SuggestedOrderItem> items
    List<SuggestedOrderProduct> products
}

class SuggestedOrderItem {
    Long productId
    String productEan
    Long productUnits
    String productImageId
    Integer quantity
    String productTitle
    String categoryTitle
}

class SuggestedOrderProduct {
    Long id
    String name
    Category category
    Brand brand
    List<Image> images
    Price price
    Integer quantity
}

class AcceptTcInput{
    boolean marketingEnabled
    String accessToken
}


class GetFavoriteProductsInput {
    String accessToken
}

class FavouriteProductInput {
    Long productId
    String accessToken
}

class IsValidPhoneInput {
    String phone
    String countryCode
}

class BranchOfficesResponse extends PaginatedResponse<Customer> {
    String accessToken
    Long total
    Long active
}

class GetMyBranchOfficesInput extends PaginatedInput {
    String accessToken
}

class GetBranchOfficeInput {
    String accessToken
    String branchOfficeId
}

class EnableBranchOfficeInput {
    String branchOfficeId
    String accessToken
}

class DisableBranchOfficeInput {
    String branchOfficeId
    String accessToken
}

class AddBranchOfficeInput{
    String accessToken
    String mainOfficeId
    String name
    Boolean emailVerification
    String linePhone
    String firstName
    String lastName
    String countryCode
    String phone
    String email
    AddressInput address
    WorkingDays workingDays
    String deliveryComment
    String customerTypeId
    List<VerificationDocumentInput> verificationDocuments
}