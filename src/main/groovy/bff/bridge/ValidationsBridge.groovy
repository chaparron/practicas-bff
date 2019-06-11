package bff.bridge

import bff.model.ValidateInput
import bff.model.ValidateUsernameInput

interface ValidationsBridge {

    boolean validateUsername(ValidateUsernameInput input)

    boolean validate(ValidateInput input)
}