package bff.bridge.sdk

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.sdk.GroceryListing.SuggestionQueryRequestBuilder
import bff.configuration.EntityNotFoundException
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import scala.Option
import sun.util.locale.LanguageTag
import wabi2b.grocery.listing.sdk.*

import static bff.model.SortInput.DESC
import static java.util.Locale.forLanguageTag
import static java.util.Optional.*
import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.CollectionConverters.asScala
import static scala.jdk.javaapi.OptionConverters.toJava
import static scala.jdk.javaapi.OptionConverters.toScala
import static wabi2b.grocery.listing.sdk.BrandQueryRequest.availableBrandsIn
import static wabi2b.grocery.listing.sdk.ProductQueryRequest.availableProductsIn
import static wabi2b.grocery.listing.sdk.ProductQueryRequest.similarProductsTo
import static wabi2b.grocery.listing.sdk.PromotionQueryRequest.availablePromotionsIn
import static wabi2b.grocery.listing.sdk.SuggestionQueryRequestBuilder.availableSuggestionsIn
import static wabi2b.grocery.listing.sdk.SupplierQueryRequest.availableSuppliersIn

@Slf4j
class GroceryListing {

    private Sdk sdk
    private CountryBridge countryBridge
    private CustomerBridge customerBridge
    private MessageSource messageSource

    Optional<Country> find(CoordinatesInput input) {
        toJava(sdk.find(new Coordinate(input.lat.toDouble(), input.lng.toDouble())))
                .flatMap { id ->
                    try {
                        ofNullable(countryBridge.getCountry(id))
                    } catch (CountryNotFoundException ignored) {
                        empty() as Optional<Country>
                    }
                }
    }

    SearchResult search(SearchInput input) {
        def page = new Page(input)
        def request =
                [new ProductQueryRequestFilteringBuilder(input), new ProductQueryRequestSortingBuilder(input)]
                        .inject(
                                ofNullable(input.similarTo)
                                        .map {
                                            def (customer, deliveryAddress) =
                                            getCustomerAndDeliveryAddress(input.accessToken)
                                            similarProductsTo(it.toString())
                                                    .availableIn(*deliveryAddress)
                                                    .forCustomer(*customer)
                                                    .sized(page.size)
                                                    .fetchingOptions(50, Option.empty()) as ProductQueryRequest
                                        }
                                        .orElse(
                                                availableProductsForCustomer(input.accessToken)
                                                        .sized(page.size)
                                                        .aggregatedByBrands(10)
                                                        .aggregatedByCategories(1, true)
                                                        .aggregatedBySuppliers(10)
                                                        .aggregatedByFeatures()
                                                        .aggregatedByDiscounts(5)
                                                        .fetchingOptions(50, Option.empty())
                                        ),
                                { req, builder -> builder.apply(req) }
                        )
        try {
            def response = sdk.query(request.offset(page.offset))
            return new SearchResultMapper(input, request).map(response)
        } catch (Exception ex) {
            log.error("Error searching products for request {}", request, ex)
            throw ex
        }
    }

    PreviewSearchResult search(PreviewSearchInput input) {
        def page = new Page(input)
        def request =
                [new ProductQueryRequestFilteringBuilder(input), new ProductQueryRequestSortingBuilder(input)]
                        .inject(
                                ofNullable(input.similarTo)
                                        .map {
                                            similarProductsTo(it.toString())
                                                    .availableIn(
                                                            new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                                                            Option.apply(input.countryId)
                                                    )
                                                    .sized(page.size)
                                                    .fetchingOptions(50, Option.empty()) as ProductQueryRequest
                                        }
                                        .orElse(
                                                availableProductsIn(
                                                        new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                                                        Option.apply(input.countryId)
                                                )
                                                        .sized(page.size)
                                                        .aggregatedByBrands(10)
                                                        .aggregatedByCategories(1, true)
                                                        .aggregatedByFeatures()
                                                        .aggregatedByDiscounts(5)
                                                        .fetchingOptions(50, Option.empty())
                                        ),
                                { request, builder -> builder.apply(request) }
                        )
        try {
            def response = sdk.query(request.offset(page.offset))
            return new PreviewSearchResultMapper(input, request).map(response)
        } catch (Exception ex) {
            log.error("Error searching products for request {}", request, ex)
            throw ex
        }
    }

    ScrollableSearchResult scroll(SearchScrollInput input) {
        def request = new ProductScrollRequest(input.scroll)
        try {
            def response = sdk.query(request)
            return new ScrollableSearchResultMapper(input).map(response)
        } catch (Exception ex) {
            log.error("Error scrolling products for request {}", request, ex)
            throw ex
        }
    }

