package bff.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

@Configuration
public class LocaleConfiguration {
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource()
        source.setBasenames("lang/messages")
        source.setUseCodeAsDefaultMessage(true)
        source
    }
}

