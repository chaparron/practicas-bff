package bff.bridge

import bff.model.FrequentProductResult
import bff.model.GetFrequentProductsInput

interface RecommendedOrderBridge {

    List<FrequentProductResult> getFrequentProducts(GetFrequentProductsInput getFrequentProductsInput)

}
