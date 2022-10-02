package bff.resolver

import bff.model.Badge
import bff.model.PreviewSupplier
import bff.model.SupplierAvatarSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class PreviewSupplierResolver implements GraphQLResolver<PreviewSupplier> {

    @Autowired
    ImageService imageService

    String avatar(PreviewSupplier supplier, SupplierAvatarSize size) {
        ofNullable(supplier.avatar)
                .map { avatar ->
                    ofNullable(size).map { imageService.url(avatar, it) }.orElse(avatar)
                }
                .orElse(null)
    }

    List<Badge> badges(PreviewSupplier supplier) {
        ofNullable(supplier.badges).orElse([])
    }

}