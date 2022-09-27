package bff.model

import bff.service.ImageSizeEnum

interface GetHomeBrandsResponse { }

class GetHomeBrandsResult implements GetHomeBrandsResponse {
    List<Brand> brands
}

enum GetBrandsFailedReason {
    NOT_FOUND,
    BAD_REQUEST,
    INVALID_COUNTRY_ID,
    NO_SUPPLIERS_FOUND,
    INVALID_LOCATION,

    def build() {
        new GetHomeBrandsFailed(reason: this)
    }
}

class GetHomeBrandsFailed implements GetHomeBrandsResponse {
    GetBrandsFailedReason reason
}


class Brand implements Piece {
    Long id
    String name
    Boolean enabled
    String logo
    String country_id
    Optional<Boolean> discount
}

enum BannerLogoSize implements ImageSizeEnum {
    SIZE_170x130, SIZE_130x61, SIZE_96x40, SIZE_22x22

    @Override
    String value() {
        name().substring("SIZE_".length())
    }

}

class GetBrandsInput {
    String accessToken
    String countryId
}