package bff.filter


import groovy.util.logging.Slf4j
import org.apache.catalina.filters.FilterBase
import org.apache.juli.logging.Log
import org.apache.juli.logging.LogFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// https://spring.io/blog/2015/06/08/cors-support-in-spring-framework

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class SiteCrossFilter extends FilterBase {

    @Value('${allowed.origins:*}')
    private String allowedOrigins

    @Override
    void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req
        HttpServletResponse response = (HttpServletResponse) resp

        if ('*' == allowedOrigins) {
            response.addHeader('Access-Control-Allow-Origin', allowedOrigins)
        } else if (allowedOrigins) {
            def origins = allowedOrigins.split ','
            String originDomain = fromDomain request
            origins.each {
                if (originDomain.contains(it)) {
                    response.addHeader 'Access-Control-Allow-Origin', originDomain
                }
            }
        }

        response.addHeader 'Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS'
        response.addHeader 'Access-Control-Allow-Headers', 'Origin,x-requested-with,Accepts,Accept-Language,Access-Control-Allow-Origin,Content-Type,Authorization'
        response.addHeader 'Access-Control-Max-Age', '3600'
        if (request.method != 'OPTIONS') {
            chain?.doFilter req, resp
        }
    }

    @Override
    protected Log getLogger() {
        LogFactory.getLog SiteCrossFilter
    }

    private static String fromDomain(ServletRequest request) {
        Enumeration<String> headers = ((HttpServletRequest) request).getHeaders 'Origin'
        if (headers.hasMoreElements()) {
            return headers.nextElement().toString()
        }
        String protocol = request.scheme
        String domain = request.serverName
        return "${protocol}://${domain}${port(request.serverPort)}"
    }

    private static String port(int port) {
        port == 80 ? '' : ":${port}"
    }
}
