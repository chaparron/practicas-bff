package bff.model

import bff.bridge.SearchBridge
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
class SearchQueryTest {

    @Mock
    SearchBridge searchBridge
    @Mock
    GroceryListing groceryListing
    @InjectMocks
    SearchQuery query

    @Test
    void 'search should be resolved by search bridge by default'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        )
        def result = new SearchResult()

        when(searchBridge.search(input)).thenReturn(result)

        assertEquals(result, query.search(input, null))
        verify(groceryListing, never()).search(input)
    }

    @Test
    void 'search should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        )
        def result = new SearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.search(input, anyExperimentalDataFetchingEnvironment()))
        verify(searchBridge, never()).search(input)
    }

    @Test
    void 'search should be resolved by grocery listing when customer country enabled by configuration'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        )
        def result = new SearchResult()
        query.groceryListingEnabledCountries = ["ar"]

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.search(input, null))
        verify(searchBridge, never()).search(input)
    }

    @Test
    void 'search v2 should be resolved by search bridge by default'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        )
        def result = new SearchResult()

        when(searchBridge.searchV2(input)).thenReturn(result)

        assertEquals(result, query.searchV2(input, null))
        verify(groceryListing, never()).search(input)
    }

    @Test
    void 'search v2 should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48",
        )
        def result = new SearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.searchV2(input, anyExperimentalDataFetchingEnvironment()))
        verify(searchBridge, never()).searchV2(input)
    }

    @Test
    void 'search v2 should be resolved by grocery listing when customer country enabled by configuration'() {
        def input = new SearchInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48"
        )
        def result = new SearchResult()
        query.groceryListingEnabledCountries = ["ar"]

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.searchV2(input, null))
        verify(searchBridge, never()).searchV2(input)
    }

    @Test
    void 'search preview should be resolved by search bridge by default'() {
        def input = new PreviewSearchInput()
        def result = new SearchResult()

        when(searchBridge.previewSearch(input)).thenReturn(result)

        assertEquals(result, query.previewSearch(input, null))
        verify(groceryListing, never()).search(input)
    }

    @Test
    void 'search preview should be resolved by grocery listing when experimental mode is enabled'() {
        def input = new PreviewSearchInput()
        def result = new PreviewSearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.previewSearch(input, anyExperimentalDataFetchingEnvironment()))
        verify(searchBridge, never()).previewSearch(input)
    }

    @Test
    void 'search preview should be resolved by grocery listing when country enabled by configuration'() {
        def input = new PreviewSearchInput(countryId: "ar")
        def result = new PreviewSearchResult()
        query.groceryListingEnabledCountries = ["ar"]

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.previewSearch(input, null))
        verify(searchBridge, never()).previewSearch(input)
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
