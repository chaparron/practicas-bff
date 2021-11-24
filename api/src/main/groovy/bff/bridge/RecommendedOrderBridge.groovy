package bff.bridge

import bff.model.FavouriteProductInput

interface RecommendedOrderBridge {

    Boolean setFavouriteProduct(FavouriteProductInput favoriteProductInput)

    Boolean unsetFavouriteProduct(FavouriteProductInput favoriteProductInput)
}
