package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.model.HomeInput
import bff.model.ListingInput
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import scala.Option
import wabi2b.cms.sdk.FullText$
import wabi2b.cms.sdk.Module as CmsModule
import wabi2b.cms.sdk.Sdk

import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.when
import static scala.jdk.javaapi.CollectionConverters.asScala
import static wabi2b.cms.sdk.FindModulesQuery.homeModulesIn
import static wabi2b.cms.sdk.FindModulesQuery.listingModulesIn

@RunWith(MockitoJUnitRunner.class)
class CmsTest {

    @Mock
    Sdk sdk
    @Mock
    CustomerBridge customerBridge
    @InjectMocks
    Cms cms

    @Test
    void 'home modules should be fetched for the given country'() {
        when(sdk.query(homeModulesIn("ar", false, Option.empty())))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new HomeInput(country: "ar")).isEmpty())
    }

    @Test
    void 'home modules can be filtered by tag'() {
        when(sdk.query(homeModulesIn("ar", false, Option.empty()).tagged("tag_1").tagged("tag_2")))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new HomeInput(country: "ar", tags: ["tag_1", "tag_2"])).isEmpty())
    }

    @Test
    void 'listing modules should be fetched for the given country'() {
        when(sdk.query(listingModulesIn("ar", Option.empty())))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar")).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by tag'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).tagged("tag_1").tagged("tag_2")))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", tags: ["tag_1", "tag_2"])).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by brand'() {
        when(
                sdk.query(
                        listingModulesIn("ar", Option.empty())
                                .filteredByBrand("5", asScala([] as List<String>).toSeq())
                )
        ).thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", brand: 5)).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by category'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).filteredByCategory("1")))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", category: 1)).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by term'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).filteredByTerm("coca", Option.empty(), FullText$.MODULE$)))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", keyword: "coca")).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by promotion'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).filteredByPromotion("promo")))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", tag: "promo")).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by any promotion'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).filteredByAnyPromotion()))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", promoted: true)).isEmpty())
    }

    @Test
    void 'listing modules can be filtered by favourite'() {
        when(sdk.query(listingModulesIn("ar", Option.empty()).filteredByFavourite()))
                .thenReturn(asScala([] as List<CmsModule>).toList())

        assertTrue(cms.find(new ListingInput(country: "ar", favourites: true)).isEmpty())
    }

}
