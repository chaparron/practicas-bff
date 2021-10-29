package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.configuration.EntityNotFoundException
import bff.model.*
import scala.Option
import wabi2b.grocery.listing.sdk.*

import static bff.model.SortInput.DESC
import static java.util.Optional.ofNullable
import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.CollectionConverters.asScala
import static scala.jdk.javaapi.OptionConverters.toJava
import static scala.jdk.javaapi.OptionConverters.toScala
import static wabi2b.grocery.listing.sdk.BrandQueryRequest.availableBrandsIn
import static wabi2b.grocery.listing.sdk.ProductQueryRequest.availableProductsIn
import static wabi2b.grocery.listing.sdk.SuggestionQueryRequestBuilder.availableSuggestionsIn
import static wabi2b.grocery.listing.sdk.SupplierQueryRequest.availableSuppliersIn

class GroceryListing {

    private Sdk sdk
    private CustomerBridge customerBridge

    SearchResult search(SearchInput input) {
        def page = new Page(input)
        def request =
                [new FilteringBuilder(input), new SortingBuilder(input)]
                        .inject(
                                availableProductsForCustomer(input.accessToken)
                                        .sized(page.size)
                                        .aggregatedByBrands(10)
                                        .aggregatedByCategories(1, true)
                                        .aggregatedBySuppliers(10)
                                        .aggregatedByFeatures()
                                        .fetchingOptions(50),
                                { request, builder -> builder.apply(request) }
                        )
        def response = sdk.query(request.offset(page.offset))
        return new SearchResultMapper(input, request).map(response)
    }

    ScrollableSearchResult scroll(SearchScrollInput input) {
        def response = sdk.query(new ProductScrollRequest(input.scroll))
        return new ScrollableSearchResultMapper().map(response)
    }

    PreviewSearchResult search(PreviewSearchInput input) {
        def page = new Page(input)
        def request =
                [new FilteringBuilder(input), new SortingBuilder(input)]
                        .inject(
                                availableProductsIn(
                                        new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                                        Option.apply(input.countryId)
                                )
                                        .sized(page.size)
                                        .aggregatedByBrands(10)
                                        .aggregatedByCategories(1, true)
                                        .aggregatedByFeatures()
                                        .fetchingOptions(50),
                                { request, builder -> builder.apply(request) }
                        )
        def response = sdk.query(request.offset(page.offset))
        return new PreviewSearchResultMapper(input, request).map(response)
    }

