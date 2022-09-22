package bff.bridge.data

import wabi2b.sdk.regional.ContactInformation
import wabi2b.sdk.regional.Country
import wabi2b.sdk.regional.CountryServiceResponse
import wabi2b.sdk.regional.CurrencyInformation
import wabi2b.sdk.regional.FeeConfiguration
import wabi2b.sdk.regional.Geolocation
import wabi2b.sdk.regional.KeyValueResponse
import wabi2b.sdk.regional.Language
import wabi2b.sdk.regional.LegalDocumentFile
import wabi2b.sdk.regional.LegalDocumentInformation
import wabi2b.sdk.regional.LegalLink
import wabi2b.sdk.regional.ShoppingConfiguration
import wabi2b.sdk.regional.Translation
import wabi2b.sdk.regional.WabipayConfiguration

abstract class CountryGatewayBridgeImplTestData {
    protected static CountryServiceResponse countryServiceResponseEs =
            new CountryServiceResponse(
                    "es",
                    [
                            new KeyValueResponse("name", "España"),
                            new KeyValueResponse("name-en", "Spain"),
                            new KeyValueResponse("locale", "es_ES"),
                    ]
            )

    protected static Country regionalCountryEs =
            new Country(
                    "es",
                    "España",
                    new ContactInformation("+541120400002", "541161290635", "0862000780"),
                    new CurrencyInformation("€", "EUR"),
                    "Europe/España/Madrid",
                    "7ab0fd14-efa9-11eb-9a03-0242ac1300ar.png",
                    "+94",
                    [new LegalLink("tyc.com", "tyc")],
                    new LegalDocumentInformation(
                            "CUIT",
                            "999999999999",
                            "^\\\\d{1,12}\$",
                            [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]
                    ),
                    new FeeConfiguration("WABICREDITS_PERCENTAGE", 1),
                    new Language(
                            "es",
                            "es_ES",
                            "ltr",
                            [new Translation("en", "Spain")]
                    ),
                    new WabipayConfiguration(true, true, true),
                    new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                    new ShoppingConfiguration(true)
            )

    protected static Country regionalCountryAr =
            new Country(
                    "ar",
                    "Argentina",
                    new ContactInformation("+541120400002", "541161290635", "0862000780"),
                    new CurrencyInformation("€", "EUR"),
                    "America/Argentina/Buenos Aires",
                    "7ab0fd14-efa9-11eb-9a03-0242ac1300ar.png",
                    "+54",
                    [new LegalLink("tyc.com", "tyc")],
                    new LegalDocumentInformation(
                            "CUIT",
                            "999999999999",
                            "^\\\\d{1,12}\$",
                            [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]
                    ),
                    new FeeConfiguration("WABICREDITS_PERCENTAGE", 1),
                    new Language(
                            "es",
                            "es_AR",
                            "ltr",
                            [new Translation("en", "Argentina")]
                    ),
                    new WabipayConfiguration(true, true, true),
                    new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                    new ShoppingConfiguration(true)
            )

    protected static List<Country> homeCountriesResponse =
            [
                    new Country(
                            "eg",
                            "Egipto",
                            new ContactInformation("xxx", "xxx", "xxx"),
                            new CurrencyInformation("xxx", "xxx"),
                            "Africa/Cairo",
                            "xxx",
                            "xxx",
                            [
                                    new LegalLink("tyc.com", "tyc"),
                                    new LegalLink("pp.com", "pp"),
                                    new LegalLink("cookies.com", "cookies"),
                                    new LegalLink("faqs.com", "faqs"),
                                    new LegalLink("about.com", "about"),
                                    new LegalLink("operation.com", "operation"),
                                    new LegalLink("complaint.com", "complaint")
                            ],
                            new LegalDocumentInformation("TIN", "D*", "^[a-zA-Z0-9]*\$", [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]),
                            new FeeConfiguration("xxx", 1),
                            new Language("xxx", "xxx", "xxx",
                                    [
                                            new Translation("ar", "مصر"),
                                            new Translation("en", "Egypt"),
                                            new Translation("es", "Egipto")
                                    ]
                            ),
                            new WabipayConfiguration(true, true, true),
                            new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                            new ShoppingConfiguration(true)
                    ),
                    new Country(
                            "ph",
                            "Philippines",
                            new ContactInformation("xxx", "xxx", "xxx"),
                            new CurrencyInformation("xxx", "xxx"),
                            "Asia/Manila",
                            "xxx",
                            "xxx",
                            [new LegalLink("tyc.com", "tyc")],
                            new LegalDocumentInformation("TIN", "000000009999", "^\\d{8,12}\$", [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]),
                            new FeeConfiguration("xxx", 1),
                            new Language("xxx", "xxx", "xxx",
                                    [
                                            new Translation("ar", "فيلبيني"),
                                            new Translation("en", "Philippines"),
                                            new Translation("es", "Filipinas")
                                    ]
                            ),
                            new WabipayConfiguration(true, true, true),
                            new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                            new ShoppingConfiguration(true)
                    ),
                    new Country(
                            "ma",
                            "Morocco",
                            new ContactInformation("xxx", "xxx", "xxx"),
                            new CurrencyInformation("xxx", "xxx"),
                            "Africa/Casablanca",
                            "xxx",
                            "xxx",
                            [],
                            new LegalDocumentInformation("ICE", "000000000000000", "^\\d{15}\$", [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]),
                            new FeeConfiguration("xxx", 1),
                            new Language("xxx", "xxx", "xxx",
                                    [
                                            new Translation("ar", "المغرب"),
                                            new Translation("en", "Morocco"),
                                            new Translation("es", "Marruecos")
                                    ]
                            ),
                            new WabipayConfiguration(true, true, true),
                            new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                            new ShoppingConfiguration(true)
                    )
            ]

    protected static final publicCountryResponse =
            new Country(
                    "ru",
                    "Rusia",
                    new ContactInformation("xxx", "xxx", "xxx"),
                    new CurrencyInformation("xxx", "xxx"),
                    "xxx",
                    "xxx",
                    "+7",
                    [new LegalLink("xxx", "xxx")],
                    new LegalDocumentInformation("xxx", "xxx", "xxx", [new LegalDocumentFile("legalIdFront", "Imagen DNI (frente)")]),
                    new FeeConfiguration("WABICREDITS_PERCENTAGE", 1),
                    new Language("xxx", "ru-RU", "xxx",
                            [
                                    new Translation("l1", "xxx"),
                                    new Translation("l2", "xxx"),
                                    new Translation("l3", "xxx"),
                                    new Translation("l4", "xxx"),
                                    new Translation("l5", "xxx"),
                                    new Translation("l6", "xxx"),
                                    new Translation("l7", "xxx"),
                                    new Translation("l8", "xxx"),
                            ]
                    ),
                    new WabipayConfiguration(true, true, true),
                    new Geolocation(new Double(-38.416097), new Double(-63.616672)),
                    new ShoppingConfiguration(true)
            )
}
