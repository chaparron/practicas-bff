package bff.model

import bff.service.ImageSizeEnum
import groovy.transform.ToString

@ToString
class HomeInput {
    String country
    Set<String> tags
    Boolean fallback
    String accessToken
}

@ToString
class ListingInput {
    String country
    Set<String> tags
    Integer category
    Integer brand
    String keyword
    String tag
    Boolean favourites
    Boolean promoted
    String accessToken
}

@ToString(excludes = ["accessToken"])
class ContextInput {
    String accessToken
    CoordinatesInput coordinates
}

enum TitleIconSize implements ImageSizeEnum {
    SIZE_24x24

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

@ToString
class Module {
    String id
    String tag
    Optional<I18N> title
    Optional<String> titleIcon
    Optional<String> link
    Optional<TimestampOutput> expiration
}

interface Piece {}

enum AdBannerImageSize implements ImageSizeEnum {
    SIZE_1920x314, SIZE_320x162, SIZE_315x135

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

@ToString
class AdBanner implements Piece {
    String id
    String name
    String desktop
    String mobile
    Optional<String> link
}
