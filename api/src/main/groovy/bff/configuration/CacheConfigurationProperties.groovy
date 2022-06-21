package bff.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("app.cache")
class CacheConfigurationProperties {

    Long categories
    Long regionalConfig
    Long favorites
    Long brands;
    Long suppliers;
    Long promotions;
}
