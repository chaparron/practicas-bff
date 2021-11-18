package bff.model

class SupplierLeadInput {
    String countryId
    String businessName
    Boolean haveDistribution
    String city
    String contactName
    String contactPhoneNumber
    String contactEmail
    HowMeetUs howMeetUs
}

class HowMeetUs {
    String type
    String detail
}
