package bff.bridge

import bff.model.PhoneInput
import bff.model.PhoneStatusResult
import bff.model.PreSignUpInput
import bff.model.PreSignUpResponse
import bff.model.ValidateInput
import bff.model.ValidateUsernameInput
import graphql.schema.DataFetchingEnvironment

interface ValidationsBridge {

    boolean validateUsername(ValidateUsernameInput input)

    boolean validate(ValidateInput input)

    PreSignUpResponse validatePreSignUp(PreSignUpInput input)

    PhoneStatusResult getPhoneStatus(PhoneInput input , String remoteIp)
}