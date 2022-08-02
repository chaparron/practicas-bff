package bff.model

import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j
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
    ApplicationMode applicationMode
    Set<Integer> linkedProducts

    CommercialPromotion(Discount discount) {
        id = discount.id
        description = discount.description
        expiration = discount.expiration
        type = discount
        label = discount.label
        remainingUses = discount.remainingUses
        applicationMode = discount.applicationMode
        linkedProducts = discount.linkedProducts
    }

    CommercialPromotion(FreeProduct freeProduct) {
        id = freeProduct.id
        description = freeProduct.description
        expiration = freeProduct.expiration
        type = freeProduct
        label = freeProduct.label
        remainingUses = freeProduct.remainingUses
        applicationMode = freeProduct.applicationMode
        linkedProducts = freeProduct.linkedProducts
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

class MinProductQuantityByProduct {
    Integer product
    Integer quantity
}

class DiscountStep {
    Integer from
    Integer to
    BigDecimal value
    BigDecimal unitValue
    BigDecimal percentage
    String countryId
    Map<Integer, Integer> minQuantityByProducts
}

enum ApplicationMode {
    SLABBED,
    PROGRESSIVE,
    NON_PROGRESSIVE
}

interface RewardItem {}

@EqualsAndHashCode
class FixedQuantityFreeProduct implements RewardItem {
    Product product
    Display display
    Integer quantity
}

@EqualsAndHashCode
class ProportionalQuantityFreeProduct implements RewardItem {
    Product product
    Display display
    Integer quantity
}

@Slf4j
@EqualsAndHashCode(includes = ["id"])
class Discount implements CommercialPromotionType {
    String id
    String description
    TimestampOutput expiration
    Closure<String> label
    Integer remainingUses
    ApplicationMode applicationMode
    List<DiscountStep> steps
    Set<Integer> linkedProducts

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
        ofNullable(appliesToViaApplicationMode(quantity)).isPresent()
    }

    // TODO : Check this implementation, its a hotfix due missing parameter progressive
    // see #https://wabiproject.atlassian.net/browse/WO-2590
    private DiscountStep appliesToViaApplicationMode(int quantity) {
        // PROGRESSIVE -> todos los step por los que pase
        // LINEAL -> un solo step
        // SLAVE -> multiplos de step
        log.info("La promo :: " + id + " :: " + description + " :: " + applicationMode + " es aplicable para la quantity " + quantity + "?")
        if (applicationMode == ApplicationMode.NON_PROGRESSIVE) {
            final step = steps.find { quantity % it.from == 0 && quantity >= it.from && quantity <= it?.to }
            log.info("Applicable step :: " + step)
            return step
        }

        if (applicationMode == ApplicationMode.PROGRESSIVE) {
            final step = steps.find { quantity >= it.from && quantity <= it?.to }
            log.info("Applicable step :: " + step)
            return step
        }

        log.info("No es aplicable !!!")
        return null
    }

    Discount labeled(Closure<String> label) {
        new Discount(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                label: label,
                remainingUses: this.remainingUses,
                applicationMode: this.applicationMode,
                steps: this.steps
        )
    }

}

enum RewardsNodeType {
    AND,
    OR
}

@EqualsAndHashCode
class RewardsNode {
    String id
    Optional<String> parent
    RewardsNodeType type
    List<RewardItem> items
}

@EqualsAndHashCode
class FreeProductStep {
    Integer from
    Integer to
    List<RewardsNode> rewards
    Map<Integer, Integer> minQuantityByProducts
}

@EqualsAndHashCode(includes = ["id"])
class FreeProduct implements CommercialPromotionType {
    String id
    String description
    TimestampOutput expiration
    Closure<String> label
    Integer remainingUses
    ApplicationMode applicationMode
    List<FreeProductStep> steps
    Set<Integer> linkedProducts

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
        quantity >= steps.min { it.from }.from &&
                quantity < of(steps.max { it.to }.to).orElse(Integer.MAX_VALUE)
    }

    FreeProduct labeled(Closure<String> label) {
        new FreeProduct(
                id: this.id,
                description: this.description,
                expiration: this.expiration,
                label: label,
                remainingUses: this.remainingUses,
                applicationMode: this.applicationMode,
                steps: this.steps,
                linkedProducts: this.linkedProducts
        )
    }

}