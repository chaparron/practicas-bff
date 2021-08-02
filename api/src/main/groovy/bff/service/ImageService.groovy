package bff.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageService {
    @Value('${resizer.url:}')
    String resizerUrl

    String url(String imageId, ImageSizeEnum size) {
        return "$resizerUrl${size.value()}/$imageId"
    }
}

interface ImageSizeEnum {
    String value()
}