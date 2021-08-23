package bff.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

import java.nio.charset.StandardCharsets

@Configuration
public class LocaleConfiguration {
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource()
        source.setBasenames("lang/messages")
        source.setUseCodeAsDefaultMessage(true)
        source.setDefaultEncoding(StandardCharsets.UTF_8.name())
        source
    }
}

