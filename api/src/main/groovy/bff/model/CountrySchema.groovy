package bff.model

import bff.service.ImageSizeEnum

class CountryNotFoundException extends RuntimeException {

    @Override
    String getMessage() {
        return "Country not found"
    }

}

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
    LegalDocumentInformation legalDocumentInformation
    Geolocation geolocation
    Boolean customerBranchesEnabled
    Channels channels
}

class Detail {
    String countryCode
    String timezone
}

class Language {
    String language
    String locale
    String direction
    List<CountryTranslation> translations = []
}

class ContactInfo {
    @Deprecated String whatsappNumber
    String phoneNumber
    String whatsAppNumber
    String zaloNumber
    String lineUserId
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

class Fee {
    String serviceFeeType
    BigDecimal serviceFee
    Boolean displayFeeOnSupplierAdm
}

class LegalDocumentInformation {
    String id
    String mask
    String maskRegex
    List<LegalDocumentFile> legalDocumentFiles
}

class LegalDocumentFile{
    String id
    String label
}
class LegalUrl {
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

class Geolocation{
    Double lat
    Double lng
}

class Channels{
    List<ChannelType> notifications
    List<ChannelType> otp
}

enum LegalUrlType {
    PRIVACY_POLICY,
    TERMS_AND_CONDITIONS,
    COOKIES,
    FAQS,
    ABOUT,
    OPERATION,
    COMPLAINT
}

enum CountryFlagSize implements ImageSizeEnum {
    SIZE_30x20, SIZE_60x40, SIZE_120x80

    @Override
    String value() {
        name().substring("SIZE_".length())
    }
}