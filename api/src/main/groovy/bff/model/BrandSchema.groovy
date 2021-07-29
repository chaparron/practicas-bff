package bff.model

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


class Brand {
    Long id
    String name
    Boolean enabled
    String logo
    String country_id
}

class GetBrandsInput {
    String accessToken
    String countryId
}