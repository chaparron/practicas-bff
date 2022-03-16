package bff.resolver

import bff.model.PreviewProductSearch
import bff.model.ProductImageSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class PreviewProductSearchResolver implements GraphQLResolver<PreviewProductSearch> {

    @Autowired
    ImageService imageService

    List<String> photos(PreviewProductSearch product, ProductImageSize size, Integer take) {
        product.images
                .take(ofNullable(take).orElse(product.images.size()))
                .collect { imageService.url(it.id, size) }
    }

}