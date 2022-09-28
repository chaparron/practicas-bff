package bff.model

import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import sun.util.locale.LanguageTag

import static bff.model.ApplicationModeUtils.*
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

abstract class RewardsStep {
    Integer from
    Integer to
    Map<Integer, Integer> minQuantityByProducts

    RewardsStep(Integer from, Integer to, Map<Integer, Integer> minQuantityByProducts) {
        this.from = from
        this.to = to
        this.minQuantityByProducts = minQuantityByProducts
    }
}

class DiscountStep extends RewardsStep {

    BigDecimal value
    BigDecimal unitValue
    BigDecimal percentage
    String countryId

    DiscountStep(Integer from,
                 Integer to,
                 Map<Integer, Integer> minQuantityByProducts,
                 BigDecimal value,
                 BigDecimal unitValue,
                 BigDecimal percentage,
                 String countryId) {
        super(from, to, minQuantityByProducts)
        this.value = value
        this.unitValue = unitValue
        this.percentage = percentage
        this.countryId = countryId
    }
}

class ApplicationModeUtils {

    private static final def quantity(Closure<Integer> f) {
        { List<ProductCart> selection ->
            { RewardsStep step ->
                if (step.minQuantityByProducts.every { product, quantity ->
                    selection.find { it.product.id.toInteger() == product && it.quantity >= quantity }
                }) selection.collect { f(it) }.sum()
                else 0
            } as Closure<Integer>
        }
    }

    static final Closure<Closure<Integer>> simpleQuantifier =
            quantity { ProductCart item -> item.quantity }
    static final Closure<Closure<Integer>> quantityByUnitsQuantifier =
            quantity { ProductCart item -> item.quantity * item.price.display.units }
    static final Closure<Boolean> slabbedValidator =
            { List<RewardsStep> steps, Closure<Integer> quantifier ->
                ofNullable(
                        steps.find {
                            def quantity = quantifier(it)
                            quantity % it.from == 0 &&
                                    quantity >= it.from &&
                                    quantity <= ofNullable(it.to).orElse(Integer.MAX_VALUE)
                        }
                ).isPresent()
            }
    static final Closure<Boolean> linealOrProgressiveValidator =
            { List<RewardsStep> steps, Closure<Integer> quantifier ->
                ofNullable(
                        steps.find {
                            def quantity = quantifier(it)
                            quantity >= it.from &&
                                    quantity <= ofNullable(it.to).orElse(Integer.MAX_VALUE)
                        }
                ).isPresent()
            }
}

enum ApplicationMode {

    SLABBED(simpleQuantifier, slabbedValidator),
    SLABBED_GLOBAL(quantityByUnitsQuantifier, slabbedValidator),
    PROGRESSIVE(simpleQuantifier, linealOrProgressiveValidator),
    PROGRESSIVE_GLOBAL(quantityByUnitsQuantifier, linealOrProgressiveValidator),
    LINEAL(simpleQuantifier, linealOrProgressiveValidator),
    LINEAL_GLOBAL(quantityByUnitsQuantifier, linealOrProgressiveValidator)

    private final Closure<Closure<Integer>> quantifier
    private final Closure<Boolean> validator

    ApplicationMode(Closure<Closure<Integer>> quantifier, Closure<Boolean> validator) {
        this.quantifier = quantifier
        this.validator = validator
    }

    final Boolean appliesTo(List<RewardsStep> steps, List<ProductCart> selection) {
        return validator(steps, quantifier(selection))
    }

}

interface RewardItem {}

@EqualsAndHashCode
class FixedQuantityFreeProduct implements RewardItem {
    Long id
    String name
    String description
    List<Image> images
    Display display
    Integer quantity

    FixedQuantityFreeProduct(ProductSearch product,
                             Display display,
                             Integer quantity) {
        this.id = product.id
        this.name = product.name
        this.description = product.description
        this.images = product.images
        this.display = display
        this.quantity = quantity
    }

}

@EqualsAndHashCode
class MultipliedQuantityFreeProduct implements RewardItem {
    Long id
    String name
    String description
    List<Image> images
    Display display
    Float quantity

    MultipliedQuantityFreeProduct(ProductSearch product,
                                  Display display,
                                  Float quantity) {
        this.id = product.id
        this.name = product.name
        this.description = product.description
        this.images = product.images
        this.display = display
        this.quantity = quantity
    }

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
        this.applicationMode.appliesTo(
                this.steps,
                selection
                        .findAll {
                            (it.price.commercialPromotions
                                    .flatMap { it.discount }
                                    .orElse(null)?.id == this.id)
                        }
        )
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
class FreeProductStep extends RewardsStep {
    List<RewardsNode> rewards

    FreeProductStep(Integer from,
                    Integer to,
                    Map<Integer, Integer> minQuantityByProducts,
                    List<RewardsNode> rewards) {
        super(from, to, minQuantityByProducts)
        this.rewards = rewards
    }
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
        this.applicationMode.appliesTo(
                this.steps,
                selection
                        .findAll {
                            (it.price.commercialPromotions
                                    .flatMap { it.freeProduct }
                                    .orElse(null)?.id == this.id)
                        }
        )
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