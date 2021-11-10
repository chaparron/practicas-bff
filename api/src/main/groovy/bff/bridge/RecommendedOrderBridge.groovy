package bff.bridge

import bff.model.FavoriteProductInput

interface RecommendedOrderBridge {

    Boolean markProductAsFavorite(FavoriteProductInput favoriteProductInput)

    Boolean unmarkFavoriteProduct(FavoriteProductInput favoriteProductInput)
}
