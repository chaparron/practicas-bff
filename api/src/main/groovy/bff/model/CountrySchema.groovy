package bff.model

import bff.service.ImageSizeEnum

class CountryConfigurationEntry {
    String key
    String value
}

class CountryHomeResponse {
    String id
    String name
    String flag
    LegalUrlsCountry legalUrls
}

enum CountryFlagSize implements ImageSizeEnum {
    SIZE_50x50

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
    String tycSupplier
    String ppSupplier
}