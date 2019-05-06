package bff.model

import com.fasterxml.jackson.annotation.JsonProperty

class SearchResult {
    Header header
    Sort sort
    List<BreadCrumb> breadCrumb
    List<Product> products
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


class Product {
    Long id
    String name
    Boolean enabled
    Category category
    Brand brand
    String ean
    Long units
    String description
    List<Keyword> keywords
    List<Feature> features
    List<Image> images
    TimestampOutput created
    Manufacturer manufacturer
    List<Price> prices
    Price priceFrom
    String title
}

class Category {
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
    Supplier supplier
    Double value
    Boolean enabled
    TimestampOutput updated
    Double unitValue
}

class Supplier {
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
    List<Coordinates> coordinates
}

class Coordinates {
    Double lat
    Double lng
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
    List<Product> products
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






