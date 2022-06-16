package bff.model

import groovy.transform.EqualsAndHashCode
import org.springframework.context.MessageSource
import sun.util.locale.LanguageTag

import static java.text.NumberFormat.getNumberInstance
import static java.util.Locale.forLanguageTag
import static java.util.Optional.ofNullable

interface CommercialPromotionType {
    boolean appliesTo(Integer quantity)
}

@EqualsAndHashCode(includes = ["id"])
class CommercialPromotion {
    String id
    String description
    TimestampOutput expiration
    CommercialPromotionType type
    Closure<String> label
    Integer remainingUses

    boolean appliesTo(List<ProductCart> selection) {
        type.appliesTo(
                selection
                        .findResults { it.price.commercialPromotion == this ? it.quantity : null }
                        .sum() as Integer
        )
    }

    CommercialPromotion labeled(Closure<String> label) {
        new CommercialPromotion(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                type: this.type,
                label: label,
                remainingUses: this.remainingUses
        )
    }

}

class CommercialPromotionLabelBuilder {

    private MessageSource messageSource

    CommercialPromotionLabelBuilder(MessageSource messageSource) {
        this.messageSource = messageSource
    }

    Closure<String> apply(CommercialPromotionType type) {
        apply(type, [])
    }

    Closure<String> apply(CommercialPromotionType type, List<ProductCart> selection) {
        { LanguageTag languageTag ->
            def locale = forLanguageTag(ofNullable(languageTag.toString()).orElse("en"))
            switch (type) {
                case { type instanceof Discount }:
                    // when grouped discount, we have to inspect all selected products under
                    // the given promotion in order to consider all step percentages;
                    // otherwise, if no selection, we only consider the discount steps.
                    // then, for all considered steps, we take the unique set of them, and:
                    // 1) if one single percentage for all, then we label as "fixed percentage"
                    // 2) if more than one percentage available, we took the max an label as "up to percentage"
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
                            [getNumberInstance(locale).format(percentages.max())].toArray(),
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
    boolean appliesTo(Integer quantity) {
        ofNullable(
                progressive ?
                        steps.find { quantity >= it.from && quantity <= it?.to } :
                        steps.find { quantity % it.from == 0 && quantity >= it.from && quantity <= it?.to }
        ).isPresent()
    }

}

class FreeProduct implements CommercialPromotionType {
    Integer from
    Integer quantity
    Product product
    Display display

    @Override
    boolean appliesTo(Integer quantity) {
        quantity >= from
    }

}