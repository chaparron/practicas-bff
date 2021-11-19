package bff.model

interface PreSignUpResult {}

interface UsernameRegistrationResult {}

interface ProfileCredentialsResult {}

interface BannerDialogResult {}

class ProfileCredentials implements ProfileCredentialsResult {
    String accessToken
}

enum SiteConfigurationFailedReason {
    NOT_FOUND

    def build() {
        new SiteConfigurationFailed(reason: this)
    }

}

enum RegisterFailureReason {
    DUPLICATE_USERNAME,
    PASSWORD_MISMATCH,
    INVALID_NAME,
    INVALID_SURNAME,
    INVALID_VERIFICATION_DOCUMENTS,
    MISSING_REQUIRED_VERIFICATION_DOCUMENT

    def doThrow() {
        throw new WebRegisterException(registerReason: this)
    }
}

enum PreSignUpFailedReason {
    INVALID_CAPTCHA,
    PHONE_ALREADY_EXIST

    def doThrow() {

        throw new PreSignUpException(reason: this)
    }
}

enum UsernameRegistrationReason {
    INVALID_FIRST_NAME,
    INVALID_LAST_NAME,
    INVALID_USERNAME,
    DUPLICATE_USERNAME,
    INVALID_FIRST_NAME_LENGTH,
    INVALID_LAST_NAME_LENGTH,
    INVALID_USERNAME_LENGTH

    def doThrow() {

        throw new UsernameRegistrationException(reason: this)
    }
}

enum VoidReason {
    SUCCESS
}

enum EntityType {
    CUSTOMER,
    USER
}

class SiteConfigurationFailed implements BannerDialogResult {
    SiteConfigurationFailedReason reason
}

class RegisterFailed {
    RegisterFailureReason registerReason
}

class PreSignUpFailed implements PreSignUpResult {
    PreSignUpFailedReason reason
}

class UsernameRegistrationFailed implements UsernameRegistrationResult {
    UsernameRegistrationReason reason
}

class Void implements ChangePasswordResult,
        UsernameRegistrationResult,
        ConfirmPasswordResult,
        VerifyEmailResult,
        VerifyPhoneResult,
        ResendVerifyEmailResult,
        ResendVerifySMSResult,
        PreferredAddressResult,
        OrderUpdateResult,
        CustomerOrdersResult,
        UpdateAddressResult,
        DeleteAddressResult,
        PlaceOrderResult,
        AddAddressResult,
        CustomerRateSupplierResult,
        CustomerReportRateResult,
        PreSignUpResult {
    static final SUCCESS = new Void(voidReason: VoidReason.SUCCESS)
    VoidReason voidReason
    Integer id
    EntityType entityType
}

class Filter {
    String key
    List<FilterItem> values
    String value
}

class FilterItem {
    Integer id
    String name
}

class Facet {
    String id
    String name
    List<Slices> slices

}

class Slices {
    Long size
    Slice obj
}

class Slice {
    String id
    String name
    String key
}

class Sort {
    String field
    String direction
}

class Profile {
    Long id
    String name
    List<String> authorities
}


class UsernameInput {
    Long userId
    String accessToken
}

class PageInput {
    Long number
    PageSize size
}

enum PageSize {
    ZERO(0), ONE(1), TEN(10), TWENTY(20), FIFTY(50), ONE_HUNDRED(100)
    int numericValue

    PageSize(int i) {
        this.numericValue = i
    }
}

enum Gender {
    MALE, FEMALE, OTHER
}

class UserProfileInput {
    Long id
    String firstName
    String lastName
    String username
    String document
    String dob
    String areaCode
    String phone
    String seller
    String seller_type
    Gender gender
    String accessToken
    String address
}

class CompleteProfileInput {
    String document
    String phone
    String address
    String recaptchaResponse
    String accessToken
}


class IdInput {
    Long id
}


interface SupplierResponse {}


class GetSupplierInput {
    String accessToken
    Integer supplierId
}

class BannerDialog implements BannerDialogResult {
    Dialog dialog
    Banner banner
}


class Dialog {
    Boolean firstVisit
    String image
    List<DialogContent> dialogContent
}

class DialogContent {
    String title
    String text
    String lang
}

class Banner {
    List<BannerContent> bannerContent
}

class BannerContent {
    String content
    String lang
}

class PreviewSuppliersResponse implements PreviewSupplierResult {
    List<Supplier> supplier
}

interface PreviewSupplierResult {

}