package bff.bridge

import bff.model.Category
import bff.model.CoordinatesInput
import bff.model.RootCategoriesResult

interface CategoryBridge {

    List<Category> findRootCategories(String accessToken)

    RootCategoriesResult previewRootCategories(CoordinatesInput coordinatesInput)
}