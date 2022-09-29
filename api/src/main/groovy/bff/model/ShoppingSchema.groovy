package bff.model

import bff.service.ImageSizeEnum

class Badge {
    String image
    Optional<Closure<String>> tooltip
    Optional<OverlayPosition> overlayPosition
}

enum OverlayPosition {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT
}

enum BadgeImageSize implements ImageSizeEnum {
    SIZE_16x16,
    SIZE_24x24

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}
