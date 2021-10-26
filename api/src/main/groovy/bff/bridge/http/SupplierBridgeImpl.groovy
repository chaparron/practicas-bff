package bff.bridge.http

import bff.bridge.SupplierBridge
import bff.service.HttpBridge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.util.UriComponentsBuilder

class SupplierBridgeImpl implements SupplierBridge {

    URI root

    @Autowired
    HttpBridge httpBridge

    @Override
    String getAverageDeliveryDays(String accessToken, Long supplierId) {
        URI uri = UriComponentsBuilder.fromUri(root.resolve("/supplier/${supplierId}/average-delivery")).toUriString().toURI()

        httpBridge.get(uri, "Bearer $accessToken" , null, String.class)
    }
}
