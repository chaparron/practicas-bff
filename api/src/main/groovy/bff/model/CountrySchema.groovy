package bff.model

import bff.service.ImageSizeEnum

class Country {
    String id
    String name
    String flag
    List<LegalUrl> legalUrls = []
    Detail detail
    Language language
    ContactInfo contactInfo
    Currency currency
    Fee fee
    WabiPay wabiPay
}

class Detail {
    String countryCode
}

class Language{
    String language
    String locale
    String direction
    List<CountryTranslation> translations = []
}

class ContactInfo {
    String whatsappNumber
    String phoneNumber
}

class Currency {
    String symbol
    String code
}

class CountryTranslation {
    String language
    String value
}

class WabiPay {
    Boolean enabled
    Boolean creditEnabled
    Boolean moneyEnabled
    Boolean wcToMoneyWhenReleasingEnabled
}

class Fee{
    String serviceFeeType
    BigDecimal serviceFee
    Boolean displayFeeOnSupplierAdm
}

class LegalUrl{
    LegalUrlType type
    String value
    String label
}

class CountryConfigurationEntry {
    String key
    String value
}

class CountryHomeInput {
    String locale
}

enum LegalUrlType {
    PRIVACY_POLICY,
    TERMS_AND_CONDITIONS,
    COOKIES,
    FAQS
}

enum CountryFlagSize implements ImageSizeEnum {
    SIZE_30x20, SIZE_60x40, SIZE_120x80

    @Override
    String value() {
        name().substring("SIZE_".length())
    }
}