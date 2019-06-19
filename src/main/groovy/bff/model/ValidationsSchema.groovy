package bff.model

class ValidateUsernameInput {
    Long id
    String username
}

class ValidateInput {
    String value
    Long id
    String country_id
    String accessToken
    ValidationType validationType
}

enum ValidationType {
    USER_PHONE("userPhone"),
    USER_CELL_PHONE("userCellphone"),
    CUSTOMER_LEGAL_ID("customerLegalId"),
    CUSTOMER_LINE_PHONE("customerLinePhone"),
    MANUFACTURER_NAME("manufacturerName"),
    SUPPLIER_NAME("supplierName"),
    SUPPLIER_LEGAL_ID("supplierLegalId"),
    SUPPLIER_LEGAL_NAME("supplierLegalName"),
    BRAND_NAME("brandName"),
    USER_USERNAME("userUsername")


    String name

    ValidationType(name) {
        this.name = name

    }
}