    ScrollableSearchResult scroll(PreviewSearchScrollInput input) {
        try {
            def response = sdk.query(new ProductScrollRequest(input.scroll))
            return new ScrollableSearchResultMapper(input).map(response)
        } catch (Exception ex) {
            log.error("Error scrolling products for input {}", input, ex)
            throw ex
        }
    }

    Suggestions suggest(SuggestInput input) {
        def (customer, deliveryAddress) = getCustomerAndDeliveryAddress(input.accessToken)
        def request =
                new SuggestionQueryRequestBuilder(input)
                        .apply(
                                availableSuggestionsIn(*deliveryAddress)
                                        .forTerm(
                                                input.keyword,
                                                toScala(ofNullable(input.languageTag).map { it.language })
                                        )
                        )
                        .forCustomer(*customer)
        try {
            def response = sdk.query(request)
            return new SuggestionsMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching suggestions for request {}", request, ex)
            throw ex
        }
    }

    Suggestions suggest(PreviewSuggestInput input) {
        def request =
                new SuggestionQueryRequestBuilder(input).apply(
                        availableSuggestionsIn(
                                new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                                toScala(ofNullable(input.country))
                        ).forTerm(
                                input.keyword,
                                toScala(ofNullable(input.languageTag).map { it.language })
                        )
                )
        try {
            def response = sdk.query(request)
            return new SuggestionsMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching suggestions for request {}", request, ex)
            throw ex
        }
    }

    Cart refreshCart(RefreshCartInput input) {
        def request = availableProductsForCustomer(input.accessToken)
                .sized(input.products.size())
                .filteredByProduct(
                        input.products.head().toString(),
                        asScala(input.products.tail().collect { it.toString() }).toSeq()
                )
                .fetchingOptions(50, Option.apply(new FetchDeliveryZones(1)))
        try {
            def response = sdk.query(request)
            return new RefreshCartMapper(input, request).map(response)
        } catch (Exception ex) {
            log.error("Error refreshing cart for request {}", request, ex)
            throw ex
        }
    }

    SyncCartResult syncCart(SyncCartInput input) {
        def products = input.items.collect { it.productId }.toSet()
        def request = availableProductsForCustomer(input.accessToken)
                .sized(products.size())
                .filteredByProduct(
                        products.head().toString(),
                        asScala(products.tail().collect { it.toString() }).toSeq()
                )
                .fetchingOptions(50, Option.apply(new FetchDeliveryZones(1)))
        try {
            def response = sdk.query(request)
            return new SyncCartMapper(input, request).map(response)
        } catch (Exception ex) {
            log.error("Error synchronizing cart for request {}", request, ex)
            throw ex
        }
    }

    Product getProductById(String accessToken, Integer product) {
        def request =
                availableProductsForCustomer(accessToken)
                        .sized(1)
                        .filteredByProduct(product.toString(), asScala([] as List<String>).toSeq())
                        .fetchingOptions(50, Option.apply(new FetchDeliveryZones(1)))
        try {
            def response = sdk.query(request)
            return new ProductMapper(request, accessToken)
                    .map(response)
                    .orElseThrow { new EntityNotFoundException() }
        } catch (Exception ex) {
            log.error("Error fetching product for request {}", request, ex)
            throw ex
        }
    }

    List<ProductSearch> getProductsByIdsAndSupplierId(String accessToken, Set<Long> productIds, Long supplierId) {
        def request =
                availableProductsForCustomer(accessToken)
                        .sized(productIds.size())
                        .filteredBySupplier(supplierId.toString(), asScala([] as List<String>).toSeq())
                        .filteredByProduct(
                                productIds.head().toString(),
                                asScala(productIds.tail().collect { it.toString() }).toSeq()
                        )
                        .fetchingOptions(50, Option.apply(new FetchDeliveryZones(1)))
        try {
            def response = sdk.query(request)
            return new ProductQueryResponseMapper(request, accessToken).products(response)
        } catch (Exception ex) {
            log.error("Error fetching products for request {}", request, ex)
            throw ex
        }
    }

    GetHomeBrandsResult getHomeBrands(String accessToken, String country) {
        def request =
                ofNullable(accessToken)
                        .map {
                            def (customer, deliveryAddress) =
                            getCustomerAndDeliveryAddress(accessToken)
                            availableBrandsIn(*deliveryAddress).forCustomer(*customer)
                        }
                        .orElse(availableBrandsIn(country))
                        .sized(20)
        try {
            def response = sdk.query(request)
            return new HomeBrandsResultMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching home brands for request {}", request, ex)
            throw ex
        }
    }

