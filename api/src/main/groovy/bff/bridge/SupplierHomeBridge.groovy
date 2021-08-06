package bff.bridge

import bff.model.CoordinatesInput
import bff.model.PreviewHomeSupplierResponse

interface SupplierHomeBridge {

    PreviewHomeSupplierResponse previewHomeSuppliers(CoordinatesInput coordinatesInput)

}
