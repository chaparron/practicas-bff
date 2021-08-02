package bff.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageService {
    static final IMAGE_FOLDER = "fit-in/"

    @Value('${resizer.url:}')
    String resizerUrl

    String url(String imageId, ImageSizeEnum size) {
        return "$resizerUrl$IMAGE_FOLDER${size.value()}/$imageId"
    }
}

interface ImageSizeEnum {
    String value()
}