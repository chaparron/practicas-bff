package bff.service

import graphql.schema.DataFetchingEnvironment
import graphql.servlet.GraphQLContext
import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletRequest

@Slf4j
class DeviceIdentifierService {


    /**
     * Identify client IP where the request was first made
     */
    static String identifySource(DataFetchingEnvironment env) {
        GraphQLContext ctx = env.getContext()
        HttpServletRequest request = ctx.getHttpServletRequest().get()

        String submittedIp = request.getHeader("X-Forwarded-For")
        def candidateIPs = submittedIp?:request.getRemoteAddr()
        def singleIp = getLeftMostAddress(candidateIPs)
        if (singleIp  == "127.0.0.1") { // Useful when bff is running locally
            log.info("Could not correctly detect the remote address, returning a random IP")
            def random = new Random()
            return (0..3).collect { random.nextInt(255) }.join('.')
        }
        return singleIp
    }

    /**
     * Just get left-most address
     * Note: The X-Forwarded-For request header may contain multiple IP addresses that are comma separated.
     * The left-most address is the client IP where the request was first made. This is followed by any subsequent proxy identifiers, in a chain.
     */
    private static String getLeftMostAddress(String source) {
        def hasManyIps = source.indexOf(",") != -1
        if (hasManyIps) {
            source.split(",").collect { it.trim() }.first()
        }
        return source
    }

}
