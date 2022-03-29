package bff.resolver


import bff.bridge.sdk.GroceryListing
import bff.model.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.class)
class SuggestedOrderResultResolverTest {

    @Mock
    GroceryListing groceryListing

    @InjectMocks
    private SuggestedOrderResultResolver suggestedOrderResultResolver = new SuggestedOrderResultResolver()

    @Test
    void should_return_empty_list_if_no_product_search_results_found() {
        // given
        String accessToken = "foo"
        SuggestedOrderResult suggestedOrderResult = new SuggestedOrderResult(
                accessToken: accessToken,
                items: new ArrayList<SuggestedOrderItem>())
        Long supplierId = 20L

        // when
        List<ProductSearch> productSearchList = new ArrayList<>()
        Mockito.when(groceryListing.getProductsByIdsAndSupplierId(accessToken, new ArrayList<Long>(), supplierId))
                .thenReturn(productSearchList)
        List<SuggestedOrderProduct> result = suggestedOrderResultResolver.products(suggestedOrderResult)

        // then
        Assert.assertTrue(result.isEmpty())
    }

    @Test
    void should_return_product_search_results_filtered_by_display_units() {
        // given
        String accessToken = "foo"
        List<SuggestedOrderItem> orderItems = ArrayList.of(
                new SuggestedOrderItem(productId: 1, productUnits: 1, quantity: 10),
                new SuggestedOrderItem(productId: 1, productUnits: 12, quantity: 5),
                new SuggestedOrderItem(productId: 2, productUnits: 6, quantity: 1),
                new SuggestedOrderItem(productId: 3, productUnits: 1, quantity: 1)
        )
        Long supplierId = 10L
        SuggestedOrderResult suggestedOrderResult = new SuggestedOrderResult(
                accessToken: accessToken, supplierId: supplierId, items: orderItems)

        // when
        List<ProductSearch> productSearchList = ArrayList.of(
                new ProductSearch(id: 1, name: 'prod1', category: new Category(id: 1), brand: new Brand(id: 1),
                        images: ArrayList.of(new Image(id: "img1")),
                        prices: ArrayList.of(
                                new Price(display: new Display(units: 1), value: new BigDecimal(100)),
                                new Price(display: new Display(units: 6), value: new BigDecimal(300)),
                                new Price(display: new Display(units: 12), value: new BigDecimal(600))
                        )
                ),
                new ProductSearch(id: 2, name: 'prod2', category: new Category(id: 2), brand: new Brand(id: 2),
                        images: ArrayList.of(new Image(id: "img2")),
                        prices: ArrayList.of(new Price(display: new Display(units: 1), value: new BigDecimal(1000)))
                ),
                new ProductSearch(id: 3, name: 'prod3', category: new Category(id: 3), brand: new Brand(id: 3),
                        images: ArrayList.of(new Image(id: "img3")),
                        prices: ArrayList.of(new Price(display: new Display(units: 1), value: new BigDecimal(500)))
                )
        )
        Mockito.when(groceryListing.getProductsByIdsAndSupplierId(accessToken, Set.of(1L, 2L, 3L), supplierId))
                .thenReturn(productSearchList)
        List<SuggestedOrderProduct> result = suggestedOrderResultResolver.products(suggestedOrderResult)

        // then
        List<SuggestedOrderProduct> prod1Results = result.findAll { it.id == 1L }
        List<SuggestedOrderProduct> prod2Results = result.findAll { it.id == 2L }
        List<SuggestedOrderProduct> prod3Results = result.findAll { it.id == 3L }

        Assert.assertTrue(prod1Results.size() == 2)
        Assert.assertTrue(prod2Results.isEmpty())
        Assert.assertTrue(prod3Results.size() == 1)

        SuggestedOrderProduct prod1OneUnit = prod1Results.find { it.price.getDisplay().units == 1 }
        Assert.assertNotNull(prod1OneUnit)
        Assert.assertTrue(prod1OneUnit.category.id == 1)
        Assert.assertTrue(prod1OneUnit.brand.id == 1)
        Assert.assertTrue(prod1OneUnit.images.first().id == "img1")
        Assert.assertTrue(prod1OneUnit.price.value == 100)
        Assert.assertTrue(prod1OneUnit.quantity == 10)

        SuggestedOrderProduct prod1SixUnits = prod1Results.find { it.price.getDisplay().units == 6 }
        Assert.assertNull(prod1SixUnits)

        SuggestedOrderProduct prod1TwelveUnits = prod1Results.find { it.price.getDisplay().units == 12 }
        Assert.assertNotNull(prod1TwelveUnits)
        Assert.assertTrue(prod1TwelveUnits.price.value == 600)
        Assert.assertTrue(prod1TwelveUnits.quantity == 5)

        SuggestedOrderProduct prod3OneUnit = prod3Results.find { it.price.getDisplay().units == 1 }
        Assert.assertNotNull(prod3OneUnit)
        Assert.assertTrue(prod3OneUnit.category.id == 3)
        Assert.assertTrue(prod3OneUnit.brand.id == 3)
        Assert.assertTrue(prod3OneUnit.images.first().id == "img3")
        Assert.assertTrue(prod3OneUnit.price.value == 500)
        Assert.assertTrue(prod3OneUnit.quantity == 1)
    }
}
