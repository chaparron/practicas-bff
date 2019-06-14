package bff.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


interface ProductResult{}

interface PriceResult {}


class SearchResult {
    Header header
    Sort sort
    List<BreadCrumb> breadCrumb
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
    Integer categoryId
    Integer page
    Integer size
    Integer brand
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

    Brand brand
}

@ToString
@EqualsAndHashCode
class Category implements Serializable {
    Long id
    Long parentId
    String name
    Boolean enabled
    Boolean isLeaf

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
    Long supplierId
    String accessToken
    Double value
    Boolean enabled
    Integer minUnits
    Integer maxUnits
    TimestampOutput updated
}

class Prices implements PriceResult {
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
    RatingScore rating
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

}


class Brand {
    Long id
    String name
    Boolean enabled
    String logo
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

class ProductFailed implements ProductResult {
    ProductErrorReason reason
}

enum PriceErrorReason {
    PRICE_NOT_FOUND,
    NO_SUPPLIER_FOUND

    def build() {
        new PriceFailed(reason: this)
    }
}

class PriceFailed implements PriceResult {
    PriceErrorReason reason
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




