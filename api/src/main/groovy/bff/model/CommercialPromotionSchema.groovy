package bff.model

import groovy.transform.EqualsAndHashCode
import org.springframework.context.MessageSource
import sun.util.locale.LanguageTag

import static java.text.NumberFormat.getNumberInstance
import static java.util.Locale.forLanguageTag
import static java.util.Optional.*

interface CommercialPromotionType {

    boolean appliesTo(List<ProductCart> selection)

    CommercialPromotionType labeled(Closure<String> label)

}

class CommercialPromotions {

    Optional<Discount> discount
    Optional<FreeProduct> freeProduct

    private CommercialPromotions() {}

    // An instance of this class must contain at least one promotion of a given type
    CommercialPromotions(CommercialPromotionType promotion) {
        switch (promotion) {
            case { it instanceof Discount }:
                this.discount = of(promotion as Discount)
                this.freeProduct = empty()
                break
            case { it instanceof FreeProduct }:
                this.discount = empty()
                this.freeProduct = of(promotion as FreeProduct)
                break
            default:
                this.discount = empty()
                this.freeProduct = empty()
                break
        }
    }

    boolean contains(CommercialPromotionType promotion) {
        discount.map { it == promotion }.orElse(false) ||
                freeProduct.map { it == promotion }.orElse(false)
    }
}

@Deprecated
@EqualsAndHashCode(includes = ["id"])
class CommercialPromotion {
    String id
    String description
    TimestampOutput expiration
    CommercialPromotionType type
    Closure<String> label
    Integer remainingUses

    CommercialPromotion(CommercialPromotionType promotion) {
        switch (promotion) {
            case { it instanceof Discount }:
                new CommercialPromotion(promotion as Discount)
                break
            case { it instanceof FreeProduct }:
                new CommercialPromotion(promotion as FreeProduct)
                break
            default: null
        }
    }

    CommercialPromotion(Discount discount) {
        id = discount.id
        description = discount.description
        expiration = discount.expiration
        type = discount
        label = discount.label
        remainingUses = discount.remainingUses
    }

    CommercialPromotion(FreeProduct freeProduct) {
        id = freeProduct.id
        description = freeProduct.description
        expiration = freeProduct.expiration
        type = freeProduct
        label = freeProduct.label
        remainingUses = freeProduct.remainingUses
    }

}

class CommercialPromotionLabelBuilder {

    private MessageSource messageSource

    CommercialPromotionLabelBuilder(MessageSource messageSource) {
        this.messageSource = messageSource
    }

    Closure<String> discount(List<DiscountStep> steps, List<ProductCart> selection = []) {
        { LanguageTag languageTag ->
            def locale = forLanguageTag(ofNullable(languageTag.toString()).orElse("en"))
            // when grouped discount, we have to inspect all selected products under
            // the given promotion in order to consider all step percentages;
            // otherwise, if no selection, we only consider the discount steps.
            // then, for all considered steps, we take the unique set of them, and:
            // 1) if one single percentage for all, then we label as "fixed percentage"
            // 2) if more than one percentage available, we took the max an label as "up to percentage"
            def percentages =
                    ofNullable(
                            selection.collect {
                                it.price.commercialPromotions
                                        .flatMap { it.discount }
                                        .map { it.steps }
                                        .orElse([])
                            }.flatten() as List<DiscountStep>
                    )
                            .filter { !it.empty }
                            .orElse(steps)
                            .collect { it.percentage }
                            .unique()
            messageSource.getMessage(
                    (percentages.size() == 1) ?
                            "commercialPromotion.label.FIXED_PERCENTAGE" :
                            "commercialPromotion.label.UP_TO_PERCENTAGE",
                    [getNumberInstance(locale).format(percentages.max())].toArray(),
                    locale
            )
        }
    }

    Closure<String> freeProduct() {
        { LanguageTag languageTag ->
            messageSource.getMessage(
                    "commercialPromotion.label.FREE_PRODUCT",
                    [].toArray(),
                    forLanguageTag(ofNullable(languageTag.toString()).orElse("en"))
            )
        }
    }

    Closure<String> apply(CommercialPromotionType type, List<ProductCart> selection) {
        switch (type) {
            case { type instanceof Discount }:
                discount((type as Discount).steps, selection)
                break
            case { type instanceof FreeProduct }:
                freeProduct()
                break
            default: { LanguageTag languageTag -> "" }
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

@EqualsAndHashCode(includes = ["id"])
class Discount implements CommercialPromotionType {
    String id
    String description
    TimestampOutput expiration
    Closure<String> label
    Integer remainingUses
    Boolean progressive
    List<DiscountStep> steps

    def minValue() { steps.min { it.value }.value }

    def minUnitValue() { steps.min { it.unitValue }.unitValue }

    @Override
    boolean appliesTo(List<ProductCart> selection) {
        this.appliesTo(
                selection
                        .findResults {
                            (it.price.commercialPromotions
                                    .flatMap { it.discount }
                                    .orElse(null)?.id == this.id) ? it.quantity : null
                        }
                        .sum() as Integer
        )
    }

    boolean appliesTo(Integer quantity) {
        ofNullable(
                progressive ?
                        steps.find { quantity >= it.from && quantity <= it?.to } :
                        steps.find { quantity % it.from == 0 && quantity >= it.from && quantity <= it?.to }
        ).isPresent()
    }

    Discount labeled(Closure<String> label) {
        new Discount(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                label: label,
                remainingUses: this.remainingUses,
                progressive: this.progressive,
                steps: this.steps
        )
    }

}

@EqualsAndHashCode(includes = ["id"])
class FreeProduct implements CommercialPromotionType {
    String id
    String description
    TimestampOutput expiration
    Closure<String> label
    Integer remainingUses
    Integer from
    Integer quantity
    Product product
    Display display

    @Override
    boolean appliesTo(List<ProductCart> selection) {
        this.appliesTo(
                selection
                        .findResults {
                            (it.price.commercialPromotions
                                    .flatMap { it.freeProduct }
                                    .orElse(null)?.id == this.id) ? it.quantity : null
                        }
                        .sum() as Integer
        )
    }

    boolean appliesTo(Integer quantity) {
        quantity >= from
    }

    FreeProduct labeled(Closure<String> label) {
        new FreeProduct(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                label: label,
                remainingUses: this.remainingUses,
                from: this.from,
                quantity: this.quantity,
                product: this.product,
                display: this.display
        )
    }

}