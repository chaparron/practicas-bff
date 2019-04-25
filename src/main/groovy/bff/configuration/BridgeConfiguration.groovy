package bff.configuration

import bff.bridge.*
import bff.bridge.http.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestOperations

@Configuration
class BridgeConfiguration {

    @Autowired
    RestOperations http

    @Value('${api.root}')
    URI root

    @Bean
    AuthServerBridge authServerBridge() {
        new AuthServerBridgeImpl(
                http: http,
                root: root
        )
    }
}