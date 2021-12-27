package bff.bridge

import bff.model.Challenge
import bff.model.Credentials
import bff.model.Site
import bff.model.ChannelType

interface AuthServerBridge {

    Credentials login(String email, String password, Site site)

    Challenge challengeRequestForChangeToPasswordlessAuthentication(String countryCode, String phone, ChannelType channel, String accessToken, String remoteAddress)

    Credentials challengeAnswerForChangeToPasswordlessAuthentication(String challengeId, String challengeAnswer, String accessToken)

    Challenge challengeRequestForPasswordlessLogin(String countryCode, String phone, ChannelType channel, String remoteAddress)

    Credentials challengeAnswerForPasswordlessLogin(String challengeId, String challengeAnswer)

    Credentials refreshToken(String refreshToken)

    Credentials userRegistration(String name, String surname, String username, String password, String repeatPassword)

    Boolean resetPassword(String username)

    def resetPasswordConfirm(String token, String password, Long user_id)

    void changePassword(String oldPassword, String newPassword, String accessToken)

    void completeProfile(String phone, String document, String address, String accessToken, String recaptcha)
}

