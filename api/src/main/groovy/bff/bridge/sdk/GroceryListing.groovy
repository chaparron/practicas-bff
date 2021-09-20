package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.bridge.SearchBridge
import bff.model.*
import scala.Option
import wabi2b.grocery.listing.sdk.*

import static bff.model.SortInput.ASC
import static java.util.Optional.ofNullable
import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.CollectionConverters.asScala
import static scala.jdk.javaapi.OptionConverters.toJava
import static wabi2b.grocery.listing.sdk.ProductQueryRequest.availableProductsIn

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
        def deliveryAddress = customer.preferredDeliveryAddress()
        def request =
                ([
                        termFiltering(input.keyword),
                        brandFiltering(input.brand),
                        categoryFiltering(input.category),
                        supplierFiltering(input.supplier),
                        promotionFiltering(input.tag)
                ]
                        .findAll { it.isPresent() }
                        .collect { it.get() } +
                        featuresFiltering(input.features)
                )
                        .inject(
                                availableProductsIn(
                                        new Coordinate(
                                                deliveryAddress.lat.toDouble(),
                                                deliveryAddress.lon.toDouble()
                                        )
                                )
                                        .forCustomer(customer.id.toString(), customer.customerType.code)
                                        .sized(input.size)
                                        .aggregatedByBrands(10)
                                        .aggregatedByCategories(1, false)
                                        .aggregatedBySuppliers(10)
                                        .aggregatedByFeatures(),
                                { acc, filter -> filter(acc) }
                        )


        def response = sdk.query(request.offset((input.page - 1) * input.size))
        return new SearchResult(
                header: new Header(
                        total: response.total().toInteger(),
                        pageSize: input.size,
                        currentPage: input.page
                ),
                sort: new Sort(
                        field: input.sort ?: "DEFAULT",
                        direction: (input.sortDirection ?: ASC).name()
                ),
                breadcrumb: [],
                filters: [],
                facets: facets(request, response),
                products: asJava(response.hits()).collect {
                    def prices = prices(it)
                    new ProductSearch(
                            id: it.id().toLong(),
                            name: it.name().defaultEntry(),
                            category: category(it),
                            brand: brand(it),
                            ean: ean(it),
                            description: toJava(it.description()).map { it.defaultEntry() }.orElse(null),
                            images: images(it),
                            displays: displays(it),
                            prices: prices,
                            minUnitsPrice: prices.min { it.minUnits },
                            highlightedPrice: prices.min { it.unitValue },
                            title: it.name().defaultEntry()
                    )
                }
        )
    }

    @Override
    SearchResponse previewSearch(PreviewSearchInput input) {
        def request =
                ([
                        termFiltering(input.keyword),
                        brandFiltering(input.brand),
                        categoryFiltering(input.category),
                        promotionFiltering(input.tag)
                ]
                        .findAll { it.isPresent() }
                        .collect { it.get() } +
                        featuresFiltering(input.features)
                )
                        .inject(
                                availableProductsIn(
                                        new Coordinate(
                                                input.lat.toDouble(),
                                                input.lng.toDouble()
                                        )
                                )
                                        .sized(input.size)
                                        .aggregatedByBrands(10)
                                        .aggregatedByCategories(1, false)
                                        .aggregatedByFeatures(),
                                { acc, filter -> filter(acc) }
                        )


        def response = sdk.query(request.offset((input.page - 1) * input.size))
        return new PreviewSearchResult(
                header: new Header(
                        total: response.total().toInteger(),
                        pageSize: input.size,
                        currentPage: input.page
                ),
                sort: new Sort(
                        field: input.sort ?: "DEFAULT",
                        direction: (input.sortDirection ?: ASC).name()
                ),
                breadcrumb: [],
                filters: [],
                facets: facets(request, response),
                products: asJava(response.hits()).collect {
                    new PreviewProductSearch(
                            id: it.id().toLong(),
                            name: it.name().defaultEntry(),
                            enabled: true,
                            category: category(it),
                            brand: brand(it),
                            ean: ean(it),
                            description: toJava(it.description()).map { it.defaultEntry() }.orElse(null),
                            images: images(it),
                            created: null,
                            manufacturer: manufacturer(it),
                            prices: prices(it).collect {
                                new PreviewPrice(
                                        id: it.id,
                                        value: it.value,
                                        unitValue: it.unitValue,
                                        display: it.display,
                                        minUnits: it.minUnits
                                )
                            },
                            suppliers: suppliers(it).collect {
                                new PreviewSupplier(
                                        id: it.id,
                                        name: it.name,
                                        legalName: it.legalName,
                                        avatar: it.avatar
                                )
                            },
                            title: it.name().defaultEntry(),
                            country_id: null
                    )
                }
        )
    }

    private static List<Closure<ProductQueryRequest>> featuresFiltering(List<FeatureInput> features) {
        features.collect { feature ->
            { ProductQueryRequest r ->
                r.filteredByFeature(feature.id, feature.value, asScala([] as List<String>).toSeq())
            }
        }
    }

    private static Optional<Closure<ProductQueryRequest>> promotionFiltering(String maybePromotion) {
        ofNullable(maybePromotion).map { promotion ->
            { ProductQueryRequest r -> r.filteredByPromotion(promotion) }
        }
    }

    private static Optional<Closure<ProductQueryRequest>> supplierFiltering(Integer maybeSupplier) {
        ofNullable(maybeSupplier).map { supplier ->
            { ProductQueryRequest r -> r.filteredBySupplier(supplier.toString(), asScala([] as List<String>).toSeq()) }
        }
    }

    private static Optional<Closure<ProductQueryRequest>> categoryFiltering(Integer maybeCategory) {
        ofNullable(maybeCategory).map { category ->
            { ProductQueryRequest r -> r.filteredByCategory(category.toString(), asScala([] as List<String>).toSeq()) }
        }
    }

    private static Optional<Closure<ProductQueryRequest>> brandFiltering(Integer maybeBrand) {
        ofNullable(maybeBrand).map { brand ->
            { ProductQueryRequest r -> r.filteredByBrand(brand.toString(), asScala([] as List<String>).toSeq()) }
        }
    }

    private static Optional<Closure<ProductQueryRequest>> termFiltering(String maybeTerm) {
        ofNullable(maybeTerm).map { term ->
            { ProductQueryRequest r -> r.filteredByTerm(term, Option.empty(), TypeAhead$.MODULE$) }
        }
    }

    private static Category category(AvailableProduct product) {
        new Category(
                id: product.categorization().last().id().toLong(),
                parentId: toJava(product.categorization().last().parent())
                        .map { it.toLong() }
                        .orElse(null),
                name: product.categorization().last().name().defaultEntry(),
                enabled: true,
                isLeaf: true
        )
    }

    private static Price price(AvailableOption option) {
        new Price(
                id: option.id() as Integer,
                supplier: supplier(option),
                value: option.price().toBigDecimal(),
                unitValue: option.price() / option.display().units(),
                minUnits: option.requiredPurchaseUnits()._1() as Integer,
                maxUnits: toJava(option.requiredPurchaseUnits()._2()).map { it as Integer }.orElse(null),
                display: display(option),
                configuration: null
        )
    }

    private static Supplier supplier(AvailableOption option) {
        new Supplier(
                id: option.supplier().id().toInteger(),
                name: option.supplier().name(),
                legalName: null,
                avatar: toJava(option.supplier().avatar()).orElse(null)
        )
    }

    private static Display display(AvailableOption option) {
        new Display(
                id: option.display().id().toInteger(),
                ean: option.display().ean(),
                units: option.display().units()
        )
    }

    private static List<Price> prices(AvailableProduct product) {
        asJava(product.options()).collect { price(it) }
    }

    private static List<Display> displays(AvailableProduct product) {
        asJava(product.options()).collect { display(it) }
    }

    private static List<Supplier> suppliers(AvailableProduct product) {
        asJava(product.options()).collect { supplier(it) }
    }

    private static List<Image> images(AvailableProduct product) {
        asJava(product.images().zipWithIndex()).collect {
            new Image(
                    id: it._1()
            )
        }
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
                name: product.brand().name().defaultEntry()
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
                                    name: it._2().name().defaultEntry(),
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
