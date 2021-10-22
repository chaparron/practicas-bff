package bff.model

import bff.bridge.ProductBridge
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
