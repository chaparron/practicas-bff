package bff.bridge

import bff.model.CoordinatesInput
import bff.model.GetHomeBrandsResult

interface BrandBridge {

    GetHomeBrandsResult getHome(String accessToken, String countryId)

    GetHomeBrandsResult previewHomeBrands(CoordinatesInput coordinatesInput)
}