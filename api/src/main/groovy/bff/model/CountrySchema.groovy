package bff.model

import bff.service.ImageSizeEnum

class Country {
    String id
    String name
    String flag
    LegalUrlsCountry legalUrls
    Detail detail
    Language language
    ContactInfo contactInfo
    Currency currency
    Fee fee
    WabiPay wabiPay
}

class Detail {
    String phonePrefix
    String countryCode
}

class Language{
    String language
    String locale
    List<CountryTranslation> translations = []
}

class ContactInfo {
    String whatsappNumber
    String phoneNumber
    String direction
}

class Currency {
    String currencySymbol
    String currencyCode
}

class CountryTranslation {
    String name
    String language
    String value
}

class WabiPay {
    Boolean wabiPayEnabled
    Boolean wabiPayCreditEnabled
    Boolean wabiPayMoneyEnabled
    Boolean wabiPayWcToMoneyWhenReleasingEnabled
}

class Fee{
    String serviceFeeType
    BigDecimal serviceFee
    Boolean displayFeeOnSupplierAdm
}

class CountryConfigurationEntry {
    String key
    String value
}

class CountryHomeInput {
    String locale
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