    Suggestions suggest(SuggestInput input) {
        def customer = customerBridge.myProfile(input.accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()
        def request =
                ([
                        input.maybeProducts.map { { b -> b.fetchingProducts(it, ByRelevance$.MODULE$) } },
                        input.maybeCategories.map { { b -> b.fetchingCategories(it, ByRelevance$.MODULE$) } },
                        input.maybeBrands.map { { b -> b.fetchingBrands(it, ByRelevance$.MODULE$) } },
                        input.maybeSuppliers.map { { b -> b.fetchingSuppliers(it, ByRelevance$.MODULE$) } },
                        ofNullable(input.favourites).filter { it }.map { { b -> b.favourites() } }
                ]
                        .collect { it.orElse({ r -> r }) }
                        .inject(
                                availableSuggestionsIn(
                                        new Coordinate(deliveryAddress.lat.toDouble(), deliveryAddress.lon.toDouble()),
                                        Option.apply(customer.country_id)
                                ).forTerm(
                                        input.keyword,
                                        toScala(ofNullable(input.languageTag).map { it.language })
                                ),
                                { request, builder -> builder(request) }
                        ) as SuggestionQueryRequest
                )
                        .forCustomer(customer.id.toString(), customer.customerType.code)
        def response = sdk.query(request)
        new Suggestions(
                products: asJava(response.products()).collect {
                    new SuggestedProduct(
                            id: it.id().toInteger(),
                            name: it.name().defaultEntry()
                    )
                },
                brands: asJava(response.brands()).collect {
                    new SuggestedBrand(
                            id: it.id().toInteger(),
                            name: it.name().defaultEntry(),
                            logo: toJava(it.logo()).orElse(null)
                    )
                },
                categories: asJava(response.categories()).collect {
                    new SuggestedCategory(
                            id: it.id().toInteger(),
                            name: it.name().defaultEntry()
                    )
                },
                suppliers: asJava(response.suppliers()).collect {
                    new SuggestedSupplier(
                            id: it.id().toInteger(),
                            name: it.name(),
                            avatar: toJava(it.avatar()).orElse(null)
                    )
                }
        )
    }

    Cart refreshCart(String accessToken, List<Integer> products) {
        def request =
                availableProductsForCustomer(accessToken)
                        .sized(products.size())
                        .filteredByProduct(
                                products.head().toString(),
                                asScala(products.tail().collect { it.toString() }).toSeq()
                        )
                        .fetchingOptions(50)
                        .fetchingDeliveryZones(1)
        def response = sdk.query(request)
        return new CartMapper(accessToken, request).map(response)
    }

    Product getProductById(String accessToken, Integer product) {
        def request =
                availableProductsForCustomer(accessToken)
                        .sized(1)
                        .filteredByProduct(product.toString(), asScala([] as List<String>).toSeq())
                        .fetchingOptions(50)
                        .fetchingDeliveryZones(1)
        def response = sdk.query(request)
        return new ProductMapper(accessToken, request)
                .map(response)
                .orElseThrow { new EntityNotFoundException() }
    }

    GetHomeBrandsResult getHomeBrands(String accessToken, String country) {
        def customer = customerBridge.myProfile(accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()

        def request =
                ofNullable(accessToken)
                        .map {
                            availableBrandsIn(
                                    new Coordinate(deliveryAddress.lat.toDouble(), deliveryAddress.lon.toDouble()),
                                    Option.apply(customer.country_id)
                            ).forCustomer(customer.id.toString(), customer.customerType.code)
                        }
                        .orElse(availableBrandsIn(country))
                        .sized(20)
        def response = sdk.query(request)
        return new HomeBrandsResultMapper().map(response)
    }

    GetHomeBrandsResult getHomeBrands(CoordinatesInput input) {
        def request =
                availableBrandsIn(
                        new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                        Option.empty()
                ).sized(20)
        def response = sdk.query(request)
        return new HomeBrandsResultMapper().map(response)
    }

    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput input) {
        def request =
                availableSuppliersIn(
                        new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                        Option.empty()
                ).sized(20)
        def response = sdk.query(request)
        return new PreviewHomeSupplierResponseMapper().map(response)
    }

    private def availableProductsForCustomer(String accessToken) {
        def customer = customerBridge.myProfile(accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()

        return availableProductsIn(
                new Coordinate(deliveryAddress.lat.toDouble(), deliveryAddress.lon.toDouble()),
                Option.apply(customer.country_id)
        ).forCustomer(customer.id.toString(), customer.customerType.code)
    }

}

class Page {

    Integer number
    Integer size
    Integer offset

    Page(SearchInput input) {
        this(input.page, input.size)
    }

    Page(PreviewSearchInput input) {
        this(input.page, input.size)
    }

    private Page(Integer number, Integer size) {
        this.number = ofNullable(number).orElse(1)
        this.size = ofNullable(size).orElse(10)
        this.offset = (this.number - 1) * this.size
    }

}

interface RequestBuilder {

    ProductQueryRequest apply(ProductQueryRequest request)

}

class FilteringBuilder implements RequestBuilder {

    Optional<String> maybeKeyword
    Optional<Integer> maybeCategory
    Optional<Integer> maybeBrand
    Optional<Integer> maybeSupplier
    Optional<String> maybePromotion
    List<FeatureInput> features
    Optional<Boolean> maybeFavourites

    FilteringBuilder(SearchInput input) {
        this(input.keyword, input.category, input.brand, input.supplier, input.tag, input.features, input.favourites)
    }

    FilteringBuilder(PreviewSearchInput input) {
        this(input.keyword, input.category, input.brand, null, input.tag, input.features, null)
    }

    private FilteringBuilder(String keyword,
                             Integer category,
                             Integer brand,
                             Integer supplier,
                             String promotion,
                             List<FeatureInput> features,
                             Boolean favourites) {
        this.maybeKeyword = ofNullable(keyword).filter { !it.isEmpty() }
        this.maybeCategory = ofNullable(category)
        this.maybeBrand = ofNullable(brand)
        this.maybeSupplier = ofNullable(supplier)
        this.maybePromotion = ofNullable(promotion).filter { !it.isEmpty() }
        this.features = features
        this.maybeFavourites = ofNullable(favourites)
    }

    ProductQueryRequest apply(ProductQueryRequest request) {
        (
                [
                        termFiltering(),
                        brandFiltering(),
                        categoryFiltering(),
                        supplierFiltering(),
                        promotionFiltering(),
                        favouritesFiltering()
                ] + featuresFiltering()
        )
                .inject(request, { acc, filter -> filter(acc) })
    }

    private def identity = { ProductQueryRequest r -> r }

    private Closure<ProductQueryRequest> termFiltering() {
        maybeKeyword
                .map { term ->
                    { ProductQueryRequest r -> r.filteredByTerm(term, Option.empty(), FullText$.MODULE$) }
                }
                .orElse(identity)
    }

    private Closure<ProductQueryRequest> categoryFiltering() {
        maybeCategory
                .map { category ->
                    { ProductQueryRequest r ->
                        r.filteredByCategory(category.toString(), asScala([] as List<String>).toSeq())
                    }
                }
                .orElse(identity)
    }

    private Closure<ProductQueryRequest> brandFiltering() {
        maybeBrand
                .map { brand ->
                    { ProductQueryRequest r ->
                        r.filteredByBrand(brand.toString(), asScala([] as List<String>).toSeq())
                    }
                }
                .orElse(identity)
    }

    private Closure<ProductQueryRequest> supplierFiltering() {
        maybeSupplier
                .map { supplier ->
                    { ProductQueryRequest r ->
                        r.filteredBySupplier(supplier.toString(), asScala([] as List<String>).toSeq())
                    }
                }
                .orElse(identity)
    }

    private Closure<ProductQueryRequest> promotionFiltering() {
        maybePromotion
                .map { promotion -> { ProductQueryRequest r -> r.filteredByPromotion(promotion) } }
                .orElse(identity)
    }

    private Closure<ProductQueryRequest> favouritesFiltering() {
        maybeFavourites
                .filter { it }
                .map { { ProductQueryRequest r -> r.favourites() } }
                .orElse(identity)
    }

    private List<Closure<ProductQueryRequest>> featuresFiltering() {
        features.collect { feature ->
            { ProductQueryRequest r ->
                r.filteredByFeature(feature.id, feature.value, asScala([] as List<String>).toSeq())
            }
        }
    }

}

class SortingBuilder implements RequestBuilder {

    Optional<String> maybeSort
    Optional<SortInput> maybeDirection
    Optional<String> maybeKeyword

    SortingBuilder(SearchInput input) {
        this(input.sort, input.sortDirection, input.keyword)
    }

    SortingBuilder(PreviewSearchInput input) {
        this(input.sort, input.sortDirection, input.keyword)
    }

    private SortingBuilder(String sort,
                           SortInput direction,
                           String keyword) {
        this.maybeSort = ofNullable(sort).filter { !it.isEmpty() }
        this.maybeDirection = ofNullable(direction)
        this.maybeKeyword = ofNullable(keyword).filter { !it.isEmpty() }
    }

    ProductQueryRequest apply(ProductQueryRequest request) {
        maybeSort
                .map { sort ->
                    switch (sort) {
                        case "DEFAULT":
                            maybeKeyword
                                    .map { sortedByRelevance(request) }
                                    .orElse(sortedAlphabetically(request))
                            break
                        case "TITLE":
                            sortedAlphabetically(request)
                            break
                        case "RECENT":
                            sortedByLastAvailabilityUpdate(request)
                            break
                        case "PRICE":
                            sortedByUnitPrice(request)
                            break
                        default:
                            request
                            break
                    }
                }
                .map { Optional.of(it) }
                .orElseGet { maybeKeyword.map { sortedByRelevance(request) } }
                .orElse(request)
    }

    private static ProductQueryRequest sortedByRelevance(ProductQueryRequest request) {
        request.sortedByRelevance()
    }

    private static ProductQueryRequest sortedByLastAvailabilityUpdate(ProductQueryRequest request) {
        request.sortedByLastAvailabilityUpdate()
    }

    private ProductQueryRequest sortedByUnitPrice(ProductQueryRequest request) {
        request.sortedByUnitPrice(
                maybeDirection.map { direction ->
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
    }

    private ProductQueryRequest sortedAlphabetically(ProductQueryRequest request) {
        request.sortedAlphabetically(
                maybeDirection.map { direction ->
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
    }

}

abstract class ProductResponseMapper {

    ProductQueryRequest request

    ProductResponseMapper(ProductQueryRequest request) {
        this.request = request
    }

    protected static List<ProductSearch> products(ProductQueryResponse response) {
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
                    ean: displays.sort { it.units }.first().ean,
                    description: toJava(it.description()).map { it.defaultEntry() }.orElse(null),
                    images: asJava(it.images()).collect { new Image(id: it) },
                    displays: displays,
                    prices: prices,
                    minUnitsPrice: prices.min { Price a, Price b ->
                        (a.minUnits == b.minUnits) ? a.unitValue <=> b.unitValue : a.minUnits <=> b.minUnits
                    },
                    highlightedPrice: prices.min { it.unitValue },
                    title: it.name().defaultEntry(),
                    country_id: it.manufacturer().country(),
                    favorite: toJava(it.favourite()).orElse(null)
            )
        }
    }

    protected List<Facet> facets(ProductQueryResponse response) {
        [
                categoriesFacet(response),
                brandsFacet(response),
                suppliersFacet(response)
        ]
                .findAll { it.isPresent() }
                .collect { it.get() } +
                featuresFacet(response)
    }

    protected List<Filter> filters(ProductQueryResponse response) {
        termFilter() +
                categoryFilter(response) +
                brandFilter(response) +
                supplierFilter(response) +
                featuresFilter(response)
    }

    protected Sort sort() {
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

    protected List<BreadCrumb> breadCrumb(ProductQueryResponse response) {
        toJava(response.hits().headOption())
                .map {
                    def categorization = asJava(it.categorization())
                    def index = categorization.findLastIndexOf { category ->
                        toJava(request.filtering().byCategory())
                                .map { asJava(it.values()).first() }
                                .map { it == category.id() }
                                .orElse(false)
                    }
                    categorization
                            .take(index + 1)
                            .collect {
                                new BreadCrumb(
                                        id: it.id() as Integer,
                                        name: it.name().defaultEntry()
                                )
                            }
                            .reverse()
                }
                .orElse([])
    }

    protected Optional<Facet> suppliersFacet(ProductQueryResponse response) {
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

    protected List<Facet> featuresFacet(ProductQueryResponse response) {
        toJava(response.aggregations().features().map { asJava(it.features().toList()) })
                .orElse([])
                .findAll { t -> !request.filtering().byFeatures().exists { it.contains(t._1()) } }
                .collect {
                    new Facet(
                            id: "feature_" + it._1(),
                            name: it._2().name().defaultEntry(),
                            slices:
                                    asJava(it._2().hits())
                                            .collect { slice(it) }
                                            .findAll { it.isPresent() }
                                            .collect { it.get() }
                    )
                }
                .sort { it.name }
    }

    protected Optional<Facet> brandsFacet(ProductQueryResponse response) {
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

    protected static Optional<Facet> categoriesFacet(ProductQueryResponse response) {
        toJava(response.aggregations().categories())
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

    protected List<Filter> termFilter() {
        toJava(request.filtering().byTerm())
                .map {
                    [
                            new Filter(
                                    key: "keyword",
                                    value: it.text()
                            )
                    ]
                }
                .orElse([])
    }

    protected List<Filter> categoryFilter(ProductQueryResponse response) {
        def breadCrumb = breadCrumb(response)
        if (breadCrumb.isEmpty()) []
        else ofNullable(breadCrumb.first())
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

    protected List<Filter> brandFilter(ProductQueryResponse response) {
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

    protected List<Filter> supplierFilter(ProductQueryResponse response) {
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

    protected List<Filter> featuresFilter(ProductQueryResponse response) {
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
                                                        .collect { slice(it) }
                                                        .findAll { it.isPresent() }
                                                        .collect { it.get() }
                                                        .collect {
                                                            new Filter(
                                                                    key: "feature_" + t._1(),
                                                                    values: [
                                                                            new FilterItem(
                                                                                    id: it.obj.id as Integer,
                                                                                    name: it.obj.name
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

    protected static Price price(AvailableOption option) {
        new Price(
                id: option.id() as Integer,
                supplier: supplier(option),
                value: option.price().toBigDecimal(),
                unitValue: option.price() / option.display().units(),
                minUnits: option.requiredPurchaseUnits()._1() as Integer,
                maxUnits: toJava(option.requiredPurchaseUnits()._2()).map { it as Integer }.orElse(0),
                display: display(option),
                configuration: new SupplierProductConfiguration(
                        disableMinAmountCount: option.minPurchaseAmountCountDisabled()
                )
        )
    }

    protected static Supplier supplier(AvailableOption option) {
        new Supplier(
                id: option.supplier().id().toInteger(),
                name: option.supplier().name(),
                legalName: null,
                avatar: toJava(option.supplier().avatar()).orElse(null),
                deliveryZones: toJava(option.deliveryZones())
                        .map { asJava(it.toList()) }
                        .orElse([])
                        .collect {
                            new DeliveryZone(
                                    id: it.id().toInteger(),
                                    minAmount: it.requiredPurchaseAmount()._1().toBigDecimal(),
                                    maxAmount: toJava(it.requiredPurchaseAmount()._2()
                                            .map { it.toBigDecimal() })
                                            .orElse(null),
                                    deliveryCost: toJava(it.cost()
                                            .map { it.toBigDecimal() })
                                            .orElse(null)
                            )
                        },
                // Using default value for old clients compatibility.
                averageDeliveryDay: null
        )
    }

    protected static Display display(AvailableOption option) {
        new Display(
                id: option.display().id().toInteger(),
                ean: option.display().ean(),
                units: option.display().units()
        )
    }

    protected static slice(scala.Tuple2<SingleValue, Object> value) {
        def name = {
            if (value._1() instanceof IntValue) {
                def intValue = (value._1() as IntValue)
                intValue.value().toString() +
                        toJava(intValue.measureUnit()).map { " " + it.name() }.orElse("")
            } else if (value._1() instanceof DoubleValue) {
                def doubleValue = (value._1() as DoubleValue)
                doubleValue.value().toString() +
                        toJava(doubleValue.measureUnit()).map { " " + it.name() }.orElse("")
            } else (value._1() as StringValue).value().defaultEntry()
        }
        Optional
                .of(value)
                .filter { t ->
                    [IntValue, DoubleValue, StringValue].any { t._1() in it }
                }
                .map { t ->
                    new Slices(
                            size: t._2() as Long,
                            obj: new Slice(
                                    id: t._1().id(),
                                    key: t._1().id(),
                                    name: name()
                            )
                    )
                }
    }

}

class SearchResultMapper extends ProductResponseMapper {

    SearchInput input

    SearchResultMapper(SearchInput input, ProductQueryRequest request) {
        super(request)
        this.input = input
    }

    SearchResult map(ProductQueryResponse response) {
        SearchResult searchResult = new SearchResult(
                header: new Header(
                        total: response.total().toInteger(),
                        pageSize: request.size(),
                        currentPage: new Page(input).number,
                        scroll: toJava(response.scroll()).orElse(null)
                ),
                sort: sort(),
                breadcrumb: breadCrumb(response),
                filters: filters(response),
                facets: facets(response),
                products: products(response)
        )
        searchResult.products.each {
            it.prices.each {
                it.supplier?.accessToken = input.accessToken
            }
            it.highlightedPrice.supplier.accessToken = input.accessToken
        }
        searchResult
    }


}

class PreviewSearchResultMapper extends ProductResponseMapper {

    PreviewSearchInput input

    PreviewSearchResultMapper(PreviewSearchInput input, ProductQueryRequest request) {
        super(request)
        this.input = input
    }

    PreviewSearchResult map(ProductQueryResponse response) {
        new PreviewSearchResult(
                header: new Header(
                        total: response.total().toInteger(),
                        pageSize: request.size(),
                        currentPage: new Page(input).number
                ),
                sort: sort(),
                breadcrumb: breadCrumb(response),
                filters: filters(response),
                facets: facets(response),
                products: products(response).collect {
                    def suppliers =
                            it.prices.collect { new PreviewSupplier(id: it.supplier.id, name: "") }
                                    .toSet().toList()
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
                            title: it.title,
                            country_id: it.country_id,
                            totalNumberOfSuppliers: suppliers.size(),
                            suppliers: suppliers
                    )
                }
        )
    }

}

class CartMapper extends ProductResponseMapper {

    private String accessToken

    CartMapper(String accessToken, ProductQueryRequest request) {
        super(request)
        this.accessToken = accessToken
    }

    Cart map(ProductQueryResponse response) {
        def products = products(response)
        new Cart(
                availableProducts: products.collect {
                    new ProductCart(
                            product: new Product(
                                    id: it.id,
                                    name: it.name,
                                    brand: it.brand,
                                    enabled: it.enabled,
                                    ean: it.ean,
                                    description: it.description,
                                    title: it.title,
                                    prices: it.prices,
                                    displays: it.displays,
                                    priceFrom: it.priceFrom,
                                    minUnitsPrice: it.minUnitsPrice,
                                    highlightedPrice: it.highlightedPrice,
                                    country_id: it.country_id,
                                    favorite: it.favorite,
                                    accessToken: accessToken
                            ),
                            supplierPrices: it.prices.collect {
                                new SupplierPrice(
                                        id: it.supplier.id,
                                        name: it.supplier.name,
                                        price: it.value,
                                        display: it.display,
                                        minUnits: it.minUnits,
                                        maxUnits: it.maxUnits,
                                        avatar: it.supplier.avatar,
                                        deliveryZone: it.supplier.deliveryZones?.head(),
                                        configuration: it.configuration
                                )
                            }.toSet().toList()
                    )
                },
                suppliers: products.collect {
                    it.prices.collect {
                        it.supplier.accessToken = accessToken
                        it.supplier
                    }
                }
                        .flatten().toSet().toList() as List<Supplier>
        )
    }

}

class ProductMapper extends ProductResponseMapper {

    private String accessToken

    ProductMapper(String accessToken, ProductQueryRequest request) {
        super(request)
        this.accessToken = accessToken
    }

    Optional<Product> map(ProductQueryResponse response) {
        def products = products(response)
        products.isEmpty() ? Optional.empty() : Optional.of(products.head()).map {
            addAccessToken(new Product(
                    accessToken: accessToken,
                    id: it.id,
                    name: it.name,
                    enabled: it.enabled,
                    ean: it.ean,
                    description: it.description,
                    title: it.title,
                    prices: it.prices,
                    displays: it.displays,
                    priceFrom: it.priceFrom,
                    minUnitsPrice: it.minUnitsPrice,
                    highlightedPrice: it.highlightedPrice,
                    brand: it.brand,
                    country_id: it.country_id,
                    favorite: it.favorite
            ), accessToken)
        }
    }

    static Product addAccessToken(Product product, String accessToken) {
        product.accessToken = accessToken
        product.prices.each {
            it.accessToken = accessToken
            it.supplier?.accessToken = accessToken
        }
        product.priceFrom?.accessToken = accessToken
        product.minUnitsPrice?.accessToken = accessToken
        product.highlightedPrice?.accessToken = accessToken

        return product
    }

}

class HomeBrandsResultMapper {

    static GetHomeBrandsResult map(BrandQueryResponse response) {
        new GetHomeBrandsResult(
                brands: asJava(response.hits()).collect {
                    new Brand(
                            id: it.id().toInteger(),
                            name: it.name(),
                            logo: toJava(it.logo()).orElse(null)
                    )
                }
        )
    }

}

class PreviewHomeSupplierResponseMapper {

    static PreviewHomeSupplierResponse map(SupplierQueryResponse response) {
        new PreviewHomeSupplierResponse(
                suppliers: asJava(response.hits()).collect {
                    new PreviewSupplier(
                            id: it.id().toInteger(),
                            name: it.name(),
                            avatar: toJava(it.avatar()).orElse(null)
                    )
                }
        )
    }

}

class ScrollableSearchResultMapper extends ProductResponseMapper {

    ScrollableSearchResultMapper() {
        super(null)
    }

    static ScrollableSearchResult map(ProductQueryResponse response) {
        new ScrollableSearchResult(
                scroll: toJava(response.scroll()).orElse(null),
                products: products(response)
        )
    }
}