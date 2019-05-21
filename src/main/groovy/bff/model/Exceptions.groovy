package bff.model

import groovy.transform.InheritConstructors;

@InheritConstructors
class Unauthorized extends RuntimeException {}


@InheritConstructors
class InvalidPassword extends RuntimeException {}


@InheritConstructors
class CurrentPasswordMismatch extends RuntimeException {}


@InheritConstructors
class UsernameRegistrationException extends RuntimeException {
    UsernameRegistrationReason reason
    def build() {
        new UsernameRegistrationFailed( reason : this.reason)
    }
}

class LoginFailureException extends RuntimeException {
    LoginFailureReason loginFailureReason
    def build() {
        new LoginFailed(reason: this.loginFailureReason)
    }
}

class CustomerUpdateException extends RuntimeException {
    CustomerUpdateReason customerUpdateReason
    def build() {
        new CustomerUpdateFailed(customerUpdateReason: this.customerUpdateReason)
    }
}

class VerifyExpiredException extends RuntimeException {
    VerifyExpiredReason verifyExpiredReason
    def build() {
        new VerifyExpiredFailed(verifyExpiredReason: this.verifyExpiredReason)
    }
}

class ResendVerifyEmailException extends RuntimeException {
    ResendVerifyEmailReason resendVerifyEmailReason
    def build() {
        new ResendVerifyEmailFailed(resendVerifyEmailReason: this.resendVerifyEmailReason)
    }
}

class ChangePasswordException extends RuntimeException {
    ChangePasswordReason changePasswordReason
    def build() {
        new ChangePasswordFailed(reason: this.changePasswordReason)
    }
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