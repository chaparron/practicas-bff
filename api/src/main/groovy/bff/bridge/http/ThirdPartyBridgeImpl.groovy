package bff.bridge.http


import bff.bridge.ThirdPartyBridge
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import wabi2b.sdk.integration.MarketingConsentSdk

@Slf4j
class ThirdPartyBridgeImpl implements ThirdPartyBridge{

    @Autowired
    MarketingConsentSdk thirdPartySdk

    @Override
    Boolean findCustomerConsent(Long customerId, String accessToken) {
        return thirdPartySdk.findCustomerConsent(customerId, accessToken)
    }
}
