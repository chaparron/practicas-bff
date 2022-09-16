package bff.configuration

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureOrder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySourceLocator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.ConfigurableEnvironment

import static com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder.defaultClient
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE

@Slf4j
@Configuration
@EnableConfigurationProperties(AwsParamStoreProperties.class)
@ConditionalOnProperty("ssm")
@AutoConfigureOrder(HIGHEST_PRECEDENCE)
class CustomAwsParamStorePropertiesConfiguration {
    def localStackProfile = "localstack"

    @Value("\${ssm}")
    private String ssmPrefix

    @Bean
    AwsParamStorePropertySourceLocator awsParamStorePropertySourceLocator(AWSSimpleSystemsManagement ssmClient) {
        new AwsParamStorePropertySourceLocator(ssmClient, awsParamStoreProperties())
    }

    @Bean
    @ConditionalOnMissingBean
    AWSSimpleSystemsManagement ssmClient(ConfigurableEnvironment environment) {
        def client
        if (!environment.activeProfiles.collect { it == localStackProfile }.isEmpty())
            client = localStackSsmClient()
        else
            client = defaultClient()
        log.info("Init ssmClient using ${client.class.simpleName}")
        client
    }

    private AwsParamStoreProperties awsParamStoreProperties() {
        def awsParamStoreProperties = new AwsParamStoreProperties()
        awsParamStoreProperties.prefix = "/$ssmPrefix"
        log.info("Init ${awsParamStoreProperties.class.simpleName} with prefix '${awsParamStoreProperties.prefix}'")
        awsParamStoreProperties
    }

    private static AWSSimpleSystemsManagement localStackSsmClient() {
        AWSSimpleSystemsManagementClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4583", "us-west-2"))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "foo"))).build()
    }
}
