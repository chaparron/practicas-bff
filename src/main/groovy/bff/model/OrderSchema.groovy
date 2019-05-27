package bff.model


import groovy.transform.InheritConstructors


@InheritConstructors
class OrderUpdateFailed extends RuntimeException implements OrderUpdateResult {
    OrderUpdateReason orderUpdateReason
}

enum OrderUpdateReason {
    INVALID_SUPPLIER_ORDERS_STATUS,
    ORDER_NOT_FOUND

    def doThrow() {
        throw new OrderUpdateFailed(orderUpdateReason: this)
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
    Long id
    Order order
    Supplier supplier
    String receipt
    SupplierOrderStatus status
    TimestampOutput created
    TimestampOutput updated
    TimestampOutput shippedAt
    TimestampOutput shipAt
    Double deliveryCost
    Double total
    Integer units
    List<OrderItem> products
    RatingEntry ratings
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
    SupplierOrder supplierOrder
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
    Long id
    SupplierOrder supplierOrder
    Product product
    String productTitle
    String productEan
    Double price
    Integer quantity
    Double subtotal
}