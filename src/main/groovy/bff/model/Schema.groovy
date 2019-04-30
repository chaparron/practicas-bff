package bff.model

import groovy.transform.ToString

interface LoginResult {}

interface RefreshCredentialsResult {}

interface ChangePasswordResult {}

interface UsernameRegistrationResult {}

interface ProfileCredentialsResult {}

class LoginInput {
    String username
    String password
    Site site
}

@ToString()
class RefreshCredentials implements RefreshCredentialsResult {
    String accessToken
    String refreshToken
}

@ToString()
class Credentials {
    String accessToken
    String refreshToken
    String tokenType
    String scope
    String expiresIn
}

class GenericCredentials implements LoginResult {
    String username
    Credentials credentials
}


class ProfileCredentials implements ProfileCredentialsResult{
    String accessToken
}


//TODO: Verificar  si es necesario el retorno del site para el tipo de web que lo este pidiendo.
enum Site {
    CUSTOMER("FE_WEB"),
    SUPPLIER("SUPPLIER_WEB"),
    MANUFACTURER("MANUFACTURER_WEB"),
    BO("BO_WEB")

    String permission

    Site(String permission) {
        this.permission = permission
    }

    static Site fromPermission(String permission){
        Site.values().find({it.permission == permission})
    }
}

enum LoginFailureReason {
    FORBIDDEN,
    UNAUTHORIZED

    def doThrow() {
        throw new LoginFailureException(loginFailureReason: this)
    }
}

enum RegisterFailureReason {
    DUPLICATE_USERNAME,
    PASSWORD_MISMATCH,
    INVALID_NAME,
    INVALID_SURNAME

    def doThrow() {
        throw new WebRegisterException(registerReason: this)
    }
}


class LoginFailed implements LoginResult , RefreshCredentialsResult {
    LoginFailureReason reason
}

class RegisterFailed {
    RegisterFailureReason registerReason
}

enum AuthType {
    USER,
    ADMIN
}

class RefreshCredentialsInput {
    String refreshToken
}

class ResetPasswordRequestInput {
    String username
}
class ResetPasswordConfirmInput {
    String token
    String password
    Long user_id
}

class ChangePasswordInput {
    String currentPassword
    String newPassword
    String accessToken
}

enum ChangePasswordReason {
    PASSWORD_MISMATCH
}

class ChangePasswordFailed implements ChangePasswordResult {

    static final INCORRECT_PASSWORD = new ChangePasswordFailed(reason: ChangePasswordReason.INCORRECT_PASSWORD)

    static final INVALID_NEW = new ChangePasswordFailed(reason: ChangePasswordReason.INVALID_NEW)

    ChangePasswordReason reason
}


enum VoidReason {
    SUCCESS
}

class Void implements ChangePasswordResult, UsernameRegistrationResult,
        ProfileCredentialsResult {
    static final SUCCESS = new Void(voidReason: VoidReason.SUCCESS)
    VoidReason voidReason
}

class Filter {
    String key
    String name
    String value

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
    Long id
    String name
    String key
}

class Sort {
    String field
    String direction
}

class Profile {
    String document
    String dob
    String area_code
    String phone
    String seller
    String seller_type
    Gender gender
    String address
}


class UsernameInput {
    Long userId
    String accessToken
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

class UsernameRegistrationFailed implements UsernameRegistrationResult {
    UsernameRegistrationReason reason
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

enum UpdateProfileReason {
    DUPLICATE_USERNAME

    def doThrow() {
        throw new UpdateProfileException(reason: this)
    }
}


class UpdateProfileFailed implements ProfileCredentialsResult {
    UpdateProfileReason reason
}

class IdInput {
    Long id
}