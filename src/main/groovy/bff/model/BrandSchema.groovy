package bff.model

interface GetHomeBrandsResponse { }

class GetHomeBrandsResult implements GetHomeBrandsResponse {
    List<Brand> brands
}

enum GetBrandsFailedReason {
    NOT_FOUND,
    BAD_REQUEST

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
}

class GetBrandsInput {
    String accessToken
    String countryId
}
