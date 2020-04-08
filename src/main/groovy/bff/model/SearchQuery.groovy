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

    SearchResponse search(SearchInput searchInput) {
        searchBridge.search(searchInput)
    }

}



