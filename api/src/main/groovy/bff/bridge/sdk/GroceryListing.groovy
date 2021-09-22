package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.bridge.SearchBridge
import bff.model.*
import scala.Option
import wabi2b.grocery.listing.sdk.*

import static bff.model.SortInput.DESC
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
        searchV2(input) as SearchResult
    }

    @Override
    SearchResponse searchV2(SearchInput input) {
        def customer = customerBridge.myProfile(input.accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()
        def request =
                ([
                        sorting(input.sort, input.sortDirection, input.keyword),
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
                sort: sort(request),
                breadcrumb: breadCrumb(request, response),
                filters: filters(request, response),
                facets: facets(request, response),
                products: products(response)
        )
    }

    @Override
    SearchResponse previewSearch(PreviewSearchInput input) {
        def request =
                ([
                        sorting(input.sort, input.sortDirection, input.keyword),
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
                sort: sort(request),
                breadcrumb: breadCrumb(request, response),
                filters: filters(request, response),
                facets: facets(request, response),
                products: products(response).collect {
                    new PreviewProductSearch(
                            id: it.id,
                            name: it.name,
                            category: it.category,
                            brand: it.brand,
                            ean: it.ean,
                            description: it.description,
                            images: it.images,
                            prices: it.prices.collect {
                                new PreviewPrice(
                                        id: it.id,
                                        value: it.value,
                                        unitValue: it.unitValue,
                                        display: it.display,
                                        minUnits: it.minUnits
                                )
                            },
                            suppliers: it.prices.collect { it.supplier }.toSet().toList().collect {
                                new PreviewSupplier(
                                        id: it.id,
                                        name: it.name,
                                        avatar: it.avatar
                                )
                            },
                            title: it.title
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

    private static Optional<Closure<ProductQueryRequest>> sorting(String maybeSort,
                                                                  SortInput maybeDirection,
                                                                  String maybeKeyword) {
        ofNullable(maybeSort)
                .map { sort ->
                    { ProductQueryRequest r ->
                        switch (sort) {
                            case "DEFAULT":
                            case "TITLE":
                                r.sortedAlphabetically(
                                        ofNullable(maybeDirection).map { direction ->
                                            switch (direction) {
                                                case DESC:
                                                    false
                                                    break
                                                default:
                                                    true
                                                    break
                                            }
                                        }.orElse(true),
                                        Option.empty()
                                )
                                break
                            case "RECENT":
                                r.sortedByLastAvailabilityUpdate()
                                break
                            case "PRICE":
                                r.sortedByUnitPrice(
                                        ofNullable(maybeDirection).map { direction ->
                                            switch (direction) {
                                                case DESC:
                                                    false
                                                    break
                                                default:
                                                    true
                                                    break
                                            }
                                        }.orElse(true)
                                )
                                break
                            default:
                                r
                                break
                        }
                    }
                }
                .map { Optional.of(it) }
                .orElseGet {
                    ofNullable(maybeKeyword).map { { ProductQueryRequest r -> r.sortedByRelevance() } }
                }
    }

    private static List<ProductSearch> products(ProductQueryResponse response) {
        asJava(response.hits()).collect {
            def prices = asJava(it.options()).collect { price(it) }
            def displays = asJava(it.options()).collect { display(it) }.toSet().toList()
            new ProductSearch(
                    id: it.id().toLong(),
                    name: it.name().defaultEntry(),
                    category: new Category(
                            id: it.categorization().last().id().toLong(),
                            parentId: toJava(it.categorization().last().parent())
                                    .map { it.toLong() }.orElse(null),
                            name: it.categorization().last().name().defaultEntry(),
                            enabled: true,
                            isLeaf: true
                    ),
                    brand: new Brand(
                            id: it.brand().id().toLong(),
                            name: it.brand().name().defaultEntry()
                    ),
                    ean: displays.find { it.units == 1 }?.ean,
                    description: toJava(it.description()).map { it.defaultEntry() }.orElse(null),
                    images: asJava(it.images().zipWithIndex()).collect { new Image(id: it._1()) },
                    displays: displays,
                    prices: prices,
                    minUnitsPrice: prices.min { it.minUnits },
                    highlightedPrice: prices.min { it.unitValue },
                    title: it.name().defaultEntry()
            )
        }
    }

    private static Price price(AvailableOption option) {
        new Price(
                id: option.id() as Integer,
                supplier: supplier(option),
                value: option.price().toBigDecimal(),
                unitValue: option.price() / option.display().units(),
                minUnits: option.requiredPurchaseUnits()._1() as Integer,
                maxUnits: toJava(option.requiredPurchaseUnits()._2()).map { it as Integer }.orElse(0),
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

    private static List<Facet> facets(ProductQueryRequest request, ProductQueryResponse response) {
        [
                categoriesFacet(request, response),
                brandsFacet(request, response),
                suppliersFacet(request, response)
        ]
                .findAll { it.isPresent() }
                .collect { it.get() } +
                featuresFacet(request, response)
    }

    private static Optional<Facet> suppliersFacet(ProductQueryRequest request, ProductQueryResponse response) {
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
    }

    private static List<Facet> featuresFacet(ProductQueryRequest request, ProductQueryResponse response) {
        toJava(response.aggregations().features().map { asJava(it.features().toList()) })
                .orElse([])
                .findAll { t -> !request.filtering().byFeatures().exists { it.contains(t._1()) } }
                .collect {
                    new Facet(
                            id: "feature_" + it._1(),
                            name: it._2().name().defaultEntry(),
                            slices: asJava(it._2().hits()).collect { value ->
                                new Slices(
                                        size: value._2() as Long,
                                        obj: new Slice(
                                                id: value._1().id(),
                                                name: singleValueName(value._1()),
                                                key: value._1().id()
                                        )
                                )
                            }
                    )
                }
    }

    private static String singleValueName(SingleValue value) {
        Optional.of(value)
                .filter { it instanceof IntValue }
                .map { it as IntValue }
                .map {
                    Optional.of(
                            it.value().toString() +
                                    toJava(it.measureUnit())
                                            .map { " " + it.name() }
                                            .orElse("")
                    )
                }
                .orElseGet {
                    Optional.of(value)
                            .filter { it instanceof DoubleValue }
                            .map { it as DoubleValue }
                            .map {
                                Optional.of(
                                        it.value().toString() +
                                                toJava(it.measureUnit())
                                                        .map { " " + it.name() }
                                                        .orElse("")
                                )
                            }
                            .orElseGet {
                                Optional.of(value)
                                        .filter { it instanceof StringValue }
                                        .map { it as StringValue }
                                        .map { it.value().defaultEntry() }
                            }
                }
                .orElse("")
    }

    private static Optional<Facet> brandsFacet(ProductQueryRequest request, ProductQueryResponse response) {
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
                }
    }

    private static Optional<Facet> categoriesFacet(ProductQueryRequest request, ProductQueryResponse response) {
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
                }
    }

    private static List<Filter> filters(ProductQueryRequest request, response) {
        categoryFilter(request, response) +
                brandFilter(request, response) +
                supplierFilter(request, response) +
                featuresFilter(request, response)
    }

    private static List<Filter> categoryFilter(ProductQueryRequest request, ProductQueryResponse response) {
        def breadCrumb = breadCrumb(request, response)
        if (breadCrumb.isEmpty()) []
        else ofNullable(breadCrumb.last())
                .map {
                    [
                            new Filter(
                                    key: "category",
                                    values: [
                                            new FilterItem(
                                                    id: it.id,
                                                    name: it.name
                                            )
                                    ]
                            )
                    ]
                }
                .orElse([])
    }

    private static List<Filter> brandFilter(ProductQueryRequest request, ProductQueryResponse response) {
        toJava(request.filtering().byBrand())
                .map { asJava(it.values()) }
                .flatMap { filtered ->
                    ofNullable(
                            toJava(response.aggregations().brands())
                                    .map { asJava(it.hits()).collect { it._1() } }
                                    .orElse([])
                                    .findAll { filtered.contains(it.id()) }
                    ).map {
                        it.collect {
                            new Filter(
                                    key: "brand",
                                    values: [
                                            new FilterItem(
                                                    id: it.id() as Integer,
                                                    name: it.name().defaultEntry()
                                            )
                                    ]
                            )
                        }
                    }
                }
                .orElse([])
    }

    private static List<Filter> supplierFilter(ProductQueryRequest request, ProductQueryResponse response) {
        toJava(request.filtering().bySupplier())
                .map { asJava(it.values()) }
                .flatMap { filtered ->
                    ofNullable(
                            toJava(response.aggregations().suppliers())
                                    .map { asJava(it.hits()).collect { it._1() } }
                                    .orElse([])
                                    .findAll { filtered.contains(it.id()) }
                    ).map {
                        it.collect {
                            new Filter(
                                    key: "supplier",
                                    values: [
                                            new FilterItem(
                                                    id: it.id() as Integer,
                                                    name: it.name()
                                            )
                                    ]
                            )
                        }
                    }
                }
                .orElse([])
    }

    private static List<Filter> featuresFilter(ProductQueryRequest request, ProductQueryResponse response) {
        toJava(request.filtering().byFeatures())
                .map { asJava(it.values().toList()) }
                .map { filtered ->
                    filtered.collect { t ->
                        toJava(response.aggregations().features())
                                .flatMap {
                                    toJava(it.features().get(t._1()))
                                            .map {
                                                asJava(it.hits())
                                                        .findAll { value ->
                                                            asJava(t._2()).any { it == value._1().id() }
                                                        }
                                                        .collect {
                                                            new Filter(
                                                                    key: "feature_" + t._1(),
                                                                    values: [
                                                                            new FilterItem(
                                                                                    id: it._1().id() as Integer,
                                                                                    name: singleValueName(it._1())
                                                                            )
                                                                    ]
                                                            )
                                                        }
                                            }
                                }
                                .orElse([])
                    }.flatten() as List<Filter>
                }
                .orElse([])
    }

    private static Sort sort(ProductQueryRequest request) {
        def defaultAsc = new Tuple("DEFAULT", "ASC")
        def maybeSorting = toJava(request.sorting())
        def maybeByRelevance = maybeSorting
                .filter { it instanceof ByRelevance$ }
                .map { Optional.of(defaultAsc) }
        def maybeByLasAvailabilityUpdate = maybeSorting
                .filter { it instanceof ByLastAvailabilityUpdate$ }
                .map { Optional.of(new Tuple("RECENT", "ASC")) }
        def maybeByUnitPrice = maybeSorting
                .filter { it instanceof ByUnitPrice }
                .map { it as ByUnitPrice }
                .map { Optional.of(new Tuple("PRICE", it.asc() ? "ASC" : "DESC")) }
        def maybeAlphabetically = maybeSorting
                .filter { it instanceof Alphabetically }
                .map { it as Alphabetically }
                .map { new Tuple("TITLE", it.asc() ? "ASC" : "DESC") }
        def sort =
                maybeByRelevance.orElseGet {
                    maybeByLasAvailabilityUpdate.orElseGet {
                        maybeByUnitPrice.orElseGet {
                            maybeAlphabetically
                        }
                    }
                }.orElse(defaultAsc)
        new Sort(field: sort.first(), direction: sort.last())
    }

    private static List<BreadCrumb> breadCrumb(ProductQueryRequest request, ProductQueryResponse response) {
        toJava(response.hits().headOption())
                .map {
                    asJava(it.categorization())
                            .takeWhile { category ->
                                toJava(request.filtering().byCategory())
                                        .map { it.contains(category.id()) }
                                        .orElse(false)
                            }
                            .collect {
                                new BreadCrumb(
                                        id: it.id() as Integer,
                                        name: it.name().defaultEntry()
                                )
                            }
                }
                .orElse([])
    }

}
