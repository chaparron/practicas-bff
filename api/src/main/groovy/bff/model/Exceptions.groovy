package bff.model

import groovy.transform.InheritConstructors;

@InheritConstructors
class Unauthorized extends RuntimeException {}


@InheritConstructors
class InvalidPassword extends RuntimeException {}


@InheritConstructors
class CurrentPasswordMismatch extends RuntimeException {}



// when throwing this exceptions the new relic call is omitted
@InheritConstructors
class SilentException extends  RuntimeException {
}

@InheritConstructors
class PreSignUpException extends RuntimeException {
    PreSignUpFailedReason reason

    def build() {
        new PreSignUpFailed(reason: this.reason)
    }
}

@InheritConstructors
class UsernameRegistrationException extends RuntimeException {
    UsernameRegistrationReason reason

    def build() {
        new UsernameRegistrationFailed(reason: this.reason)
    }
}

class LoginFailureException extends RuntimeException {
    LoginFailureReason loginFailureReason

    def build() {
        new LoginFailed(reason: this.loginFailureReason)
    }
}

class SignedChallengeDemandFailureException extends RuntimeException {
    SignedChallengeDemandFailureReason signedChallengeDemandFailureReason

    def build() {
        new SignedChallengeDemandFailed(reason: this.signedChallengeDemandFailureReason)
    }
}

class TooManyShipmentsException extends RuntimeException {
    private int waitTime

    TooManyShipmentsException(int secs) {
        this.waitTime = secs
    }

    def build() {
        new TooManyShipments(waitTime: waitTime)
    }
}

class TooManyRequestException extends RuntimeException {
    private String error

    TooManyRequestException(String error) {
        this.error = error
    }

    def build() {
        new TooManyRequests(error: error)
    }
}

class ChallengeDemandFailureException extends RuntimeException {
    ChallengeDemandFailureReason challengeDemandFailureReason

    def build() {
        new ChallengeDemandFailed(reason: this.challengeDemandFailureReason)
    }
}

class ChallengeAnswerFailureException extends RuntimeException {
    ChallengeAnswerFailureReason challengeAnswerFailureReason

    def build() {
        new ChallengeAnswerFailed(reason: this.challengeAnswerFailureReason)
    }
}

class CustomerException extends RuntimeException {
    CustomerErrorReason customerErrorReason

    def build() {
        new CustomerErrorFailed(customerErrorReason: this.customerErrorReason)
    }
}

class ChangePasswordException extends RuntimeException {
    ChangePasswordReason changePasswordReason

    def build() {
        new ChangePasswordFailed(reason: this.changePasswordReason)
    }
}

class ResetPasswordException extends RuntimeException {
    ResetPasswordReason resetPasswordReason
}

class ConfirmPasswordException extends RuntimeException {
    ConfirmPasswordReason confirmPasswordReason

    def build() {
        new ConfirmPasswordFailed(confirmPasswordReason: this.confirmPasswordReason)
    }
}

class WebRegisterException extends RuntimeException {
    RegisterFailureReason registerReason

    def build() {
        new RegisterFailed(registerReason: this.registerReason)
    }
}