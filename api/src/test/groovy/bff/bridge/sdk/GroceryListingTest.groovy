package bff.bridge.sdk

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.model.CoordinatesInput
import bff.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import scala.Option
import wabi2b.grocery.listing.sdk.Coordinate
import wabi2b.grocery.listing.sdk.Sdk

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner.class)
class GroceryListingTest {

    @Mock
    Sdk sdk
    @Mock
    CountryBridge countryBridge
    @Mock
    CustomerBridge customerBridge
    @InjectMocks
    GroceryListing groceryListing

    @Test
    void 'country should be returned if found for the given coordinates'() {
        def input = new CoordinatesInput("lat": -34.55742952421648, "lng": -58.447321788788805)
        def found = new Country(id: "ar")

        when(sdk.find(new Coordinate(input.lat.toDouble(), input.lng.toDouble()))).thenReturn(Option.apply("ar"))
        when(countryBridge.getCountry("ar")).thenReturn(found)

        assertEquals(Optional.of(found), groceryListing.find(input))
    }

    @Test
    void 'country should be empty if not found for the given coordinates'() {
        def input = new CoordinatesInput("lat": -34.55742952421648, "lng": -58.447321788788805)

        when(sdk.find(new Coordinate(input.lat.toDouble(), input.lng.toDouble()))).thenReturn(Option.empty())

        assertTrue(groceryListing.find(input).isEmpty())
        verify(countryBridge, never()).getCountry(anyString())
    }

    @Test
    void 'country should be empty if not found for the given id'() {
        def input = new CoordinatesInput("lat": -34.55742952421648, "lng": -58.447321788788805)

        when(sdk.find(new Coordinate(input.lat.toDouble(), input.lng.toDouble()))).thenReturn(Option.apply("ar"))
        when(countryBridge.getCountry("ar")).thenReturn(null)

        assertTrue(groceryListing.find(input).isEmpty())
    }

}
