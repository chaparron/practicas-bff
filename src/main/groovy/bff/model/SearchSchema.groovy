package bff.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

interface ProductResult{}



class SearchResult {
    Header header
    Sort sort
    List<BreadCrumb> breadcrumb
    List<Filter> filters
    List<ProductSearch> products
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

    @JsonProperty("page_size")
    void setPageSize(Integer pageSize) {
        this.pageSize = pageSize
    }

    @JsonProperty("current_page")
    void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage
    }
}

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
    Integer supplier
    String tag
    List<FeatureInput> features
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


class ProductSearch implements ProductResult {
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
    TimestampOutput created
    Manufacturer manufacturer
    List<Price> prices
    Price priceFrom
    Price minUnitsPrice
    Price highlightedPrice
    String title
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
    Price priceFrom
    Price minUnitsPrice
    Price highlightedPrice
    Brand brand
    String country_id
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
    String accessToken
    Supplier supplier
    Double value
    Boolean enabled
    Integer minUnits
    Integer maxUnits
    TimestampOutput updated
    List<Promotion> promotions
    SupplierProductConfiguration configuration
}

class Prices {
    List<Price> prices
}

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
    Double maxAmount
    Double minAmount
    Double deliveryCost
    List<DeliveryZone> deliveryZones
    RatingScore rating
    String country_id

}

class DeliveryZone {
    Long id
    Double minAmount
    Double maxAmount
    Double deliveryCost
}

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






