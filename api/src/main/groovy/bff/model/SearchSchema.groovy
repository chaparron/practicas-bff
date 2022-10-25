package bff.model

import bff.service.ImageSizeEnum
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import sun.util.locale.LanguageTag

import static java.util.Optional.*

interface ProductResult {}

interface SearchResponse {}

enum SearchFailedReason {
    INVALID_ADDRESS,
    FORBIDDEN,
    NO_SUPPLIERS_FOUND,
    INVALID_LOCATION


    def build() {
        new SearchFailed(reason: this)
    }
}

class SearchFailed implements SearchResponse {
    SearchFailedReason reason
}

class SearchResult implements SearchResponse {
    Header header
    Sort sort
    List<BreadCrumb> breadcrumb
    List<Filter> filters
    List<ProductSearch> products
    List<Facet> facets
    Faceting faceting
}

class PreviewSearchResult implements SearchResponse {
    Header header
    Sort sort
    List<BreadCrumb> breadcrumb
    List<Filter> filters
    List<PreviewProductSearch> products
    List<Facet> facets
    Faceting faceting
}

class ScrollableSearchResult {
    String scroll
    List<ProductSearch> products
}

class Suggestions {

    private List<SuggestedProduct> products
    private List<SuggestedBrand> brands
    private List<SuggestedCategory> categories
    private List<SuggestedSupplier> suppliers

    List<SuggestedProduct> products(Integer size) {
        this.products.take(size)
    }

    List<SuggestedBrand> brands(Integer size) {
        this.brands.take(size)
    }

    List<SuggestedCategory> categories(Integer size) {
        this.categories.take(size)
    }

    List<SuggestedSupplier> suppliers(Integer size) {
        this.suppliers.take(size)
    }

}

class SuggestedProduct {
    Integer id
    String name
}

class SuggestedBrand {
    Integer id
    String name
    String logo
    List<Badge> badges
}

class SuggestedCategory {
    Integer id
    String name
}

class SuggestedSupplier {
    Integer id
    String name
    String avatar
    List<Badge> badges
}

class PreviewSearchResultMapper {
    Header header
    Sort sort
    List<BreadCrumb> breadcrumb
    Map filters
    List<PreviewProductSearch> products
    List<Facet> facets
}

class SearchResultMapper {
    Header header
    Sort sort
    List<BreadCrumb> breadcrumb
    Map filters
    List<ProductSearch> products
    List<Facet> facets
}

class BreadCrumb {
    Integer id
    String name
}


class Header {
    Integer total
    Integer pageSize
    Integer currentPage
    String scroll

    @JsonProperty("page_size")
    void setPageSize(Integer pageSize) {
        this.pageSize = pageSize
    }

    @JsonProperty("current_page")
    void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage
    }
}

@ToString
class PreviewSearchInput {
    String keyword
    String countryId
    String sort
    SortInput sortDirection
    Integer category
    Integer page
    Integer size
    Integer brand
    Set<String> brands
    Integer supplier
    Set<String> suppliers
    String tag
    List<FeatureInput> features
    BigDecimal lat
    BigDecimal lng
    Integer similarTo
    Boolean promoted
    Integer discount
    String commercialPromotion
    String collection
    Integer bottler
    Integer manufacturer
    Boolean freeProduct
    Boolean facets
    Optional<CategoryFaceting> categoryFaceting = empty()
    Optional<BrandFaceting> brandFaceting = empty()
    Optional<FeatureFaceting> featureFaceting = empty()
    Optional<DiscountFaceting> discountFaceting = empty()

    def facetingByCategories(Integer depth, Boolean flattened) {
        categoryFaceting = of(new CategoryFaceting(depth: depth, flattened: flattened))
        this
    }

    def facetingByBrands(Integer size, Optional<BrandFacetSorting> sorting) {
        brandFaceting = of(new BrandFaceting(size: size, sorting: sorting))
        this
    }

    def facetingByFeatures(Integer size, Optional<FeatureFacetSorting> sorting, Set<String> features) {
        featureFaceting = of(new FeatureFaceting(size: size, sorting: sorting, features: features))
        this
    }

