package bff.bridge

import bff.model.CountryConfigurationEntry
import bff.model.CountryHomeResponse

interface CountryBridge {

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId)

    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken)

    List<CountryHomeResponse> getHomeCountries(String locale)
}
