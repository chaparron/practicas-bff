package bff.model

import bff.bridge.SearchBridge
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
        searchBridge.searchV2(searchInput)
    }

}



