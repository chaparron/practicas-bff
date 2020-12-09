package bff.bridge

import bff.model.CountryConfigurationEntry

interface CountryBridge {

    List<CountryConfigurationEntry> getCountryConfiguration(String countryId)

    List<CountryConfigurationEntry> getCustomerCountryConfiguration(String accessToken)
}
