package bff.bridge

import bff.model.SearchInput
import bff.model.SearchResponse
import bff.model.SearchResult

interface SearchBridge {

    SearchResponse search(SearchInput searchInput)
}