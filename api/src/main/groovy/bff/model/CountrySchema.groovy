package bff.model

import bff.service.ImageSizeEnum

interface CountryConfigurationResponse {}

enum CountryConfigurationFailedResult {
    COUNTRY_NOT_FOUND

    def build() {
        return new CountryConfigurationFailed(reason: this)
    }
}

class CountryConfigurationFailed implements CountryConfigurationResponse {
    CountryConfigurationFailedResult reason
}

class CountryConfiguration implements CountryConfigurationResponse {

    CountryBasicInfo countryBasicInfo
    CountryContactInfo countryContactInfo
    CountryCurrency currency
    LegalUrlsCountry legalUrlsCountry
    CountryPaymentInfo countryPayment
    List<CountryTranslation> translations = []
}

class CountryBasicInfo {

    String language
    String locale
    String countryCode
    BigDecimal lat
    BigDecimal lng
    String flag
}

class CountryContactInfo {

    String whatsappNumber
    String phoneNumber
    String direction
}

class CountryCurrency {

    String currencySymbol
    String currencyCode
}

class CountryTranslation {
    String name
    String value
}

class CountryPaymentInfo {
    Boolean wabiPayEnabled
    Boolean wabiPayCreditEnabled
    Boolean wabiPayMoneyEnabled
    Boolean wabiPayWcToMoneyWhenReleasingEnabled
    Boolean displayFeeOnSupplierAdm
    String serviceFeeType
    BigDecimal serviceFee
}

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