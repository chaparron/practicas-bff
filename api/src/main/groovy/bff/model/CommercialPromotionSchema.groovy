package bff.model

import groovy.transform.EqualsAndHashCode
import org.springframework.context.MessageSource
import sun.util.locale.LanguageTag

import static java.util.Locale.forLanguageTag
import static java.util.Optional.ofNullable

interface CommercialPromotionType {
    boolean satisfy(List<ProductCart> selection)
}

@EqualsAndHashCode(includes = ["id"])
class CommercialPromotion {
    String id
    String description
    TimestampOutput expiration
    CommercialPromotionType type
    Closure<String> label

    boolean satisfy(List<ProductCart> selection) {
        type.satisfy(selection)
    }

    CommercialPromotion labeled(Closure<String> label) {
        new CommercialPromotion(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                type: this.type,
                label: label
        )
    }

}

class CommercialPromotionLabel {

    private MessageSource messageSource

    CommercialPromotionLabel(MessageSource messageSource) {
        this.messageSource = messageSource
    }

    Closure<String> build(CommercialPromotionType type) {
        build(type, [])
    }

    Closure<String> build(CommercialPromotionType type, List<ProductCart> selection) {
        { LanguageTag languageTag ->
            def locale = forLanguageTag(ofNullable(languageTag.toString()).orElse("en"))
            switch (type) {
                case { type instanceof Discount }:
                    def percentages =
                            ofNullable(
                                    selection.collect {
                                        (it.price.commercialPromotion.type as Discount).steps
                                    }.flatten() as List<DiscountStep>
                            )
                                    .filter { !it.empty }
                                    .orElse((type as Discount).steps)
                                    .collect { it.percentage }
                                    .unique()
                    messageSource.getMessage(
                            (percentages.size() == 1) ?
                                    "commercialPromotion.label.FIXED_PERCENTAGE" :
                                    "commercialPromotion.label.UP_TO_PERCENTAGE",
                            [percentages.max()].toArray(),
                            locale
                    )
                    break
                case { type instanceof FreeProduct }:
                    messageSource.getMessage("commercialPromotion.label.FREE_PRODUCT", [].toArray(), locale)
                    break
                default: ""
            }
        }
    }

}

class DiscountStep {
    Integer from
    Integer to
    BigDecimal value
    BigDecimal unitValue
    BigDecimal percentage
    String countryId
}

class Discount implements CommercialPromotionType {
    Boolean progressive
    List<DiscountStep> steps

    def minValue() { steps.min { it.value }.value }

    def minUnitValue() { steps.min { it.unitValue }.unitValue }

    @Override
    boolean satisfy(List<ProductCart> selection) {
        def quantity = selection.collect { it.quantity }.sum()
        ofNullable(
                progressive ?
                        steps.find { quantity >= it.from && quantity <= it?.to } :
                        steps.find { quantity % it.from == 0 && quantity >= it.from && quantity <= it?.to }
        ).isPresent()
    }

}

class FreeProduct implements CommercialPromotionType {
    Integer from
    Product product
    Display display

    @Override
    boolean satisfy(List<ProductCart> selection) {
        selection.collect { it.quantity }.sum() >= from
    }

}