    GetHomeBrandsResult getHomeBrands(CoordinatesInput input) {
        def request =
                availableBrandsIn(
                        new Coordinate(input.lat.toDouble(), input.lng.toDouble()),
                        Option.empty()
                ).sized(40)
        try {
            def response = sdk.query(request)
            return new HomeBrandsResultMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching home brands for request {}", request, ex)
            throw ex
        }
    }

    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput input) {
        def request =
                availableSuppliersIn(new Coordinate(input.lat.toDouble(), input.lng.toDouble()), Option.empty())
                        .sized(20)
        try {
            def response = sdk.query(request)
            return new PreviewHomeSupplierResponseMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching home suppliers for request {}", request, ex)
            throw ex
        }
    }

    PromotionResponse getPromotions(PromotionInput input) {
        def (customer, deliveryAddress) = getCustomerAndDeliveryAddress(input.accessToken)
        def request = availablePromotionsIn(*deliveryAddress).forCustomer(*customer)
        try {
            def response = sdk.query(request)
            return new PromotionResponseMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching promotions for request {}", request, ex)
            throw ex
        }
    }

    PromotionResponse getPromotions(CoordinatesInput input) {
        def request =
                availablePromotionsIn(new Coordinate(input.lat.toDouble(), input.lng.toDouble()), Option.empty())
                        .sized(20)
        try {
            def response = sdk.query(request)
            return new PromotionResponseMapper().map(response)
        } catch (Exception ex) {
            log.error("Error fetching promotions for request {}", request, ex)
            throw ex
        }
    }

    private def availableProductsForCustomer(String accessToken) {
        def (customer, deliveryAddress) = getCustomerAndDeliveryAddress(accessToken)
        return availableProductsIn(*deliveryAddress).forCustomer(*customer)
    }

    private def getCustomerAndDeliveryAddress(String accessToken) {
        def customer = customerBridge.myProfile(accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()
        [
                [
                        customer.id.toString(),
                        customer.customerType.code
                ],
                [
                        new Coordinate(deliveryAddress.lat.toDouble(), deliveryAddress.lon.toDouble()),
                        Option.apply(customer.country_id)
                ]
        ]
    }

    private class Page {

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
            this.number = ofNullable(number).filter { it > 0 }.orElse(1)
            this.size = ofNullable(size).orElse(10)
            this.offset = (this.number - 1) * this.size
        }

    }

    private interface ProductQueryRequestBuilder {

        ProductQueryRequest apply(ProductQueryRequest request)

    }

    private class ProductQueryRequestFilteringBuilder implements ProductQueryRequestBuilder {

        Optional<String> maybeKeyword
        Optional<Integer> maybeCategory
        Optional<Integer> maybeBrand
        Optional<Integer> maybeSupplier
        Optional<String> maybePromotion
        List<FeatureInput> features
        Optional<Boolean> maybeFavourites
        Optional<Boolean> maybePromoted
        Optional<Integer> maybeDiscount
        Optional<String> maybeCommercialPromotion
        Optional<Boolean> maybePurchased

        ProductQueryRequestFilteringBuilder(SearchInput input) {
            this(
                    input.keyword,
                    input.category,
                    input.brand,
                    input.supplier,
                    input.tag,
                    input.features,
                    input.favourites,
                    input.promoted,
                    input.discount,
                    input.commercialPromotion,
                    input.purchased
            )
        }

        ProductQueryRequestFilteringBuilder(PreviewSearchInput input) {
            this(
                    input.keyword,
                    input.category,
                    input.brand,
                    input.supplier,
                    input.tag,
                    input.features,
                    null,
                    input.promoted,
                    input.discount,
                    input.commercialPromotion,
                    null
            )
        }

        private ProductQueryRequestFilteringBuilder(String keyword,
                                                    Integer category,
                                                    Integer brand,
                                                    Integer supplier,
                                                    String promotion,
                                                    List<FeatureInput> features,
                                                    Boolean favourites,
                                                    Boolean promoted,
                                                    Integer discount,
                                                    String commercialPromotion,
                                                    Boolean purchased) {
            this.maybeKeyword = ofNullable(keyword).filter { !it.isEmpty() }
            this.maybeCategory = ofNullable(category)
            this.maybeBrand = ofNullable(brand)
            this.maybeSupplier = ofNullable(supplier)
            this.maybePromotion = ofNullable(promotion).filter { !it.isEmpty() }
            this.features = features
            this.maybeFavourites = ofNullable(favourites)
            this.maybePromoted = ofNullable(promoted)
            this.maybeDiscount = ofNullable(discount)
            this.maybeCommercialPromotion = ofNullable(commercialPromotion)
            this.maybePurchased = ofNullable(purchased)
        }

        ProductQueryRequest apply(ProductQueryRequest request) {
            (
                    [
                            termFiltering(),
                            brandFiltering(),
                            categoryFiltering(),
                            supplierFiltering(),
                            promotionFiltering(),
                            favouritesFiltering(),
                            discountFiltering(),
                            commercialPromotionFiltering(),
                            purchasedFiltering()
                    ] + featuresFiltering()
            )
                    .inject(request, { acc, filter -> filter(acc) })
        }

        private def identity = { ProductQueryRequest r -> r }

        private Closure<ProductQueryRequest> termFiltering() {
            maybeKeyword
                    .map { term ->
                        { ProductQueryRequest r ->
                            r.filteredByTerm(term, Option.empty(), FullText$.MODULE$) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> categoryFiltering() {
            maybeCategory
                    .map { category ->
                        { ProductQueryRequest r ->
                            r.filteredByCategory(
                                    category.toString(),
                                    asScala([] as List<String>).toSeq()
                            ) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> brandFiltering() {
            maybeBrand
                    .map { brand ->
                        { ProductQueryRequest r ->
                            r.filteredByBrand(
                                    brand.toString(),
                                    asScala([] as List<String>).toSeq()
                            ) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> supplierFiltering() {
            maybeSupplier
                    .map { supplier ->
                        { ProductQueryRequest r ->
                            r.filteredBySupplier(
                                    supplier.toString(),
                                    asScala([] as List<String>).toSeq()
                            ) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> promotionFiltering() {
            maybePromotion
                    .map { promotion ->
                        { ProductQueryRequest r ->
                            r.filteredByPromotion(promotion) as ProductQueryRequest
                        }
                    }
                    .orElseGet {
                        maybePromoted
                                .filter { it }
                                .map {
                                    { ProductQueryRequest r ->
                                        r.filteredByAnyPromotion() as ProductQueryRequest
                                    }
                                }
                                .orElse(identity)
                    }
        }

        private Closure<ProductQueryRequest> favouritesFiltering() {
            maybeFavourites
                    .filter { it }
                    .map {
                        { ProductQueryRequest r ->
                            r.favourites() as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private List<Closure<ProductQueryRequest>> featuresFiltering() {
            features.collect { feature ->
                { ProductQueryRequest r ->
                    r.filteredByFeature(
                            feature.id,
                            feature.value, asScala([] as List<String>).toSeq()
                    ) as ProductQueryRequest
                }
            }
        }

        private Closure<ProductQueryRequest> discountFiltering() {
            maybeDiscount
                    .map { discount ->
                        { ProductQueryRequest r ->
                            r.filteredByDiscount(discount) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> commercialPromotionFiltering() {
            maybeCommercialPromotion
                    .map { promotion ->
                        { ProductQueryRequest r ->
                            r.filteredByCommercialPromotion(promotion) as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

        private Closure<ProductQueryRequest> purchasedFiltering() {
            maybePurchased
                    .filter { it }
                    .map {
                        { ProductQueryRequest r ->
                            r.filteredByPurchased() as ProductQueryRequest
                        }
                    }
                    .orElse(identity)
        }

    }

    private class ProductQueryRequestSortingBuilder implements ProductQueryRequestBuilder {

        Optional<String> maybeSort
        Optional<SortInput> maybeDirection
        Boolean maybeKeyword
        Boolean maybeSimilarTo
        Boolean maybePromoted

        ProductQueryRequestSortingBuilder(SearchInput input) {
            this(input.sort, input.sortDirection, input.keyword, input.similarTo, input.promoted)
        }

        ProductQueryRequestSortingBuilder(PreviewSearchInput input) {
            this(input.sort, input.sortDirection, input.keyword, input.similarTo, input.promoted)
        }

        private ProductQueryRequestSortingBuilder(String sort,
                                                  SortInput direction,
                                                  String keyword,
                                                  Integer similarTo,
                                                  Boolean promoted) {
            this.maybeSort = ofNullable(sort).filter { !it.isEmpty() }
            this.maybeDirection = ofNullable(direction)
            this.maybeKeyword = ofNullable(keyword).filter { !it.isEmpty() }.present
            this.maybeSimilarTo = ofNullable(similarTo).present
            this.maybePromoted = ofNullable(promoted).filter { it }.present
        }

        ProductQueryRequest apply(ProductQueryRequest request) {
            switch (maybeSort.orElse("DEFAULT")) {
                case "DEFAULT":
                    if (maybeKeyword || maybeSimilarTo) sortedByRelevance(request)
                    else if (maybePromoted) sortedByLastAvailabilityUpdate(request)
                    else sortedByTotalSalesInLast15Days(request)
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

        private static ProductQueryRequest sortedByTotalSalesInLast15Days(ProductQueryRequest request) {
            request.sortedByTotalSalesInLast15Days()
        }

    }

    private class SuggestionQueryRequestBuilder {

        Optional<Integer> maybeProducts
        Optional<Integer> maybeBrands
        Optional<Integer> maybeCategories
        Optional<Integer> maybeSuppliers
        Optional<Boolean> maybeFavourites

        SuggestionQueryRequestBuilder(SuggestInput input) {
            this(
                    input.maybeProducts,
                    input.maybeBrands,
                    input.maybeCategories,
                    input.maybeSuppliers,
                    ofNullable(input.favourites)
            )
        }

        SuggestionQueryRequestBuilder(PreviewSuggestInput input) {
            this(
                    input.maybeProducts,
                    input.maybeBrands,
                    input.maybeCategories,
                    input.maybeSuppliers
            )
        }

        private SuggestionQueryRequestBuilder(Optional<Integer> maybeProducts,
                                              Optional<Integer> maybeBrands,
                                              Optional<Integer> maybeCategories,
                                              Optional<Integer> maybeSuppliers,
                                              Optional<Boolean> maybeFavourites = empty()) {
            this.maybeProducts = maybeProducts
            this.maybeBrands = maybeBrands
            this.maybeCategories = maybeCategories
            this.maybeSuppliers = maybeSuppliers
            this.maybeFavourites = maybeFavourites
        }

        SuggestionQueryRequest apply(wabi2b.grocery.listing.sdk.SuggestionQueryRequestBuilder request) {
            [
                    maybeProducts
                            .map { { b -> b.fetchingProducts(it, ByRelevance$.MODULE$) } }
                            .orElse(identity),
                    maybeCategories
                            .map { { b -> b.fetchingCategories(it, ByRelevance$.MODULE$) } }
                            .orElse(identity),
                    maybeBrands
                            .map { { b -> b.fetchingBrands(it, ByRelevance$.MODULE$) } }
                            .orElse(identity),
                    maybeSuppliers
                            .map { { b -> b.fetchingSuppliers(it, ByRelevance$.MODULE$) } }
                            .orElse(identity),
                    maybeFavourites
                            .filter { it }
                            .map { { b -> b.favourites() } }
                            .orElse(identity)
            ].inject(request, { acc, filter -> filter(acc) }) as SuggestionQueryRequest
        }

        private def identity = { SuggestionQueryRequest r -> r }

    }

    private class ProductQueryResponseMapper {

        ProductQueryRequest request
        Optional<String> accessToken
        CommercialPromotionLabelBuilder labelBuilder = new CommercialPromotionLabelBuilder(messageSource)

        ProductQueryResponseMapper(ProductQueryRequest request, String accessToken = null) {
            this.request = request
            this.accessToken = ofNullable(accessToken)
        }

        protected List<ProductSearch> products(ProductQueryResponse response) {
            asJava(response.hits()).collect { product(it) }
        }

        protected ProductSearch product(AvailableProduct product) {
            def country = product.manufacturer().country()
            def displays = asJava(product.options()).collect { display(it) }.toSet().toList()
            def prices = asJava(product.options()).collect { option -> price(option, country) }
            new ProductSearch(
                    product.id().toLong(),
                    product.name().defaultEntry(),
                    displays.sort { it.units }?.getAt(0)?.ean,
                    toJava(product.description()).map { it.defaultEntry() }.orElse(null),
                    country,
                    new Category(
                            id: product.categorization().last().id().toLong(),
                            parentId: toJava(product.categorization().last().parent())
                                    .map { it.toLong() }.orElse(null),
                            name: product.categorization().last().name().defaultEntry(),
                            enabled: true,
                            isLeaf: true
                    ),
                    new Brand(
                            id: product.brand().id().toLong(),
                            name: product.brand().name().defaultEntry()
                    ),
                    asJava(product.images()).collect { new Image(id: it) },
                    displays,
                    prices,
                    true,
                    toJava(product.favourite()).orElse(false) as boolean,
                    this.accessToken.orElse(null)
            )
        }

        protected Price price(AvailableOption option, String countryId) {
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
                    ),
                    commercialPromotions: toJava(option.commercialPromotion())
                            .flatMap { promo ->
                                switch (promo) {
                                    case { it instanceof AvailableDiscount }:
                                        return of(
                                                commercialPromotion(
                                                        option,
                                                        promo as AvailableDiscount,
                                                        countryId
                                                )
                                        )
                                    case { it instanceof AvailableFreeProduct }:
                                        return of(commercialPromotion(promo as AvailableFreeProduct))
                                    default: empty() as Optional<CommercialPromotionType>
                                }
                            }
                            .map { new CommercialPromotions(it) },
                    accessToken: this.accessToken.orElse(null),
                    countryId: countryId
            )
        }

        protected Discount commercialPromotion(AvailableOption option,
                                               AvailableDiscount promotion,
                                               String countryId) {
            def steps = asJava(promotion.steps()).collect {
                new DiscountStep(
                        from: it.from(),
                        to: it.to(),
                        value: option.price().toBigDecimal() - it.amount().toBigDecimal(),
                        unitValue: option.price() / option.display().units() - it.amount() / option.display().units(),
                        percentage: it.percentage().toBigDecimal(),
                        countryId: countryId
                )
            }
            new Discount(
                    id: promotion.id(),
                    description: promotion.description(),
                    expiration: new TimestampOutput(promotion.expiration().toString()),
                    label: labelBuilder.discount(steps),
                    remainingUses: promotion.remainingUses(),
                    progressive: promotion.progressive(),
                    steps: steps
            )
        }

        protected FreeProduct commercialPromotion(AvailableFreeProduct promotion) {
            new FreeProduct(
                    id: promotion.id(),
                    description: promotion.description(),
                    expiration: new TimestampOutput(promotion.expiration().toString()),
                    label: labelBuilder.freeProduct(),
                    remainingUses: promotion.remainingUses(),
                    from: promotion.from(),
                    quantity: promotion.quantity(),
                    product: new Product(product(promotion.product())),
                    display: new Display(
                            id: promotion.display().id().toInteger(),
                            ean: promotion.display().ean(),
                            units: promotion.display().units()
                    )
            )
        }

        protected Supplier supplier(AvailableOption option) {
            new Supplier(
                    id: option.supplier().id().toInteger(),
                    name: option.supplier().name(),
                    legalName: null,
                    avatar: toJava(option.supplier().avatar()).orElse(null),
                    deliveryZones: toJava(option.supplier().deliveryZones())
                            .map { asJava(it.toList()) }
                            .orElse([])
                            .collect {
                                new DeliveryZone(
                                        accessToken: this.accessToken.orElse(null),
                                        id: it.id().toInteger(),
                                        minAmount: it.requiredPurchaseAmount()._1().toBigDecimal(),
                                        maxAmount: toJava(it.requiredPurchaseAmount()._2())
                                                .map { it.toBigDecimal() }
                                                .orElse(null),
                                        deliveryCost: toJava(it.cost())
                                                .map { it.toBigDecimal() }
                                                .orElse(null)
                                )
                            },
                    rating: toJava(option.supplier().rating()).map {
                        new RatingScore(
                                count: it.count().toInteger(),
                                average: it.average().toDouble(),
                                percentage: it.percentage().toDouble()
                        )
                    }.orElse(null),
                    averageDeliveryDay: toJava(option.supplier().averageDeliveryTime())
                            .map { it.toDays().toString() },
                    accessToken: this.accessToken.orElse(null)
            )
        }

        protected static Display display(AvailableOption option) {
            new Display(
                    id: option.display().id().toInteger(),
                    ean: option.display().ean(),
                    units: option.display().units()
            )
        }

        protected List<Filter> filters(ProductQueryResponse response) {
            termFilter() +
                    categoryFilter(response) +
                    brandFilter(response) +
                    discountFilter() +
                    supplierFilter(response) +
                    featuresFilter(response) +
                    purchasedFilter()
        }

        protected List<Filter> termFilter() {
            toJava(request.filtering().byTerm())
                    .map {
                        [
                                new Filter(
                                        key: "keyword",
                                        value: { languageTag -> it.text() }
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
                                                        name: { LanguageTag languageTag -> it.name }
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
                                                        name: { LanguageTag languageTag -> it.name().defaultEntry() }
                                                )
                                        ]
                                )
                            }
                        }
                    }
                    .orElse([])
        }

        protected List<Filter> discountFilter() {
            toJava(request.filtering().byDiscount())
                    .map {
                        [
                                new Filter(
                                        key: "discount",
                                        values: [
                                                new FilterItem(
                                                        id: it.min() as Integer,
                                                        name: { LanguageTag languageTag ->
                                                            messageSource.getMessage(
                                                                    "search.DISCOUNT_SLICE",
                                                                    [it.min()].toArray(),
                                                                    forLanguageTag(
                                                                            ofNullable(languageTag.toString()).
                                                                                    orElse("en")
                                                                    )
                                                            )
                                                        }
                                                )
                                        ]
                                )
                        ]
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
                                                        name: { LanguageTag languageTag -> it.name() }
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

        protected List<Filter> purchasedFilter() {
            toJava(request.filtering().byPurchased())
                    .map {
                        [
                                new Filter(
                                        key: "purchased",
                                        value: { LanguageTag languageTag ->
                                            messageSource.getMessage(
                                                    "search.PURCHASED_FILTER",
                                                    [].toArray(),
                                                    forLanguageTag(
                                                            ofNullable(languageTag.toString()).
                                                                    orElse("en")
                                                    )
                                            )
                                        }
                                )
                        ]
                    }
                    .orElse([])
        }

        protected Sort sort() {
            def defaultAsc = new Tuple("DEFAULT", "ASC")
            def maybeSorting = toJava(request.sorting())
            def maybeByRelevance = maybeSorting
                    .filter { it instanceof ByRelevance$ }
                    .map { of(defaultAsc) }
            def maybeByLasAvailabilityUpdate = maybeSorting
                    .filter { it instanceof ByLastAvailabilityUpdate$ }
                    .map { of(new Tuple("RECENT", "ASC")) }
            def maybeByUnitPrice = maybeSorting
                    .filter { it instanceof ByUnitPrice }
                    .map { it as ByUnitPrice }
                    .map { of(new Tuple("PRICE", it.asc() ? "ASC" : "DESC")) }
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

        protected List<Facet> facets(ProductQueryResponse response) {
            [
                    categoriesFacet(response),
                    brandsFacet(response),
                    discountFacet(response),
                    suppliersFacet(response)
            ]
                    .findAll { it.isPresent() }
                    .collect { it.get() } +
                    featuresFacet(response)
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
                                                    name: { LanguageTag languageTag -> it._1().name() },
                                                    key: it._1().id()
                                            )
                                    )
                                }
                        )
                    }
        }

        protected Optional<Facet> discountFacet(ProductQueryResponse response) {
            toJava(response.aggregations().discounts())
                    .filter { request.filtering().byDiscount().isEmpty() }
                    .map {
                        new Facet(
                                id: "discount",
                                name: "discount",
                                slices: asJava(it.ranges()).collect {
                                    new Slices(
                                            size: it._2() as Long,
                                            obj: new Slice(
                                                    id: it._1(),
                                                    name: { LanguageTag languageTag ->
                                                        messageSource.getMessage(
                                                                "search.DISCOUNT_SLICE",
                                                                [it._1()].toArray(),
                                                                forLanguageTag(
                                                                        ofNullable(languageTag.toString()).
                                                                                orElse("en")
                                                                )
                                                        )
                                                    },
                                                    key: it._1()
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
                                                    name: { LanguageTag languageTag -> it._1().name().defaultEntry() },
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
                                                    name: { LanguageTag languageTag -> it._1().name().defaultEntry() },
                                                    key: it._1().id()
                                            )
                                    )
                                }
                        )
                    }
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
            of(value)
                    .filter { t ->
                        [IntValue, DoubleValue, StringValue].any { t._1() in it }
                    }
                    .map { t ->
                        new Slices(
                                size: t._2() as Long,
                                obj: new Slice(
                                        id: t._1().id(),
                                        key: t._1().id(),
                                        name: { LanguageTag languageTag -> name() }
                                )
                        )
                    }
        }

    }

    private class SearchResultMapper extends ProductQueryResponseMapper {

        SearchInput input

        SearchResultMapper(SearchInput input, ProductQueryRequest request) {
            super(request, input.accessToken)
            this.input = input
        }

        SearchResult map(ProductQueryResponse response) {
            new SearchResult(
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
        }

    }

    private class PreviewSearchResultMapper extends ProductQueryResponseMapper {

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
                            currentPage: new Page(input).number,
                            scroll: toJava(response.scroll()).orElse(null)
                    ),
                    sort: sort(),
                    breadcrumb: breadCrumb(response),
                    filters: filters(response),
                    facets: facets(response),
                    products: products(response).collect { new PreviewProductSearch(it) }
            )
        }

    }

    private class ScrollableSearchResultMapper extends ProductQueryResponseMapper {

        ScrollableSearchResultMapper(SearchScrollInput input) {
            this(input.accessToken)
        }

        ScrollableSearchResultMapper(PreviewSearchScrollInput input) {
            this()
        }

        private ScrollableSearchResultMapper(String accessToken = null) {
            super(null, accessToken)
        }

        ScrollableSearchResult map(ProductQueryResponse response) {
            new ScrollableSearchResult(
                    scroll: toJava(response.scroll()).orElse(null),
                    products: products(response)
            )
        }
    }

    private class SuggestionsMapper {

        static Suggestions map(SuggestionQueryResponse response) {
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
    }

    private class RefreshCartMapper extends ProductQueryResponseMapper {

        RefreshCartMapper(RefreshCartInput input, ProductQueryRequest request) {
            super(request, input.accessToken)
        }

        Cart map(ProductQueryResponse response) {
            def products = products(response)
            new Cart(
                    availableProducts: products.collect { new ProductCart(it, null, null) },
                    suppliers: products.collect { it.prices.collect { it.supplier } }
                            .flatten().toSet().toList() as List<Supplier>
            )
        }

    }

    private class SyncCartMapper extends ProductQueryResponseMapper {

        SyncCartInput input

        SyncCartMapper(SyncCartInput input, ProductQueryRequest request) {
            super(request, input.accessToken)
            this.input = input
        }

        SyncCartResult map(ProductQueryResponse response) {
            // we first retain all those products that contain requested items
            def available =
                    input.items.findResults { item ->
                        products(response).findResult { product ->
                            product.prices.findResult { price ->
                                if (item.productId == product.id &&
                                        item.supplierId == price.supplier.id.toInteger() &&
                                        item.units == price.display.units)
                                    new Tuple2(
                                            new ProductCart(product, price, item.quantity),
                                            price.commercialPromotions
                                                    .flatMap { it.discount } |
                                                    {
                                                        price.commercialPromotions
                                                                .flatMap { it.freeProduct }
                                                    }
                                    )
                                else null
                            }
                        }
                    }
            // then we grouped them by applied commercial promotion, sorting first those with free products
            def promoted =
                    available
                            .findAll { it.second.isPresent() }
                            .groupBy { it.second.get() }
                            .findResults {
                                def promotion = (it.key as CommercialPromotionType)
                                def selection = it.value.collect { it.first as ProductCart }
                                PromotedProductsCart.apply(
                                        // when grouped, promotion label must be rebuilt
                                        promotion.labeled(labelBuilder.apply(promotion, selection)),
                                        selection
                                ).orElse(null)
                            }
                            .sort { (it.commercialPromotion.type instanceof FreeProduct) ? -1 : 1 }
            // then we list those with no applied commercial promotion at all
            // sorting first those with available commercial promotion
            def unpromoted =
                    available
                            .findAll {
                                (it.second as Optional<CommercialPromotionType>)
                                        .filter { promotion ->
                                            promoted.any {
                                                it.commercialPromotions.contains(promotion)
                                            }
                                        }
                                        .isEmpty()
                            }
                            .collect { it.first as ProductCart }
                            .sort {
                                it.product.prices.findResult { it.commercialPromotions.isPresent() } ? -1 : 1
                            }
            new SyncCartResult(
                    promoted: promoted,
                    unpromoted: unpromoted
            )
        }

    }

    private class ProductMapper extends ProductQueryResponseMapper {

        ProductMapper(ProductQueryRequest request, String accessToken) {
            super(request, accessToken)
        }

        Optional<Product> map(ProductQueryResponse response) {
            def products = products(response)
            products.isEmpty() ? empty() : of(products.head()).map { new Product(it) }
        }

    }

    private class HomeBrandsResultMapper {

        static GetHomeBrandsResult map(BrandQueryResponse response) {
            new GetHomeBrandsResult(
                    brands: asJava(response.hits())
                            .findAll { it.logo().isDefined() }
                            .collect {
                                new Brand(
                                        id: it.id().toInteger(),
                                        name: it.name().defaultEntry(),
                                        logo: toJava(it.logo()).orElse(null)
                                )
                            }
            )
        }

    }

    private class PreviewHomeSupplierResponseMapper {

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

    private class PromotionResponseMapper {

        static PromotionResponse map(PromotionQueryResponse response) {
            new PromotionResponse(
                    content: asJava(response.hits()).collect {
                        new Promotion(
                                id: it.id().toInteger(),
                                tag: it.tag(),
                                banner: it.banner(),
                                banner_mobile: it.bannerMobile()
                        )
                    }
            )
        }

    }

}