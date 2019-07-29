package bff.bridge

import bff.model.*

interface ProductBridge {

    Category getCategoryByProductId(String accessToken, Long productId)

    Manufacturer getManufacturerByProductId(String accessToken, Long productId)

    Brand getBrandByProductId(String accessToken, Long productId)

    Product getProductById(String accessToken, Long productId)

    Product getProductByEan(String accessToken, String ean)

    List<Feature> getFeaturesByProductId(String accessToken, Long productId)

    List<Image> getImagesByProductId(String accessToken, Long productId)

    List<Price> getPricesByProductId(String accessToken, Long productId)

    List<Keyword> getKeywordsByProductId(String accessToken, Long productId)

    Supplier getSupplierById(String accessToken, Long supplierId)

    Cart refreshCart(String accessToken, List<Integer> products)

}