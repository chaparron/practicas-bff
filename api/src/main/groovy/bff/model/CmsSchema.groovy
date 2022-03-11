package bff.model

import groovy.transform.ToString

@ToString
class HomeInput {
    String country
    Set<String> tags
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
}

@ToString
class ContextInput {
    String accessToken
    CoordinatesInput coordinates
}

@ToString
class Module {
    String id
    String tag
    String title
    TimestampOutput expiration
}

interface Piece {}

@ToString
class AdBanner implements Piece {
    String desktop
    String mobile
    String link
}
