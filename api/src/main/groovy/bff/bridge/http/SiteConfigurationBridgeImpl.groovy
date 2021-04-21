package bff.bridge.http

import bff.bridge.SiteConfigurationBridge
import bff.model.BannerDialog
import bff.model.BannerDialogResult
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestOperations
import org.springframework.web.util.UriComponentsBuilder

class SiteConfigurationBridgeImpl implements SiteConfigurationBridge {

    URI root
    RestOperations http

    @Override
    BannerDialogResult getBannerDialog(String accessToken) {
        def uri = UriComponentsBuilder.fromUri(root.resolve("/site_configuration/me/banner"))

        def request = RequestEntity.method(HttpMethod.GET, uri.toUriString().toURI())
                .contentType(MediaType.APPLICATION_JSON)

        request.header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        http.exchange(request.build(), BannerDialog).body
    }
}