    def facetingByDiscounts(Integer interval) {
        discountFaceting = of(new DiscountFaceting(interval: interval))
        this
    }

}

class CategoryFaceting {
    Integer depth
    Boolean flattened
}

class BrandFaceting {
    Integer size
    Optional<BrandFacetSorting> sorting
}

class SupplierFaceting {
    Integer size
    Optional<SupplierFacetSorting> sorting
}

class DiscountFaceting {
    Integer interval
}

class FeatureFaceting {
    Integer size
    Optional<FeatureFacetSorting> sorting
    Set<String> features
}

@ToString(excludes = ["accessToken"])
class SearchInput {
    String accessToken
    Integer addressId
    String keyword
    String sort
    SortInput sortDirection
    Integer category
    Integer page
    Integer size
    Integer brand
    Set<String> brands
    Integer supplier
    Set<String> suppliers
    String tag
    List<FeatureInput> features
    Boolean favourites
    Integer similarTo
    Boolean promoted
    Integer discount
    String commercialPromotion
    Boolean purchased
    String collection
    Integer bottler
    Integer manufacturer
    Boolean freeProduct
    Boolean facets
    Optional<CategoryFaceting> categoryFaceting = empty()
    Optional<BrandFaceting> brandFaceting = empty()
    Optional<SupplierFaceting> supplierFaceting = empty()
    Optional<FeatureFaceting> featureFaceting = empty()
    Optional<DiscountFaceting> discountFaceting = empty()

    def facetingByCategories(Integer depth, Boolean flattened) {
        categoryFaceting = of(new CategoryFaceting(depth: depth, flattened: flattened))
        this
    }

    def facetingByBrands(Integer size, Optional<BrandFacetSorting> sorting) {
        brandFaceting = of(new BrandFaceting(size: size, sorting: sorting))
        this
    }

    def facetingBySuppliers(Integer size, Optional<SupplierFacetSorting> sorting) {
        supplierFaceting = of(new SupplierFaceting(size: size, sorting: sorting))
        this
    }

    def facetingByFeatures(Integer size, Optional<FeatureFacetSorting> sorting, Set<String> features) {
        featureFaceting = of(new FeatureFaceting(size: size, sorting: sorting, features: features))
        this
    }

    def facetingByDiscounts(Integer interval) {
        discountFaceting = of(new DiscountFaceting(interval: interval))
        this
    }

}

@ToString(excludes = ["accessToken"])
@EqualsAndHashCode
class SearchScrollInput {
    String accessToken
    String scroll
}

@ToString
@EqualsAndHashCode
class PreviewSearchScrollInput {
    String scroll
}

@ToString(excludes = ["accessToken"])
class SuggestInput {
    String accessToken
    String keyword
    LanguageTag languageTag
    Boolean favourites
    Integer category
    Optional<Integer> maybeProducts = empty()
    Optional<Integer> maybeBrands = empty()
    Optional<Integer> maybeCategories = empty()
    Optional<Integer> maybeSuppliers = empty()

    def forProducts(Integer size) {
        this.maybeProducts = of(size)
        return this
    }

    def forBrands(Integer size) {
        this.maybeBrands = of(size)
        return this
    }

    def forCategories(Integer size) {
        this.maybeCategories = of(size)
        return this
    }

    def forSuppliers(Integer size) {
        this.maybeSuppliers = of(size)
        return this
    }

}

@ToString
class PreviewSuggestInput {

    String country
    BigDecimal lat
    BigDecimal lng
    String keyword
    LanguageTag languageTag
    Integer category
    Optional<Integer> maybeProducts = empty()
    Optional<Integer> maybeBrands = empty()
    Optional<Integer> maybeCategories = empty()
    Optional<Integer> maybeSuppliers = empty()

    def forProducts(Integer size) {
        this.maybeProducts = of(size)
        return this
    }

    def forBrands(Integer size) {
        this.maybeBrands = of(size)
        return this
    }

    def forCategories(Integer size) {
        this.maybeCategories = of(size)
        return this
    }

