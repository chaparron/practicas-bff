package bff.bridge

import bff.model.CountryConfiguration
import bff.model.CountryConfigurationEntry
import bff.model.Country

interface CountryBridge {

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId)

    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken)

    List<Country> getHomeCountries(String locale)

    CountryConfiguration getCountry(String countryId)

}
