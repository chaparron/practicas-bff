package bff.bridge

import bff.model.GetHomeBrandsResult

interface BrandBridge {

    GetHomeBrandsResult getHome(String accessToken, String countryId)
}