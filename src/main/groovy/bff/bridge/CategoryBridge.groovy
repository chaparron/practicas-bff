package bff.bridge

import bff.model.Category
import bff.model.FindRootsInput

interface CategoryBridge {
    List<Category> findRootCategories(FindRootsInput accessToken)
}