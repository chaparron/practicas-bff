package bff.model

import bff.bridge.BrandBridge
import bff.bridge.ProductBridge
import bff.bridge.SupplierHomeBridge
import bff.bridge.sdk.GroceryListing
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import static org.junit.Assert.assertEquals
import static org.mockito.Mockito.*

@RunWith(MockitoJUnitRunner.class)
class QueryTest {

    @Mock
    ProductBridge productBridge
    @Mock
    BrandBridge brandBridge
    @Mock
    SupplierHomeBridge supplierBridge
    @Mock
    GroceryListing groceryListing
    @InjectMocks
    Query query

    @Test
    void 'find country should be resolved by grocery listing'() {
        def input = new CoordinatesInput()
        def result = new Country(id: "ar")

        when(groceryListing.find(input)).thenReturn(Optional.of(result))

        assertEquals(result, query.findCountry(input))
    }

    @Test
    void 'product detail should be resolved by grocery listing'() {
        def input = new ProductInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48",
                productId: 1234
        )
        def result = new Product()

        when(groceryListing.getProductById(input.accessToken, input.productId)).thenReturn(result)

        assertEquals(result, query.productDetail(input))
        verify(productBridge, never()).getProductById(input.accessToken, input.productId)
    }

    @Test
    void 'refresh cart should be resolved by grocery listing'() {
        def input = new RefreshCartInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48",
                products: [1234, 5678]
        )
        def result = new Cart()

        when(groceryListing.refreshCart(input.accessToken, input.products)).thenReturn(result)

        assertEquals(result, query.refreshCart(input))
        verify(productBridge, never()).refreshCart(input.accessToken, input.products)
    }

    @Test
    void 'home brands should be resolved by grocery listing'() {
        def input = new GetBrandsInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48",
                countryId: "ar"
        )
        def result = new GetHomeBrandsResult()

        when(groceryListing.getHomeBrands(input.accessToken, input.countryId)).thenReturn(result)

        assertEquals(result, query.getHomeBrands(input))
        verify(brandBridge, never()).getHome(input.accessToken, input.countryId)
    }

    @Test
    void 'preview home brands should be resolved by grocery listing'() {
        def input = new CoordinatesInput()
        def result = new GetHomeBrandsResult()

        when(groceryListing.getHomeBrands(input)).thenReturn(result)

        assertEquals(result, query.previewHomeBrands(input))
        verify(brandBridge, never()).previewHomeBrands(input)
    }

    @Test
    void 'preview home suppliers should be resolved by grocery listing'() {
        def input = new CoordinatesInput()
        def response = new PreviewHomeSupplierResponse()

        when(groceryListing.previewHomeSuppliers(input)).thenReturn(response)

        assertEquals(response, query.previewHomeSuppliers(input))
        verify(supplierBridge, never()).previewHomeSuppliers(input)
    }

}
