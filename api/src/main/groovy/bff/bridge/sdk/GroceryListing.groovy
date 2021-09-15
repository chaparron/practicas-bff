package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.bridge.SearchBridge
import bff.model.*
import wabi2b.grocery.listing.sdk.Coordinate
import wabi2b.grocery.listing.sdk.Sdk
import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.OptionConverters.toJava

import static wabi2b.grocery.listing.sdk.ProductQueryRequest.availableProductsFor

class GroceryListing implements SearchBridge {

    private Sdk sdk
    private CustomerBridge customerBridge

    @Override
    SearchResult search(SearchInput searchInput) {
        return null
    }

    @Override
    SearchResponse searchV2(SearchInput searchInput) {
        def customer = customerBridge.myProfile(searchInput.accessToken)
        def deliveryAddress = customer.addresses.find { it.addressType == AddressMode.DELIVERY }
        def request = availableProductsFor(
                new Coordinate(
                        deliveryAddress.lat.toDouble(),
                        deliveryAddress.lon.toDouble()
                ),
                customer.id.toString(),
                customer.customerType.code
        )
                .sized(searchInput.size)
                .aggregatedByBrands(10)
                .aggregatedByCategories(1, false)
                .aggregatedBySuppliers(10)
                .aggregatedByFeatures()
        def response = sdk.query(request.offset(searchInput.page * searchInput.size))
        return new SearchResult(
                products: asJava(response.hits()).collect {
                    new ProductSearch(
                            accessToken: searchInput.accessToken,
                            id: it.id().toLong(),
                            name: it.name(),
                            enabled: true,
                            category: new Category(
                                    id: it.categorization().last().id().toLong(),
                                    parentId: toJava(it.categorization().last().parent())
                                            .map { it.toLong() }.orElse(null),
                                    name: it.categorization().last().name(),
                                    enabled: true,
                                    isLeaf: true,
                                    country_id: null
                            ),
                            brand: new Brand(
                                    id: it.brand().id().toLong(),
                                    name: it.brand().name(),
                                    enabled: true,
                                    logo: toJava(it.brand().logo()).orElse(null),
                                    country_id: null
                            ),
                            ean: null,
                            description: toJava(it.description()).orElse(null),
                            keywords: null,
                            features: null,
                            images: asJava(it.images().zipWithIndex()).collect {
                                new Image(
                                        id: it._1,
                                        originalName: null,
                                        added: null,
                                        imageOrder: it._2 as Integer
                                )
                            },
                            displays: asJava(it.options()).collect {
                                new Display(
                                        id: it.display().id().toInteger(),
                                        ean: it.display().ean(),
                                        units: it.display().units()
                                )
                            },
                            created: null,
                            manufacturer: new Manufacturer(
                                    id: it.manufacturer().id().toLong(),
                                    name: it.manufacturer().name(),
                                    enabled: true,
                                    phone: null,
                                    avatar: toJava(it.manufacturer().avatar()).orElse(null),
                                    country_id: null
                            ),
                            prices: asJava(it.options()).collect {
                                new Price(
                                        id: null,
                                        accessToken: searchInput.accessToken,
                                        supplier: new Supplier(
                                                accessToken: searchInput.accessToken,
                                                id: it.supplier().id().toInteger(),
                                                name: it.supplier().name(),
                                                legalName: null,
                                                avatar: it.supplier().avatar().orElse(null),
                                                enabled: true,
                                                phone: null,
                                                legalId: null,
                                                address: null,
                                                postalCode: null,
                                                maxAmount: null,
                                                minAmount: null,
                                                deliveryZones: [],
                                                rating: null,
                                                country_id: null,
                                                averageDeliveryDay: null,
                                                orderPercentageCap: null,
                                                wabipayConfiguration: null
                                        ),
                                        value: it.price().toBigDecimal(),
                                        unitValue: it.price() / it.display().units(),
                                        enabled: true,
                                        minUnits: it.requiredPurchaseUnits()._1 as Integer,
                                        maxUnits: toJava(it.requiredPurchaseUnits()._2)
                                                .map { it as Integer }.orElse(null),
                                        display: new Display(
                                                id: it.display().id().toInteger(),
                                                ean: it.display().ean(),
                                                units: it.display().units()
                                        ),
                                        updated: null,
                                        promotions: [],
                                        configuration: null
                                )
                            },
                            priceFrom: null,
                            minUnitsPrice: null,
                            highlightedPrice: null,
                            title: null,
                            favorite: null,
                            country_id: null
                    )
                },
                breadcrumb: [],
                sort: new Sort(
                        field: searchInput.sort,
                        direction: searchInput.sortDirection.name()
                ),
                header: null,
                facets: [],
                filters: []
        )
    }

    @Override
    SearchResponse previewSearch(PreviewSearchInput searchInput) {
        return null
    }

}
