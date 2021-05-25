package bff.service


import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
@Slf4j
class ServiceDiscovery {

    @Value('${aws.cloudmap.namespace:qa.local}')
    String namespace

    Random RAND = new Random(System.currentTimeMillis())

    def discover(String serviceName, URI fallback) {
        try {
            def discoverInstancesRequest = new DiscoverInstancesRequest()
            discoverInstancesRequest.setNamespaceName(namespace)
            discoverInstancesRequest.setServiceName(serviceName)

            def awsServiceDiscovery = AWSServiceDiscoveryClientBuilder.defaultClient()
            def allInstances = awsServiceDiscovery.discoverInstances(discoverInstancesRequest).getInstances()

            new URI(allInstances?.get(RAND.nextInt(allInstances.size()))?.getAttributes()?.AWS_INSTANCE_CNAME)
        } catch(Exception e) {
            log.error("Error in service discovery: ", e)
            fallback
        }
    }
}
