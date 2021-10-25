package bff.model

import bff.bridge.BrandBridge
import bff.bridge.ProductBridge
import bff.bridge.SupplierHomeBridge
import bff.bridge.sdk.GroceryListing
import graphql.execution.ExecutionContext
import graphql.language.OperationDefinition
import graphql.schema.DataFetchingEnvironment
import graphql.schema.DataFetchingEnvironmentImpl
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import static bff.support.DataFetchingEnvironments.EXPERIMENTAL
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
    void 'product detail should be resolved by product bridge by default'() {
        def input = new ProductInput(accessToken: "abcde", productId: 1234)
        def result = new Product()

        when(productBridge.getProductById(input.accessToken, input.productId)).thenReturn(result)

        assertEquals(result, query.productDetail(input, null))
        verify(groceryListing, never()).getProductById(input.accessToken, input.productId)
    }

    @Test
    void 'product detail should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new ProductInput()
        def result = new Product()

        when(groceryListing.getProductById(input.accessToken, input.productId)).thenReturn(result)

        assertEquals(result, query.productDetail(input, anyExperimentalDataFetchingEnvironment()))
        verify(productBridge, never()).getProductById(input.accessToken, input.productId)
    }

    @Test
    void 'product detail should be resolved by grocery listing when enabled by configuration'() {
        def input = new ProductInput()
        def result = new Product()
        query.groceryListingEnabled = true

        when(groceryListing.getProductById(input.accessToken, input.productId)).thenReturn(result)

        assertEquals(result, query.productDetail(input, null))
        verify(productBridge, never()).getProductById(input.accessToken, input.productId)
    }

    @Test
    void 'refresh cart should be resolved by product bridge by default'() {
        def input = new RefreshCartInput(accessToken: "abcde", products: [1234, 5678])
        def result = new Cart()

        when(productBridge.refreshCart(input.accessToken, input.products)).thenReturn(result)

        assertEquals(result, query.refreshCart(input, null))
        verify(groceryListing, never()).refreshCart(input.accessToken, input.products)
    }

    @Test
    void 'refresh cart should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new RefreshCartInput(accessToken: "abcde", products: [1234, 5678])
        def result = new Cart()

        when(groceryListing.refreshCart(input.accessToken, input.products)).thenReturn(result)

        assertEquals(result, query.refreshCart(input, anyExperimentalDataFetchingEnvironment()))
        verify(productBridge, never()).refreshCart(input.accessToken, input.products)
    }

    @Test
    void 'refresh cart should be resolved by grocery listing when enabled by configuration'() {
        def input = new RefreshCartInput(accessToken: "abcde", products: [1234, 5678])
        def result = new Cart()
        query.groceryListingEnabled = true

        when(groceryListing.refreshCart(input.accessToken, input.products)).thenReturn(result)

        assertEquals(result, query.refreshCart(input, null))
        verify(productBridge, never()).refreshCart(input.accessToken, input.products)
    }

    @Test
    void 'home brands should be resolved by product bridge by default'() {
        def input = new GetBrandsInput(accessToken: "abcde", countryId: "ar")
        def result = new GetHomeBrandsResult()

        when(brandBridge.getHome(input.accessToken, input.countryId)).thenReturn(result)

        assertEquals(result, query.getHomeBrands(input, null))
        verify(groceryListing, never()).getHomeBrands(input.accessToken, input.countryId)
    }

    @Test
    void 'home brands should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new GetBrandsInput(accessToken: "abcde", countryId: "ar")
        def result = new GetHomeBrandsResult()

        when(groceryListing.getHomeBrands(input.accessToken, input.countryId)).thenReturn(result)

        assertEquals(result, query.getHomeBrands(input, anyExperimentalDataFetchingEnvironment()))
        verify(brandBridge, never()).getHome(input.accessToken, input.countryId)
    }

    @Test
    void 'home brands should be resolved by grocery listing when enabled by configuration'() {
        def input = new GetBrandsInput(accessToken: "abcde", countryId: "ar")
        def result = new GetHomeBrandsResult()
        query.groceryListingEnabled = true

        when(groceryListing.getHomeBrands(input.accessToken, input.countryId)).thenReturn(result)

        assertEquals(result, query.getHomeBrands(input, null))
        verify(brandBridge, never()).getHome(input.accessToken, input.countryId)
    }

    @Test
    void 'preview home brands should be resolved by product bridge by default'() {
        def input = new CoordinatesInput()
        def result = new GetHomeBrandsResult()

        when(brandBridge.previewHomeBrands(input)).thenReturn(result)

        assertEquals(result, query.previewHomeBrands(input, null))
        verify(groceryListing, never()).getHomeBrands(input)
    }

    @Test
    void 'preview home brands should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new CoordinatesInput()
        def result = new GetHomeBrandsResult()

        when(groceryListing.getHomeBrands(input)).thenReturn(result)

        assertEquals(result, query.previewHomeBrands(input, anyExperimentalDataFetchingEnvironment()))
        verify(brandBridge, never()).previewHomeBrands(input)
    }

    @Test
    void 'preview home brands should be resolved by grocery listing when enabled by configuration'() {
        def input = new CoordinatesInput()
        def result = new GetHomeBrandsResult()
        query.groceryListingEnabled = true

        when(groceryListing.getHomeBrands(input)).thenReturn(result)

        assertEquals(result, query.previewHomeBrands(input, null))
        verify(brandBridge, never()).previewHomeBrands(input)
    }

    @Test
    void 'preview home suppliers should be resolved by product bridge by default'() {
        def input = new CoordinatesInput()
        def response = new PreviewHomeSupplierResponse()

        when(supplierBridge.previewHomeSuppliers(input)).thenReturn(response)

        assertEquals(response, query.previewHomeSuppliers(input, null))
        verify(groceryListing, never()).previewHomeSuppliers(input)
    }

    @Test
    void 'preview home suppliers should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new CoordinatesInput()
        def response = new PreviewHomeSupplierResponse()

        when(groceryListing.previewHomeSuppliers(input)).thenReturn(response)

        assertEquals(response, query.previewHomeSuppliers(input, anyExperimentalDataFetchingEnvironment()))
        verify(supplierBridge, never()).previewHomeSuppliers(input)
    }

    @Test
    void 'preview home suppliers should be resolved by grocery listing when enabled by configuration'() {
        def input = new CoordinatesInput()
        def response = new PreviewHomeSupplierResponse()
        query.groceryListingEnabled = true

        when(groceryListing.previewHomeSuppliers(input)).thenReturn(response)

        assertEquals(response, query.previewHomeSuppliers(input, null))
        verify(supplierBridge, never()).previewHomeSuppliers(input)
    }

    private static DataFetchingEnvironment anyExperimentalDataFetchingEnvironment() {
        new DataFetchingEnvironmentImpl(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                new ExecutionContext(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new OperationDefinition(EXPERIMENTAL, null, null),
                        null,
                        null,
                        null
                )
        )
    }

}
