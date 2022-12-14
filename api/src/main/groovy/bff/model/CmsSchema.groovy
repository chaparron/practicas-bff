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
    Set<String> brands
    String keyword
    String tag
    Integer supplier
    Set<String> suppliers
    Boolean favourites
    Boolean promoted
    String accessToken
}

@ToString
class LandingInput {
    String country
    String id
    Set<String> tags
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

enum CmsPromoImageSize implements ImageSizeEnum {
    SIZE_3840x2160,
    SIZE_1440x1912,
    SIZE_1300x732,
    SIZE_1312x368,
    SIZE_1792x596,
    SIZE_656x656

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

interface CmsCallToAction {}

@ToString
class CmsLink implements CmsCallToAction {
    String url
}

@ToString
class CmsButton implements CmsCallToAction {
    I18N label
    String link
}

@ToString
class CmsPromo implements Piece {
    String id
    String desktop
    String mobile
    Optional<I18N> title
    Optional<I18N> epigraph
    Optional<I18N> label
    Optional<CmsCallToAction> callToAction
}