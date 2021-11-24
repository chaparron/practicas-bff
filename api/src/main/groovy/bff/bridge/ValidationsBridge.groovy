package bff.bridge

import bff.model.PreSignUpInput
import bff.model.ValidateInput
import bff.model.ValidateUsernameInput

interface ValidationsBridge {

    boolean validateUsername(ValidateUsernameInput input)

    boolean validate(ValidateInput input)

    PreSignUpResponse validatePreSignUp(PreSignUpInput input)

    static class PreSignUpResponse {
        boolean userPhoneExist
        boolean emailExist
    }
}