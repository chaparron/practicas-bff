package bff.resolver

import bff.bridge.CountryBridge
import bff.model.CountryConfigurationEntry
import bff.model.PreviewPrice
import bff.model.Price
import bff.service.MoneyService
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.when

class PreviewPriceResolverTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private MoneyService moneyService
    @Mock
    private CountryBridge countryBridge
    private PreviewPriceResolver resolver

    @Before
    void setup() {
        resolver = new PreviewPriceResolver(
                moneyService: moneyService,
                countryBridge: countryBridge
        )
    }

    @Test
    void 'displayable should delegate to country configuration'() {
        def price = new Price(countryId: "pt")
        when(countryBridge.getCountryConfiguration(price.countryId))
                .thenReturn([new CountryConfigurationEntry(key: "shopping_display_price_guest_customer", value: false)])
        assertFalse(resolver.displayable(new PreviewPrice(price)))
    }

    @Test
    void 'displayable should default to true if no configuration was found for the given country'() {
        def price = new Price(countryId: "ar")
        when(countryBridge.getCountryConfiguration(price.countryId)).thenReturn([])
        assertTrue(resolver.displayable(new PreviewPrice(price)))
    }

}
