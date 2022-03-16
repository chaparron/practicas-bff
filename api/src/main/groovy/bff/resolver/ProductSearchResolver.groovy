package bff.resolver


import bff.model.ProductImageSize
import bff.model.ProductSearch
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class ProductSearchResolver implements GraphQLResolver<ProductSearch> {

    @Autowired
    ImageService imageService

    List<String> photos(ProductSearch product, ProductImageSize size, Integer take) {
        product.images
                .take(ofNullable(take).orElse(product.images.size()))
                .collect { imageService.url(it.id, size) }
    }

}