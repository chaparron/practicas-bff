package bff.configuration

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import groovy.util.logging.Slf4j
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Slf4j
class SqsPublisherConfiguration {

    @Bean
    static AmazonSQS amazonSQS() {
        AmazonSQSClientBuilder.defaultClient()
    }
}
