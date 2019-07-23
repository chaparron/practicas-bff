package bff.bridge

import bff.model.State

interface StateBridge {

    List<State> getByCountryId(String countryId)
}
