package bff.bridge

import bff.model.Brand
import bff.model.Category
import bff.model.Manufacturer

interface ProductBridge {

    Category getCategoryByProductId(String accessToken, Integer productId)

    Manufacturer getManufacturerByProductId(String accessToken, Integer productId)

    Brand getBrandByProductId(String accessToken, Integer productId)

}