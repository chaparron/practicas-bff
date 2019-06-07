package bff.model

import graphql.GraphQLError
import groovy.transform.InheritConstructors


interface PlaceOrderResult {}

interface CartResult {}

@InheritConstructors
class OrderUpdateFailed extends RuntimeException implements OrderUpdateResult {
    OrderUpdateReason orderUpdateReason
}

enum OrderUpdateReason {
    INVALID_SUPPLIER_ORDERS_STATUS,
    ORDER_NOT_FOUND,
    INVALID_SUPPLIER,
    INVALID_DELIVERY_COST,
    INVALID_PRODUCTS

    def build() {
        new OrderUpdateFailed(orderUpdateReason: this)
    }
}



enum SupplierOrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    CANCELED
}

enum OrderStatus {
    PENDING,
    IN_PROGRESS,
    FINISHED
}

enum RatingOwner {
    SUPPLIER,
    CUSTOMER
}


enum RatingStatus {
    ENABLED,
    DISABLED,
    REPORTED
}


interface OrderUpdateResult {}
interface CustomerOrdersResult {}

class CancelOrderInput {
    String accessToken
    Integer orderId
}

class SortResult {
    String field
    Direction direction

    enum Direction {
        ASC, DESC
    }
}


class PaginatedInput {
    Long page
    Long size
    SortResult sort = new SortResult()
}

class FindOrdersInput extends PaginatedInput {
    String accessToken
    Integer orderId
    SupplierOrderStatus status

}

class Headers{
    Long page
    Long pageSize
    Long total
    SortResult sort = new SortResult()
}

class CustomerOrdersResponse extends PaginatedResponse<Order> implements CustomerOrdersResult {
}

class PaginatedResponse<T> {
    Headers headers = new Headers()
    List<T> content
}

class Order {
    String accessToken
    Long id
    OrderStatus status
    TimestampOutput created
    TimestampOutput updated
    DeliveryPreference deliveryPreference
}

class SupplierOrder {
    String accessToken
    Long id
    String receipt
    SupplierOrderStatus status
    TimestampOutput created
    TimestampOutput updated
    TimestampOutput shippedAt
    TimestampOutput shipAt
    Double deliveryCost
    Double total
    Integer units
    Boolean canCustomerRate
    Boolean canSupplierRate
    Boolean customerRated
    Boolean supplierRated
}

class RatingEntry {
    Rating SUPPLIER
    Rating CUSTOMER
}

class Rating {
    Long id
    Supplier supplier
    Customer customer
    User user
    TimestampOutput created
    String opinion
    Long score
    RatingOwner owner
    RatingStatus status
    Float percentage
}

class OrderItem {
    String accessToken
    Long id
    String productTitle
    String productEan
    Double price
    Integer quantity
    Double subtotal
}

enum PlaceOrderFailedReason {
    DELIVERY_COST_HAS_CHANGED,
    INVALID_CUSTOMER,
    BAD_REQUEST,
    PRODUCT_PRICES_HAS_CHANGED,
    SUPPLIER_AVAILABILITY_HAS_CHANGED,
    INVALID_SUPPLIER_ORDER,
    INVALID_ORDER

    def build() {
        new PlaceOrderFailed(reason: this)
    }
}

class CustomerOrderFindFailed implements CustomerOrdersResult {
    CustomerOrderFindFailedReason reason
}

enum CustomerOrderFindFailedReason {
    ORDER_NOT_FOUND

    def build() {
        new CustomerOrderFindFailed(reason: this)
    }
}

enum CartFailedReason {
    FORBIDDEN,
    BAD_REQUEST,
    INVALID_ADDRESS,
    EMPTY_PRODUCTS

    def build() {
        new CartFailed(reason: this)
    }

}

class Cart implements CartResult {
    List<ProductCart> products //dumb
    List<ProductCart> availableProducts
    List<Supplier> suppliers
}

class ProductCart {
    Product product
    List<SupplierPrice> suppliers //dumb
    List<SupplierPrice> supplierPrices
}

class SupplierPrice {
    Integer id
    String name
    Double price
    Integer minUnits
    Integer maxUnits
    String avatar
}

class CartFailed implements CartResult {
    CartFailedReason reason
}

class RefreshCartInput {
    String accessToken
    List<Integer> products
}

class PlaceOrderFailed implements PlaceOrderResult {
    PlaceOrderFailedReason reason
}

class ProductOrderInput {
    Integer productId
    Integer quantity
    Double price
}

class OrderInput {
    Integer supplierId
    Double deliveryCost
    List<ProductOrderInput> products
}


class PlaceOrderInput {
    String accessToken
    List<OrderInput> orders
}