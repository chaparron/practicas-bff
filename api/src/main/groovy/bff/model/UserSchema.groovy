package bff.model

import cats.kernel.Eq
import groovy.transform.EqualsAndHashCode
import groovy.transform.InheritConstructors
import groovy.transform.ToString

interface LoginResult {}

interface SignedChallengeDemandResult {}

interface ChallengeDemandResult {}

interface SignedChallengeAnswerResult {}

interface ChallengeAnswerResult {}

interface RefreshCredentialsResult {}

interface ChangePasswordResult {}

interface ConfirmPasswordResult {}

class User {
    Long id
    String username
    String firstName
    String lastName
    Boolean acceptWhatsApp
    String countryCode
    String phone
    String email
    UserCredentials credentials
    Boolean usesPasswordless
}

class UserCredentials {
    Boolean enabled
}

class LoginInput {
    String username
    String password
    Boolean supportLegacy
    Site site
}

class SignedChallengeDemandInput {
    String countryCode
    String phone
    String accessToken
}

class SignedChallengeAnswer {
    String challengeId
    String challengeAnswer
    String accessToken
}

class ChallengeDemandInput {
    String countryCode
    String phone
}

class ChallengeAnswer {
    String challengeId
    String challengeAnswer
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

@InheritConstructors
@EqualsAndHashCode
class LegacyCredentials extends GenericCredentials {}

class GenericCredentials implements LoginResult, SignInResult, SignedChallengeAnswerResult, ChallengeAnswerResult {
    String username
    Credentials credentials
    Customer customer
}

class UpgradeRequired implements LoginResult {
    Credentials credentials
}

class Challenge implements SignedChallengeDemandResult, ChallengeDemandResult {
    String challengeId
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

    static Site fromPermission(String permission) {
        values().find({ it.permission == permission })
    }
}

enum LoginFailureReason {
    FORBIDDEN,
    UNAUTHORIZED,
    PASSWORDLESS_REQUIRED,
    DEPRECATED_LOGIN_SYSTEM

    def doThrow() {
        throw new LoginFailureException(loginFailureReason: this)
    }
}

enum SignedChallengeDemandFailureReason {

    PHONE_ALREADY_EXISTS,
    USER_ALREADY_USES_PASSWORDLESS,
    UNAUTHORIZED

    def doThrow() {
        throw new SignedChallengeDemandFailureException(signedChallengeDemandFailureReason: this)
    }
}

enum ChallengeDemandFailureReason {
    UNKNOWN_PHONE

    def doThrow() {
        throw new ChallengeDemandFailureException(challengeDemandFailureReason: this)
    }
}

enum ChallengeAnswerFailureReason {
    UNAUTHORIZED,
    UNKNOWN_CHALLENGE_ID,
    USER_ALREADY_USES_PASSWORDLESS,
    INCORRECT_CODE,
    EXPIRED_CHALLENGE,
    MAX_ATTEMPTS_REACHED

    def doThrow() {
        throw new ChallengeAnswerFailureException(challengeAnswerFailureReason: this.unifiedReason)
    }

    private ChallengeAnswerFailureReason getUnifiedReason() {
        if ([UNKNOWN_CHALLENGE_ID, USER_ALREADY_USES_PASSWORDLESS,
             INCORRECT_CODE].contains(this)) {
            return UNAUTHORIZED
        }
        return this
    }

}

enum AuthType {
    USER,
    ADMIN
}


enum ChangePasswordReason {
    PASSWORD_MISMATCH,
    CANNOT_CHANGE_PWD

    def doThrow() {
        throw new ChangePasswordException(changePasswordReason: this)
    }

}

enum ResetPasswordReason {
    CANNOT_CHANGE_PWD

    def doThrow() {
        throw new ResetPasswordException(resetPasswordReason: this)
    }

}

enum ConfirmPasswordReason {
    TOKEN_EXPIRED,
    CANNOT_CHANGE_PWD

    def doThrow() {
        throw new ConfirmPasswordException(confirmPasswordReason: this)
    }

}

class LoginFailed implements LoginResult, RefreshCredentialsResult {
    LoginFailureReason reason
}

class SignedChallengeDemandFailed implements SignedChallengeDemandResult {
    SignedChallengeDemandFailureReason reason
}

class TooManyShipments implements SignedChallengeDemandResult, ChallengeDemandResult {
    Integer waitTime
}

class ChallengeDemandFailed implements ChallengeDemandResult {
    ChallengeDemandFailureReason reason
}

class ChallengeAnswerFailed implements ChallengeAnswerResult, SignedChallengeAnswerResult {
    ChallengeAnswerFailureReason reason
}

class ChangePasswordFailed implements ChangePasswordResult {
    ChangePasswordReason reason
}

class ResetPasswordDemandFailed {
    ResetPasswordReason reason
}

class ConfirmPasswordFailed implements ConfirmPasswordResult {
    ConfirmPasswordReason confirmPasswordReason
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

class PhoneInput {
    String phone
}










