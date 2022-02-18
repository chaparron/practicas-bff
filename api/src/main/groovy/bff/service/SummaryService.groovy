package bff.service

import bff.model.CartSummaryItemType
import bff.model.Summary

class SummaryService {

    static List<Summary> sortAndGetVisibleForMe(List<Summary> summary, String country) {
        summary.removeIf({ country == 'mx' && it.type == CartSummaryItemType.WABIMONEY_USED })
        return summary.findAll { it.type.visibleToSummary }.sort(false) { it.type.position }
    }
}
