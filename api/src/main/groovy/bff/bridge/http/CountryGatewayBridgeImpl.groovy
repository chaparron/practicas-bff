package bff.bridge.http

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.mapper.CountryMapper
import bff.model.Country
import bff.model.CountryConfigurationEntry
import bff.model.CountryNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import wabi2b.sdk.regional.RegionalConfigSdk

import static java.util.Optional.ofNullable

class CountryGatewayBridgeImpl implements CountryBridge {

    @Autowired
    CountryMapper countryMapper

    @Autowired
    private RegionalConfigSdk regionalConfigSdk

    @Override
    List<CountryConfigurationEntry> getCountryConfiguration(String countryId) {
        regionalConfigSdk.findCountryConfig(countryId)
                ?.config
                ?.collect {
                    new CountryConfigurationEntry(key: it.key, value: it.value)
                }
    }

    @Override
    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken) {
        String countryId = JwtToken.countryFromString(accessToken)
        getCountryConfiguration(countryId)
    }

    @Override
    List<Country> getHomeCountries(String locale) {
        String language = Locale.forLanguageTag(locale).language
        regionalConfigSdk.findCountries(true)?.collect {
            return countryMapper.buildCountryWithLocale(it, language)
        }?.sort(false, { it.name })
    }

    @Override
    Country getCountry(String countryId) {
        ofNullable(regionalConfigSdk.findCountry(countryId))
                .map { countryMapper.buildCountry(it) }
                .orElseThrow { new CountryNotFoundException() }
    }
}
