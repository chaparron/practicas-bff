package bff.resolver


import bff.model.FeatureSlice
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
@Slf4j
class FeatureSliceResolver implements GraphQLResolver<FeatureSlice> {

    String label(FeatureSlice feature, LanguageTag languageTag) {
        feature.label.call(languageTag)
    }

}