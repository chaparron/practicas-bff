package bff.model

import bff.bridge.CategoryBridge
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@Slf4j
class CategoryQuery implements GraphQLQueryResolver {

    @Autowired
    CategoryBridge categoryBridge

    List<Category> findRootCategories(FindRootsInput searchInput) {
        categoryBridge.findRootCategories(searchInput)
    }

}