    def forSuppliers(Integer size) {
        this.maybeSuppliers = of(size)
        return this
    }

}

@ToString(excludes = ["accessToken"])
class MostSearchedTermsInput {
    String accessToken
}

class PreviewMostSearchedTermsInput {
    String country
    BigDecimal lat
    BigDecimal lng
}

class MostSearchedTerm {
    String text
    Optional<String> language
    Category category
    Closure<String> label
}

class FeatureInput {
    String id
    String value
}

enum SortInput {
    DESC,
    ASC,
    DEFAULT
}


class PreviewProductSearch implements ProductResult, Piece {
    Long id
    String name
    Boolean enabled
    Category category
    Brand brand
    String ean
    String description
    List<Keyword> keywords
    List<Feature> features
    List<Image> images
    TimestampOutput created
    Manufacturer manufacturer
    List<PreviewPrice> prices
    PreviewPrice priceFrom
    PreviewPrice minUnitsPrice
    PreviewPrice highlightedPrice
    /**
     * @deprecated Suppliers list should not be used in search preview due private information.
     * Use {@link PreviewProductSearch#totalNumberOfSuppliers} instead to retrieve the total number
     * of available suppliers for the given product.
     */
    @Deprecated
    List<PreviewSupplier> suppliers
    Integer totalNumberOfSuppliers
    String title
    String country_id

    PreviewProductSearch(ProductSearch product) {
        def suppliers =
                product.prices.collect { new PreviewSupplier(id: it.supplier.id, name: "") }
                        .toSet().toList()
        this.id = product.id
        this.name = product.name
        this.category = product.category
        this.brand = product.brand
        this.ean = product.ean
        this.description = product.description
        this.images = product.images
        this.prices = product.prices.collect { new PreviewPrice(it) }
        this.priceFrom = new PreviewPrice(product.priceFrom)
        this.minUnitsPrice = new PreviewPrice(product.minUnitsPrice)
        this.highlightedPrice = new PreviewPrice(product.highlightedPrice)
        this.title = product.title
        this.country_id = product.country_id
        this.totalNumberOfSuppliers = suppliers.size()
        this.suppliers = suppliers
    }
}

class PreviewPrice {
    String countryId
    Long id
    BigDecimal value
    Money valueMoney
    BigDecimal unitValue
    Money unitValueMoney
    Display display
    Integer minUnits
    Optional<CommercialPromotions> commercialPromotions

    PreviewPrice(Price price) {
        this.countryId = price.countryId
        this.id = price.id
        this.value = price.value
        this.unitValue = price.unitValue
        this.display = price.display
        this.minUnits = price.minUnits
        this.commercialPromotions = price.commercialPromotions
    }
}

enum SupplierAvatarSize implements ImageSizeEnum {
    SIZE_120x50, SIZE_96x40, SIZE_22x22

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

@EqualsAndHashCode
class PreviewSupplier implements Piece {
    Long id
    String name
    String legalName
    String avatar
    List<Badge> badges
}

enum ProductImageSize implements ImageSizeEnum {
    SIZE_148x148, SIZE_85x85, SIZE_96x96

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

class ProductSearch implements ProductResult, Piece {
    String accessToken
    Long id
    String name
    Boolean enabled
    Category category
    Brand brand
    String ean
    String description
    List<Keyword> keywords
    List<Feature> features
    List<Image> images
    List<Display> displays
    Manufacturer manufacturer
    String title
    boolean favorite
    String country_id
    List<Price> prices
    Price priceFrom
    Price minUnitsPrice
    Price highlightedPrice

    ProductSearch() {}

