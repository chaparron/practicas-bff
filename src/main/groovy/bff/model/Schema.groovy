package bff.model

import groovy.transform.ToString

interface UsernameRegistrationResult {}

interface ProfileCredentialsResult {}

class ProfileCredentials implements ProfileCredentialsResult{
    String accessToken
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

enum UpdateProfileReason {
    DUPLICATE_USERNAME

    def doThrow() {
        throw new UpdateProfileException(reason: this)
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

class RegisterFailed {
    RegisterFailureReason registerReason
}

class UsernameRegistrationFailed implements UsernameRegistrationResult {
    UsernameRegistrationReason reason
}

class UpdateProfileFailed implements ProfileCredentialsResult {
    UpdateProfileReason reason
}

class Void implements ChangePasswordResult, UsernameRegistrationResult,
        ProfileCredentialsResult, ConfirmPasswordResult {
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