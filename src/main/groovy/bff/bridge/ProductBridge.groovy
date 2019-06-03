package bff.bridge

import bff.model.Brand
import bff.model.Category
import bff.model.Feature
import bff.model.Image
import bff.model.Keyword
import bff.model.Manufacturer
import bff.model.Price
import bff.model.Prices
import bff.model.Product
import bff.model.Supplier

interface ProductBridge {

    Category getCategoryByProductId(String accessToken, Long productId)

    Manufacturer getManufacturerByProductId(String accessToken, Long productId)

    Brand getBrandByProductId(String accessToken, Long productId)

    Product getProductById(String accessToken, Long productId)

    List<Feature> getFeaturesByProductId(String accessToken, Long productId)

    List<Image> getImagesByProductId(String accessToken, Long productId)

    Prices getPricesByProductId(String accessToken, Long productId)

    Price getPriceFromByProductId(String accessToken, Long productId)

    Price getMinUnitsPriceByProductId(String accessToken, Long productId)

    List<Keyword> getKeywordsByProductId(String accessToken, Long productId)

    Supplier getSupplierById(String accessToken, Long supplierId)

}