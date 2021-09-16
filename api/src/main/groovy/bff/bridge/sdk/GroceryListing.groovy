package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.bridge.SearchBridge
import bff.model.*
import wabi2b.grocery.listing.sdk.*

import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.OptionConverters.toJava
import static wabi2b.grocery.listing.sdk.ProductQueryRequest.availableProductsFor

class GroceryListing implements SearchBridge {

    private Sdk sdk
    private CustomerBridge customerBridge

    @Override
    SearchResult search(SearchInput input) {
        return searchV2(input) as SearchResult
    }

    @Override
    SearchResponse searchV2(SearchInput input) {
        def customer = customerBridge.myProfile(input.accessToken)
        def deliveryAddress = customer.addresses.find { it.addressType == AddressMode.DELIVERY }
        def request = availableProductsFor(
                new Coordinate(
                        deliveryAddress.lat.toDouble(),
                        deliveryAddress.lon.toDouble()
                ),
                customer.id.toString(),
                customer.customerType.code
        )
                .sized(input.size)
                .aggregatedByBrands(10)
                .aggregatedByCategories(1, false)
                .aggregatedBySuppliers(10)
                .aggregatedByFeatures()
        def response = sdk.query(request.offset(input.page * input.size))
        return new SearchResult(
                header: new Header(
                        total: response.total().toInteger(),
                        pageSize: input.size,
                        currentPage: input.page
                ),
                sort: new Sort(
                        field: input.sort,
                        direction: input.sortDirection.name()
                ),
                breadcrumb: [],
                filters: [],
                facets: facets(request, response),
                products: asJava(response.hits()).collect {
                    def prices = prices(it)
                    new ProductSearch(
                            accessToken: null,
                            id: it.id().toLong(),
                            name: it.name().defaultEntry(),
                            enabled: true,
                            category: category(it),
                            brand: brand(it),
                            ean: ean(it),
                            description: toJava(it.description()).map { it.defaultEntry()}.orElse(null),
                            keywords: [],
                            features: [],
                            images: images(it),
                            displays: displays(it),
                            created: null,
                            manufacturer: manufacturer(it),
                            prices: prices,
                            priceFrom: prices.min { it.unitValue },
                            minUnitsPrice: prices.min { it.minUnits },
                            highlightedPrice: null,
                            title: it.name().defaultEntry(),
                            favorite: null,
                            country_id: null
                    )
                }
        )
    }

    @Override
    SearchResponse previewSearch(PreviewSearchInput input) {
        return null
    }

    private static Category category(AvailableProduct product) {
        new Category(
                id: product.categorization().last().id().toLong(),
                parentId: toJava(product.categorization().last().parent())
                        .map { it.toLong() }
                        .orElse(null),
                name: product.categorization().last().name().defaultEntry(),
                enabled: true,
                isLeaf: true,
                country_id: null
        )
    }

    private static List<Price> prices(AvailableProduct product) {
        asJava(product.options()).collect {
            new Price(
                    id: null,
                    accessToken: null,
                    supplier: new Supplier(
                            accessToken: null,
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
                    minUnits: it.requiredPurchaseUnits()._1() as Integer,
                    maxUnits: toJava(it.requiredPurchaseUnits()._2())
                            .map { product as Integer }.orElse(null),
                    display: new Display(
                            id: it.display().id().toInteger(),
                            ean: it.display().ean(),
                            units: it.display().units()
                    ),
                    updated: null,
                    promotions: [],
                    configuration: null
            )
        }
    }

    private static List<Display> displays(AvailableProduct product) {
        asJava(product.options()).collect {
            new Display(
                    id: it.display().id().toInteger(),
                    ean: it.display().ean(),
                    units: it.display().units()
            )
        }
    }

    private static List<Image> images(AvailableProduct product) {
        asJava(product.images().zipWithIndex()).collect {
            new Image(
                    id: it._1(),
                    originalName: null,
                    added: null,
                    imageOrder: it._2() as Integer
            )
        }
    }

    private static Manufacturer manufacturer(AvailableProduct product) {
        new Manufacturer(
                id: product.manufacturer().id().toLong(),
                name: product.manufacturer().name(),
                enabled: true,
                phone: null,
                avatar: toJava(product.manufacturer().avatar()).orElse(null),
                country_id: null
        )
    }

    private static String ean(AvailableProduct product) {
        asJava(product.options())
                .collect { it.display() }
                .find { it.units() == 1 }
                ?.ean()
    }

    private static Brand brand(AvailableProduct product) {
        new Brand(
                id: product.brand().id().toLong(),
                name: product.brand().name().defaultEntry(),
                enabled: true,
                logo: toJava(product.brand().logo()).orElse(null),
                country_id: null
        )
    }

    private static List<Facet> facets(ProductQueryRequest request, ProductQueryResponse response) {
        [
                // categories facet
                toJava(response.aggregations().categories())
                        .filter { request.filtering().byCategory().isEmpty() }
                        .map {
                            new Facet(
                                    id: "category",
                                    name: "category",
                                    slices: asJava(it.hits()).collect {
                                        new Slices(
                                                size: it._2() as Long,
                                                obj: new Slice(
                                                        id: it._1().id(),
                                                        name: it._1().name().defaultEntry(),
                                                        key: it._1().id()
                                                )
                                        )
                                    }
                            )
                        },
                // brands facet
                toJava(response.aggregations().brands())
                        .filter { request.filtering().byBrand().isEmpty() }
                        .map {
                            new Facet(
                                    id: "brand",
                                    name: "brand",
                                    slices: asJava(it.hits()).collect {
                                        new Slices(
                                                size: it._2() as Long,
                                                obj: new Slice(
                                                        id: it._1().id(),
                                                        name: it._1().name().defaultEntry(),
                                                        key: it._1().id()
                                                )
                                        )
                                    }
                            )
                        },
                // suppliers facet
                toJava(response.aggregations().suppliers())
                        .filter { request.filtering().bySupplier().isEmpty() }
                        .map {
                            new Facet(
                                    id: "supplier",
                                    name: "supplier",
                                    slices: asJava(it.hits()).collect {
                                        new Slices(
                                                size: it._2() as Long,
                                                obj: new Slice(
                                                        id: it._1().id(),
                                                        name: it._1().name(),
                                                        key: it._1().id()
                                                )
                                        )
                                    }
                            )
                        }
        ]
                .findAll { it.isPresent() }
                .collect { it.get() } +
                // features facets
                toJava(response.aggregations().features().map { asJava(it.features().toList()) })
                        .orElse([])
                        .findAll { t -> !request.filtering().byFeatures().exists { it.contains(t._1()) } }
                        .collect {
                            new Facet(
                                    id: "feature_" + it._1(),
                                    name: "feature_" + it._2().name().defaultEntry(),
                                    slices: asJava(it._2().hits()).collect {
                                        new Slices(
                                                size: it._2() as Long,
                                                obj: new Slice(
                                                        id: it._1().id(),
                                                        name: it._1().toString(),
                                                        key: it._1().id()
                                                )
                                        )
                                    }
                            )
                        }
    }

}
