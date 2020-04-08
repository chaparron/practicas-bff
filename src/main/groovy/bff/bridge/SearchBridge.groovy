package bff.bridge

import bff.model.SearchInput
import bff.model.SearchResult

interface SearchBridge {

    SearchResult search(SearchInput searchInput)
}