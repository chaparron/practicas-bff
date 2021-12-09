package bff.model

interface CreditsSchema {}

class FindSupplierCreditBalancesInput {
    String accessToken
    Integer first
    String after
    Integer last
    String before
}

class PageResponse{
    PageInfoResponse pageInfo
    List<EdgeResponse> items
}

class PageInfoResponse{
    Boolean hasNextPage
    Boolean hasPreviousPage
    String startCursor
    String endCursor
}

class EdgeResponse{
    String cursor
    BalanceItem item
}

class BalanceItem{
    String customerId
    String supplierId
    Float availableCreditAmount
    Float limitAmount
}


