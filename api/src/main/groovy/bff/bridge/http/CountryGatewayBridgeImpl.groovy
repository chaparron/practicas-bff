package bff.bridge.http

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.CountryConfigurationEntry
import bff.service.HttpBridge
import bff.service.ServiceDiscovery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.util.UriComponentsBuilder

import javax.annotation.PostConstruct

class CountryGatewayBridgeImpl implements CountryBridge {

    @Value('${country.service:RegionalConfigService}')
    String countryServiceName

    @Autowired
    HttpBridge httpBridge

    @Autowired
    ServiceDiscovery serviceDiscovery

    URI countryUrl

    @PostConstruct
    void init() {
        countryUrl = new URI(serviceDiscovery.discover(countryServiceName))
    }

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        httpBridge.get(
                UriComponentsBuilder.fromUri(countryUrl.resolve("country/public/$countryId")).toUriString().toURI(),
                null)?.config?.collect {
            new CountryConfigurationEntry(key: it.key, value: it.value)
        }
    }

    @Override
    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {
        return getCountryConfiguration(JwtToken.countryFromString(accessToken))
    }
}
