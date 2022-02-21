package bff.bridge


import bff.model.DeleteUserDeviceInput
import bff.model.UserDeviceInput

interface PhoneNotifierBridge {
    Boolean isValidPhone(String countryCode, String phone)

    Boolean addUserDevice(UserDeviceInput input)

    Boolean deleteUserDevice(DeleteUserDeviceInput input)

}
