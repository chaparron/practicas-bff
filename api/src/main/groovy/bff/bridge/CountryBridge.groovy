package bff.bridge

import bff.model.Country
import bff.model.CountryConfigurationEntry

interface CountryBridge {

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId)

    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken)

    List<Country> getHomeCountries(String locale)

    Country getCountry(String countryId)
}
