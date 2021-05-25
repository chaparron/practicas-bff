package bff.bridge.http

import bff.bridge.StateBridge
import bff.model.State
import bff.service.HttpBridge
import bff.service.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct

class StateGatewayBridgeImpl implements StateBridge {

    @Value('${country.service:RegionalConfigService}')
    String countryServiceName

    @Autowired
    HttpBridge httpBridge

    @Autowired
    ServiceDiscovery serviceDiscovery

    @Value('${country.url:}')
    URI countryUrl

    @PostConstruct
    void init() {
        countryUrl = serviceDiscovery.discover(countryServiceName, countryUrl)
    }

    @Override
    List<State> getByCountryId(String countryId) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(countryUrl.resolve("state/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new State(name: it.name)
        }
    }
}
