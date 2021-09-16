package bff.bridge

import bff.model.PreviewSearchInput
import bff.model.SearchInput
import bff.model.SearchResponse
import bff.model.SearchResult

interface SearchBridge {

    SearchResult search(SearchInput input)

    SearchResponse searchV2(SearchInput input)

    SearchResponse previewSearch(PreviewSearchInput input)

}