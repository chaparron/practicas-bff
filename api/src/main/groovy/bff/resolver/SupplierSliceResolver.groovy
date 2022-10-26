package bff.resolver


import bff.model.SupplierAvatarSize
import bff.model.SupplierSlice
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import static java.util.Optional.ofNullable

@Component
@Slf4j
class SupplierSliceResolver implements GraphQLResolver<SupplierSlice> {

    @Autowired
    ImageService imageService

    String avatar(SupplierSlice supplier, SupplierAvatarSize size) {
        supplier.avatar
                .map { avatar ->
                    ofNullable(size).map { imageService.url(avatar, it) }.orElse(avatar)
                }
                .orElse(null)
    }

}