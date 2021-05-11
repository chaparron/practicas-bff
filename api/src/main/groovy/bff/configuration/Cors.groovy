package bff.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


@Configuration
class Cors {

    @Bean
    CorsFilter corsFilter() {
        def source = new UrlBasedCorsConfigurationSource()
        def config = new CorsConfiguration()
        config.allowCredentials = true
        // TODO: use a proper list  of allowed origins
        config.allowedOrigins = ["*"]
        config.allowedHeaders = ["Origin", "Content-Type", "Accept", "x-requested-with", "Accepts", "Accept-Language", "Access-Control- Allow-Origin", "Content-Type", "Authorization"]
        config.allowedMethods = ["GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"]
        source.registerCorsConfiguration("/**", config)
        return new CorsFilter(source)
    }
}
