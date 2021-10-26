package bff.bridge

interface SupplierBridge {

    String getAverageDeliveryDays(String accessToken, Long supplierId)

}