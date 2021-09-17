package bff.service

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest

@Slf4j
class DeviceIdentifierService {


    /**
     * Device fingerprint maybe?
     */
    static String identifySource(DataFetchingEnvironment env) {
        GraphQLContext ctx = env.getContext()
        HttpServletRequest request = ctx.getHttpServletRequest().get()

        String submittedIp = request.getHeader("X-Forwarded-For")
        def candidateIP = submittedIp?:request.getRemoteAddr()
        if (candidateIP  == "127.0.0.1") {
            log.info("Could not correctly detect the remote address, returning a random IP")
            def random = new Random()
            return (0..3).collect { random.nextInt(255) }.join('.')
        }
        return candidateIP
    }

}
