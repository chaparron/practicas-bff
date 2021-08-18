package bff.model

import bff.service.ImageSizeEnum

class CountryConfigurationEntry {
    String key
    String value
}

class CountryHomeInput {
    String locale
}

class Country {
    String id
    String name
    String flag
    LegalUrlsCountry legalUrls
}

enum CountryFlagSize implements ImageSizeEnum {
    SIZE_30x20, SIZE_60x40, SIZE_120x80

    @Override
    String value() {
        name().substring("SIZE_".length())
    }
}

class LegalUrlsCountry {
    String tyc
    String pp
    String cookies
    String faqs
}