package bff.resolver


import bff.model.FreeProduct
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class FreeProductResolver implements GraphQLResolver<FreeProduct> {

    String label(FreeProduct freeProduct, LanguageTag languageTag) {
        freeProduct.label.call(languageTag)
    }

}
