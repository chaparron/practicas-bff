package bff.model

import bff.bridge.SearchBridge
import bff.configuration.BadRequestErrorException
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class SearchQuery implements GraphQLQueryResolver {

    @Autowired
    SearchBridge searchBridge

    SearchResult search(SearchInput searchInput) {
        searchBridge.search(searchInput)
    }

    SearchResponse searchV2(SearchInput searchInput) {
        try {
            return searchBridge.searchV2(searchInput)
        } catch (BadRequestErrorException ex) {
            SearchFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }

    SearchResponse previewSearch(PreviewSearchInput searchInput) {
        try {
            return searchBridge.previewSearch(searchInput)
        } catch (BadRequestErrorException ex) {
            SearchFailedReason.valueOf((String) ex.innerResponse).build()
        }
    }



}



