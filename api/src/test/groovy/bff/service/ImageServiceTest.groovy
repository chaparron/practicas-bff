package bff.service

import bff.model.PaymentProviderLogoSize
import org.junit.Test
import org.springframework.test.util.ReflectionTestUtils

class ImageServiceTest {

    private final ImageService imageService = new ImageService()

    @Test
    void 'Given an imageId and a size then returns the correct url' () {
        //Given
        ReflectionTestUtils.setField(imageService, "resizerUrl", "https://resizer-qa.wabi2b.com/")
        String imageId = UUID.randomUUID().toString()
        ImageSizeEnum imageSizeEnum = PaymentProviderLogoSize.SIZE_30x20
        String expectedUrl = "https://resizer-qa.wabi2b.com/fit-in/30x20/".concat(imageId)

        //When
        String result = imageService.url(imageId, imageSizeEnum)

        //Then
        assert result == expectedUrl
    }

}
