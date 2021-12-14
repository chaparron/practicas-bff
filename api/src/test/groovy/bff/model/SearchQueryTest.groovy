package bff.model

import bff.bridge.SearchBridge
import bff.bridge.sdk.GroceryListing
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

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
    void 'search should be resolved by grocery listing'() {
        def input = new SearchInput()
        def result = new SearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.search(input))
        verify(searchBridge, never()).search(input)
    }

    @Test
    void 'search v2 should be resolved by grocery listing'() {
        def input = new SearchInput()
        def result = new SearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.searchV2(input))
        verify(searchBridge, never()).searchV2(input)
    }

    @Test
    void 'search preview should be resolved by grocery listing'() {
        def input = new PreviewSearchInput()
        def result = new PreviewSearchResult()

        when(groceryListing.search(input)).thenReturn(result)

        assertEquals(result, query.previewSearch(input))
        verify(searchBridge, never()).previewSearch(input)
    }

    @Test
    void 'scroll search should be resolved by grocery listing'() {
        def input = new SearchScrollInput(
                accessToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImNvdW50cmllcyI6W3siaWQiOiJhciJ9XX19.-lzJTqVJio3MI5XWyfwKtYQHYZkxG5uMvfrUkiJnx48",
                scroll: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        )
        def result = new ScrollableSearchResult()

        when(groceryListing.scroll(input)).thenReturn(result)

        assertEquals(result, query.scrollSearch(input))
    }

    @Test
    void 'scroll preview search should be resolved by grocery listing'() {
        def input = new PreviewSearchScrollInput(scroll: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        def result = new ScrollableSearchResult()

        when(groceryListing.scroll(input)).thenReturn(result)

        assertEquals(result, query.scrollPreviewSearch(input))
    }

}
