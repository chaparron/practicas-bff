package bff.bridge

import bff.model.CoordinatesInput
import bff.model.PreviewHomeSupplierResponse

interface SupplierBridge {

    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput coordinatesInput)


}
