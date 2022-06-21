package bff.bridge.http

import bff.bridge.StateBridge
import bff.model.State
import org.springframework.beans.factory.annotation.Autowired
import wabi2b.sdk.regional.RegionalConfigSdk

class StateGatewayBridgeImpl implements StateBridge {

    @Autowired
    private RegionalConfigSdk regionalConfigSdk

    @Override
    List<State> getByCountryId(String countryId) {
        regionalConfigSdk.findStatesForCountry(countryId).collect {
            new State(id: it.isoCode, name: it.name)
        }
    }
}
