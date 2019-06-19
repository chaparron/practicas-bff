package bff.bridge

import bff.model.Category

interface CategoryBridge {
    List<Category> findRootCategories(String accessToken)
}