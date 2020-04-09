package bff.bridge

import bff.model.SearchInput
import bff.model.SearchResponse
import bff.model.SearchResult

interface SearchBridge {

    SearchResult search(SearchInput searchInput)

    SearchResponse searchV2(SearchInput searchInput)
}