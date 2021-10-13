package bff.bridge.data

import bff.model.ContactInfo
import bff.model.Country
import bff.model.CountryTranslation
import bff.model.Currency
import bff.model.Customer
import bff.model.CustomerStatus
import bff.model.CustomerType
import bff.model.Day
import bff.model.Detail
import bff.model.Fee
import bff.model.HourRange
import bff.model.Language
import bff.model.LegalDocumentInformation
import bff.model.LegalUrl
import bff.model.LegalUrlType
import bff.model.TimestampOutput
import bff.model.User
import bff.model.WabiPay
import bff.model.WorkingDays

import java.time.Instant

abstract class CustomerBridgeImplTestData {
    protected static final Customer CUSTOMER = new Customer(
            id: 1,
            name: "name",
            enabled: true,
            legalId: "legalId",
            linePhone: "linePhone",
            customerStatus: CustomerStatus.APPROVED,
            user: new User(id: 2, created: new TimestampOutput(Instant.now().toString())),
            smsVerification: false,
            emailVerification: true,
            workingDays: new WorkingDays(days: [new Day(dayIndex: 0, selected: true)], hours: [new HourRange(from: "from 1", to: "to 1")]),
            country_id: "1",
            country: new Country(
                    id: "countryId",
                    name: "countryName",
                    flag: "SIZE_30x20",
                    legalUrls: [new LegalUrl(type: LegalUrlType.ABOUT, value: "value", label: "label")],
                    detail: new Detail(countryCode: "ar", timezone: "arTZ"),
                    language: new Language(language: "language", locale: "locale", direction: "direction",
                            translations: [new CountryTranslation(language: "ar", value: "langValue")]),
                    contactInfo: new ContactInfo(),
                    currency: new Currency(symbol: "symbol", code: "code"),
                    fee: new Fee(serviceFeeType: "serviceFeeType", serviceFee: BigDecimal.ZERO, displayFeeOnSupplierAdm: false),
                    wabiPay: new WabiPay(enabled: false, creditEnabled: false, moneyEnabled: false, wcToMoneyWhenReleasingEnabled: false),
                    legalDocumentInformation: new LegalDocumentInformation(id: "legalDocId", mask: "mask")
            ),
            customerType: new CustomerType(id: "customerTypeId", code: "customerTypeCode"),
            marketingEnabled: false
    )
}
