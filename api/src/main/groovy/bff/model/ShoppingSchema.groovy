package bff.model

import bff.service.ImageSizeEnum
import org.springframework.context.MessageSource
import sun.util.locale.LanguageTag

import static java.util.Locale.forLanguageTag
import static java.util.Optional.of
import static java.util.Optional.ofNullable

class Badge {
    String id
    String image
    Optional<Closure<String>> tooltip
    Optional<OverlayPosition> overlayPosition
}

class DiscountBadge {
    static def create(Closure<String> tooltip) {
        new Badge(
                id: "discount",
                image: "a15a1f04-e452-4a1b-bebc-b531c9b26776.svg",
                tooltip: of(
                        { LanguageTag languageTag ->
                            def locale = forLanguageTag(
                                    ofNullable(languageTag.toString())
                                            .orElse("en")
                            )
                            tooltip(locale)
                        }
                ),
                overlayPosition: of(OverlayPosition.TOP_LEFT)
        )
    }
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
