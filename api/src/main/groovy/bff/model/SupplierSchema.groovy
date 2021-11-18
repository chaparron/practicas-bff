package bff.model

class SupplierLeadInput {
    String countryId
    String businessName
    Boolean haveDistribution
    String city
    String contactName
    String contactPhoneNumber
    String contactEmail
    HowHearAboutUs howHearAboutUs
    String howHearAboutUsDetail
}

enum HowHearAboutUs{
    ADVERTISING,
    INTERNET,
    SOCIAL_MEDIA,
    RECOMMENDED,
    SALES_REPRESENTATIVE,
    OTHER
}