    ProductSearch(
            Long id,
            String name,
            String ean,
            String description,
            String country_id,
            Category category,
            Brand brand,
            List<Image> images,
            List<Display> displays,
            List<Price> prices,
            Boolean enabled,
            boolean favorite,
            String accessToken
    ) {
        this.id = id
        this.name = name
        this.title = name
        this.ean = ean
        this.description = description
        this.country_id = country_id
        this.category = category
        this.brand = brand
        this.images = images
        this.displays = displays
        this.prices = prices
        this.enabled = enabled
        this.favorite = favorite
        this.accessToken = accessToken
        this.priceFrom = prices.min { it.netValue() }
        this.minUnitsPrice = prices.min { Price a, Price b ->
            (a.minUnits == b.minUnits) ? a.unitValue <=> b.unitValue : a.minUnits <=> b.minUnits
        }
        this.highlightedPrice =
                ofNullable(
                        prices.find {
                            it.commercialPromotions
                                    .flatMap { it.freeProduct }
                                    .orElse(null)
                        }
                ).orElse(prices.min { it.netUnitValue() })
    }
}


class Product implements ProductResult {
    String accessToken
    Long id
    String name
    Boolean enabled
    String ean
    String description
    TimestampOutput created
    String title
    List<Price> prices
    List<Display> displays
    Price priceFrom
    Price minUnitsPrice
    Price highlightedPrice
    Brand brand
    String country_id
    boolean favorite

    Product() {}

    Product(ProductSearch product) {
        this.accessToken = product.accessToken
        this.id = product.id
        this.name = product.name
        this.enabled = product.enabled
        this.ean = product.ean
        this.description = product.description
        this.created = null
        this.title = product.title
        this.prices = product.prices
        this.displays = product.displays
        this.priceFrom = product.priceFrom
        this.minUnitsPrice = product.minUnitsPrice
        this.highlightedPrice = product.highlightedPrice
        this.brand = product.brand
        this.country_id = product.country_id
        this.favorite = product.favorite
    }

}

@ToString
@EqualsAndHashCode
class Category implements Serializable {
    Long id
    Long parentId
    String name
    Boolean enabled
    Boolean isLeaf
    String country_id

    @JsonProperty("parent_id")
    void setParentId(Long parentId) {
        this.parentId = parentId
    }

    @JsonProperty("is_leaf")
    void setIsLeaf(Boolean isLeaf) {
        this.isLeaf = isLeaf
    }
}

class Image {
    String id
    String originalName
    TimestampOutput added
    Integer imageOrder

    @JsonProperty("image_order")
    void setImageOrder(Integer imageOrder) {
        this.imageOrder = imageOrder
    }
}

class Price {
    Long id
    String accessToken
    Supplier supplier
    BigDecimal value
    Money valueMoney
    BigDecimal unitValue
    Money unitValueMoney
    Boolean enabled
    Integer minUnits
    Integer maxUnits
    Display display
    TimestampOutput updated
    List<Promotion> promotions
    SupplierProductConfiguration configuration
    Optional<CommercialPromotions> commercialPromotions
    String countryId

    def netValue() {
        commercialPromotions
                .flatMap { it.discount }
                .map { it.minValue() }
                .orElse(value)
    }

    def netUnitValue() {
        commercialPromotions
                .flatMap { it.discount }
                .map { it.minUnitValue() }
                .orElse(unitValue)
    }

}

@EqualsAndHashCode
class Display {
    Integer id
    String ean
    Integer units

}

class Prices {
    List<Price> prices
}

class HomeSupplier {
    String accessToken
    Long id
    String name
    String avatar
}

@EqualsAndHashCode
class WabipayConfiguration {
    Boolean use_wabipay_cap
    Integer order_percentage_cap
}

@EqualsAndHashCode
class Supplier implements SupplierResponse {
    String accessToken
    Long id
    String name
    String legalName
    String avatar
    Boolean enabled
    String phone
    String legalId
    String address
    String postalCode
    BigDecimal maxAmount
    BigDecimal minAmount
    List<DeliveryZone> deliveryZones
    RatingScore rating
    String country_id
    Optional<String> averageDeliveryDay
    Integer orderPercentageCap
    WabipayConfiguration wabipayConfiguration
}

@EqualsAndHashCode
class DeliveryZone {
    String accessToken
    Long id
    BigDecimal minAmount
    Money minAmountMoney
    BigDecimal maxAmount
    Money maxAmountMoney
    BigDecimal deliveryCost
    Money deliveryCostMoney
}

@EqualsAndHashCode
class RatingScore {
    Double average
    Integer count
    Double percentage
}

class Manufacturer {
    Long id
    String name
    Boolean enabled
    String phone
    String avatar
    String country_id
}


class Keyword {
    Long id
    String name
    Boolean enabled
}

class Feature {
    Long id
    String name
    Boolean required
    Boolean hasUnit
    FeatureType featureType
    List<FeatureValue> allowedValues
    FeatureValue value
    Category category
    Boolean facetable

