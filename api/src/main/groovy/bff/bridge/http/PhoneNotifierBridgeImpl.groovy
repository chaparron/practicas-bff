package bff.bridge.http

import bff.bridge.PhoneNotifierBridge

import bff.model.DeleteUserDeviceInput
import bff.model.UserDeviceInput
import com.wabi2b.phonenotifier.sdk.PhoneNotifierClient
import groovy.util.logging.Slf4j

@Slf4j
class PhoneNotifierBridgeImpl implements PhoneNotifierBridge {

    String root

    PhoneNotifierClient client = new PhoneNotifierClient(root)

    @Override
    Boolean isValidPhone(String countryCode, String phone) {
        return client.phoneVerify(countryCode.concat(phone)).isPresent()
    }

    @Override
    Boolean addUserDevice(UserDeviceInput input) {
        return client.addUserDevice(input.accessToken, input.pushToken, input.os, input.appVersion).isPresent()
    }

    @Override
    Boolean deleteUserDevice(DeleteUserDeviceInput input) {
        return client.deleteUserDevice(input.accessToken, input.pushToken).isPresent()
    }
}
