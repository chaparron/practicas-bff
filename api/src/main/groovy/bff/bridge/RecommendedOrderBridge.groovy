package bff.bridge

import bff.model.FavoriteProductResult
import bff.model.FrequentProductResult
import bff.model.GetFavoriteProductsInput
import bff.model.GetFrequentProductsInput
import bff.model.ProductToMarkAsFavoriteInput
import bff.model.ProductToUnmarkAsFavoriteInput
import bff.model.Void

interface RecommendedOrderBridge {

    List<FrequentProductResult> getFrequentProducts(GetFrequentProductsInput getFrequentProductsInput)

    List<FavoriteProductResult> getFavoriteProducts(GetFavoriteProductsInput getFavoriteProductsInput)

    Void markProductAsFavorite(ProductToMarkAsFavoriteInput productToMarkAsFavoriteInput)

    Void unmarkProductAsFavorite(ProductToUnmarkAsFavoriteInput productToUnmarkAsFavoriteInput)

    List<FavoriteProductResult> getFavoriteProductsUpdatedByApi(GetFavoriteProductsInput getFavoriteProductsInput)
}