    @JsonProperty("has_unit")
    void setHasUnit(Boolean hasUnit) {
        this.hasUnit = hasUnit
    }

}

class FeatureValue {
    Long id
    String name
    Feature feature
    MeasurementUnit unit
    Boolean enabled
}

class MeasurementUnit {
    Long id
    String name
    Boolean enabled
}

interface RootCategoriesResponse {

}

class RootCategoriesFailed implements RootCategoriesResponse {
    RootCategoriesFailedReasons reason

}

enum RootCategoriesFailedReasons {
    BAD_REQUEST,
    INVALID_LOCATION,
    NO_SUPPLIERS_FOUND

    def build() {
        new RootCategoriesFailed(reason: this)
    }
}

class RootCategoriesResult implements RootCategoriesResponse {
    List<Category> categories
}

enum FeatureType {
    SINGLE,
    MULTIPLE,
    BINARY,
    RANGE
}

enum ProductErrorReason {
    PRODUCT_NOT_FOUND,
    BAD_REQUEST

    def build() {
        new ProductFailed(reason: this)
    }
}

class ProductInput {
    String accessToken
    Integer productId
}

class ProductEanInput {
    String accessToken
    String ean
}

class ProductFailed implements ProductResult {
    ProductErrorReason reason
}


enum SupplierFailedReason {
    NOT_FOUND

    def build() {
        new SupplierFailed(reason: this)
    }
}

class SupplierFailed implements SupplierResponse {
    SupplierFailedReason reason
}

interface HomeSupplierResult {

}

class PreviewHomeSupplierResponse implements HomeSupplierResult {
    List<PreviewSupplier> suppliers
}

class PreviewHomeSupplierFailed implements HomeSupplierResult {
    PreviewHomeSupplierFailedReason reason
}

enum PreviewHomeSupplierFailedReason {
    INVALID_LOCATION,
    NOT_FOUND,
    BAD_REQUEST,
    INVALID_COUNTRY_ID,
    NO_SUPPLIERS_FOUND

    def build() {
        return new PreviewHomeSupplierFailed(reason: this)
    }
}

enum BrandFacetSorting {
    BY_FREQUENCY,
    ALPHABETICALLY
}

enum SupplierFacetSorting {
    BY_FREQUENCY,
    ALPHABETICALLY
}

enum FeatureFacetSorting {
    BY_FREQUENCY,
    ALPHABETICALLY
}

class Faceting {
    Optional<CategoryFacet> categories
    Optional<BrandFacet> brands
    Optional<SupplierFacet> suppliers
    List<FeatureFacet> features
    Optional<DiscountFacet> discounts
}

class CategoryFacet {
    List<CategorySlice> slices
}

class CategorySlice {
    String id
    Closure<String> name
    Integer frequency
    List<CategorySlice> slices
}

class BrandFacet {
    Integer cardinality
    List<BrandSlice> slices
}

class BrandSlice {
    String id
    Closure<String> name
    String logo
    List<Badge> badges
    Integer frequency
    Boolean selected
}

class SupplierFacet {
    Integer cardinality
    List<SupplierSlice> slices
}

class SupplierSlice {
    String id
    String name
    String avatar
    List<Badge> badges
    Integer frequency
    Boolean selected
}

class FeatureFacet {
    String id
    Closure<String> name
    Integer cardinality
    List<FeatureSlice> slices
}

class FeatureSlice {
    String id
    Closure<String> label
    Integer frequency
    Boolean selected
}

class DiscountFacet {
    List<DiscountSlice> slices
}

class DiscountSlice {
    Integer value
    Closure<String> label
    Integer frequency
}







