package bff.resolver


import bff.model.CmsCallToAction
import bff.model.CmsPromo
import bff.model.CmsPromoImageSize
import bff.service.ImageService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sun.util.locale.LanguageTag

@Component
class CmsPromoResolver implements GraphQLResolver<CmsPromo> {

    @Autowired
    ImageService imageService

    String desktop(CmsPromo promo, CmsPromoImageSize size) {
        imageService.url(promo.desktop, size)
    }

    String mobile(CmsPromo promo, CmsPromoImageSize size) {
        imageService.url(promo.mobile, size)
    }

    String title(CmsPromo promo, LanguageTag languageTag) {
        promo.title.map { it.getOrDefault(languageTag) }.orElse(null)
    }

    String epigraph(CmsPromo promo, LanguageTag languageTag) {
        promo.epigraph.map { it.getOrDefault(languageTag) }.orElse(null)
    }

    String label(CmsPromo promo, LanguageTag languageTag) {
        promo.label.map { it.getOrDefault(languageTag) }.orElse(null)
    }

    CmsCallToAction callToAction(CmsPromo promo) {
        promo.callToAction.orElse(null)
    }

}
