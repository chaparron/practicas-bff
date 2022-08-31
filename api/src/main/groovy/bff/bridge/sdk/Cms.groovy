package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.model.*
import groovy.util.logging.Slf4j
import org.springframework.context.MessageSource
import org.springframework.web.util.DefaultUriBuilderFactory
import org.springframework.web.util.UriBuilder
import scala.Option
import wabi2b.cms.sdk.*
import wabi2b.cms.sdk.Banner as CmsBanner
import wabi2b.cms.sdk.Brand as CmsBrand
import wabi2b.cms.sdk.CommercialPromotion as CmsCommercialPromotion
import wabi2b.cms.sdk.Discount as CmsDiscount
import wabi2b.cms.sdk.FreeProduct as CmsFreeProduct
import wabi2b.cms.sdk.Module as CmsModule
import wabi2b.cms.sdk.Piece as CmsPiece
import wabi2b.cms.sdk.Product as CmsProduct
import wabi2b.cms.sdk.Supplier as CmsSupplier

import static groovy.lang.Closure.IDENTITY
import static java.util.Optional.*
import static scala.jdk.javaapi.CollectionConverters.asJava
import static scala.jdk.javaapi.OptionConverters.toJava
import static wabi2b.cms.sdk.BuildModulePiecesQuery.piecesOf
import static wabi2b.cms.sdk.FindModulesQuery.homeModulesIn
import static wabi2b.cms.sdk.FindModulesQuery.listingModulesIn

@Slf4j
class Cms {

    private Sdk sdk
    private CustomerBridge customerBridge
    private String siteRoot
    private MessageSource messageSource

    List<Module> find(HomeInput input) {
        def request =
                [tagged(input.tags)]
                        .inject(
                                homeModulesIn(input.country, ofNullable(input.fallback).orElse(false)),
                                { query, builder ->
                                    builder(query) as FindHomeModulesQuery
                                }
                        )
        try {
            def response = sdk.query(request)
            new FindModulesQueryResponseMapper().map(response)
        } catch (Exception ex) {
            log.error("Error finding modules for request {}", request, ex)
            throw ex
        }
    }

    List<Module> find(ListingInput input) {
        def filteredByBrand =
                ofNullable(input.brand)
                        .map { it.toString() }
                        .map { { FindListingModulesQuery query -> query.filteredByBrand(it) } }
                        .orElse(IDENTITY)
        def filteredByCategory =
                ofNullable(input.category)
                        .map { it.toString() }
                        .map { { FindListingModulesQuery query -> query.filteredByCategory(it) } }
                        .orElse(IDENTITY)
        def filteredByTerm =
                ofNullable(input.keyword)
                        .filter { !it.trim().empty }
                        .map {
                            { FindListingModulesQuery query ->
                                query.filteredByTerm(it, Option.empty(), FullText$.MODULE$)
                            }
                        }
                        .orElse(IDENTITY)
        def filteredByPromotion =
                ofNullable(input.tag)
                        .map { { FindListingModulesQuery query -> query.filteredByPromotion(it) } }
                        .orElseGet {
                            ofNullable(input.promoted)
                                    .filter { it }
                                    .map {
                                        { FindListingModulesQuery query -> query.filteredByAnyPromotion() }
                                    }
                                    .orElse(IDENTITY)
                        }
        def filteredByFavourite =
                ofNullable(input.favourites)
                        .filter() { it }
                        .map { { FindListingModulesQuery query -> query.filteredByFavourite() } }
                        .orElse(IDENTITY)
        def request =
                [
                        tagged(input.tags),
                        filteredByBrand,
                        filteredByCategory,
                        filteredByTerm,
                        filteredByPromotion,
                        filteredByFavourite
                ]
                        .inject(
                                listingModulesIn(input.country),
                                { query, builder ->
                                    builder(query) as FindListingModulesQuery
                                }
                        )
        try {
            def response = sdk.query(request)
            new FindModulesQueryResponseMapper().map(response)
        } catch (Exception ex) {
            log.error("Error finding modules for request {}", request, ex)
            throw ex
        }
    }

    List<Piece> build(Module module, ContextInput context, Optional<Integer> maybeSize) {
        def maybeCustomer =
                ofNullable(context.accessToken)
                        .map { getCustomerAndDeliveryAddress(it) }
        def sizing =
                maybeSize
                        .map { { BuildModulePiecesQuery query -> query.sized(it) } }
                        .orElse(IDENTITY)
        def forCustomer =
                maybeCustomer
                        .map { it.first as Customer }
                        .map {
                            { BuildModulePiecesQuery query ->
                                query.forCustomer(it.id.toString(), it.customerType.code)
                            }
                        }
                        .orElse(IDENTITY)
        def inLocation =
                (ofNullable(context.coordinates)
                        .map {
                            new Tuple2(new Coordinate(it.lat.toDouble(), it.lng.toDouble()), Option.empty())
                        } | {
                    maybeCustomer
                            .map { it.second as Address }
                            .map {
                                new Tuple2(
                                        new Coordinate(it.lat, it.lon),
                                        Option.apply(it.state).map { it.id }
                                )
                            }
                })
                        .map {
                            { BuildModulePiecesQuery query ->
                                query.in(it.first as Coordinate, it.second as Option<String>)
                            }
                        }
                        .orElse(IDENTITY)
        def request =
                [sizing, forCustomer, inLocation]
                        .inject(
                                piecesOf(module.id),
                                { query, builder -> builder(query) }
                        )
        try {
            def response = sdk.query(request)
            return new BuildModulePiecesQueryResponseMapper(context).map(response)
        } catch (Exception ex) {
            log.error("Error building module pieces for request {}", request, ex)
            return []
        }
    }

    private def getCustomerAndDeliveryAddress(String accessToken) {
        def customer = customerBridge.myProfile(accessToken)
        def deliveryAddress = customer.preferredDeliveryAddress()
        new Tuple2(customer, deliveryAddress)
    }

    private static def tagged(Set<String> tags) {
        { FindModulesQuery query ->
            tags.inject(query, { builder, tag -> builder.tagged(tag) })
        }
    }

    private class FindModulesQueryResponseMapper {

        List<Module> map(scala.collection.immutable.List<CmsModule> modules) {
            asJava(modules).collect {
                new Module(
                        id: it.id(),
                        tag: it.tag(),
                        title: toJava(it.title())
                                .map { new I18N(entries: asJava(it.entries()), defaultEntry: it.defaultEntry()) },
                        titleIcon: toJava(it.titleIcon()),
                        link: link(it),
                        expiration: toJava(it.expiresIn())
                                .map { new TimestampOutput(it.toString()) }
                )
            }
        }

        Optional<String> link(CmsModule module) {
            def contentType = module.contentType()
            switch (contentType) {
                case { (it instanceof ProductShowCase) && (it as ProductShowCase).allowShowAll() }:
                    def request = (contentType as ProductShowCase).request()
                    def filteredByTerm = { UriBuilder b ->
                        request.filtering().byTerm()
                                .map { b.queryParam("keyword", it.text()) }
                                .getOrElse { b }
                    }
                    def filteredByCategory = { UriBuilder b ->
                        request.filtering().byCategory()
                                .map { b.queryParam("category", it.id()) }
                                .getOrElse { b }
                    }
                    def filteredByBrand = { UriBuilder b ->
                        request.filtering().byBrand()
                                .map { b.queryParam("brand", it.id()) }
                                .getOrElse { b }
                    }
                    def filteredByPromotion = { UriBuilder b ->
                        request.filtering().byPromotion()
                                .map {
                                    it.tag()
                                            .map { b.queryParam("tag", it) }
                                            .getOrElse { b.queryParam("promoted", true) }
                                }
                                .getOrElse { b }
                    }
                    def filteredByFavourite = { UriBuilder b ->
                        request.filtering().byFavourite()
                                .map { b.queryParam("favourites", true) }
                                .getOrElse { b }
                    }
                    def filteredByPurchased = { UriBuilder b ->
                        request.filtering().byPurchased()
                                .map { b.queryParam("purchased", true) }
                                .getOrElse { b }
                    }
                    def filteredByCollection = { UriBuilder b ->
                        request.filtering().byCollection()
                                .map { b.queryParam("collection", it.id()) }
                                .getOrElse { b }
                    }
                    def filteredByDiscount = { UriBuilder b ->
                        request.filtering().byDiscount()
                                .map { b.queryParam("discount", it.min()) }
                                .getOrElse { b }
                    }
                    def filteredByBottler = { UriBuilder b ->
                        request.filtering().byBottler()
                                .map { b.queryParam("bottler", it.id()) }
                                .getOrElse { b }
                    }
                    def filteredByManufacturer = { UriBuilder b ->
                        request.filtering().byManufacturer()
                                .map { b.queryParam("manufacturer", it.id()) }
                                .getOrElse { b }
                    }
                    def sortedByPrice = { UriBuilder b ->
                        toJava(request.sorting())
                                .filter { it instanceof ByUnitPrice }
                                .map {
                                    b
                                            .queryParam("sort", "PRICE")
                                            .queryParam("sortDirection", (it as ByUnitPrice).asc() ? "ASC" : "DESC")
                                }
                                .orElse(b)
                    }
                    def sortedAlphabetically = { UriBuilder b ->
                        toJava(request.sorting())
                                .filter { it instanceof Alphabetically }
                                .map {
                                    b
                                            .queryParam("sort", "TITLE")
                                            .queryParam("sortDirection", (it as Alphabetically).asc() ? "ASC" : "DESC")
                                }
                                .orElse(b)
                    }
                    def sortedByRecent = { UriBuilder b ->
                        toJava(request.sorting())
                                .filter { it instanceof ByLastAvailabilityUpdate$ }
                                .map { b.queryParam("sort", "RECENT") }
                                .orElse(b)
                    }
                    return of(
                            [
                                    filteredByTerm,
                                    filteredByCategory,
                                    filteredByBrand,
                                    filteredByPromotion,
                                    filteredByFavourite,
                                    filteredByPurchased,
                                    filteredByCollection,
                                    filteredByDiscount,
                                    filteredByBottler,
                                    filteredByManufacturer,
                                    sortedByPrice,
                                    sortedAlphabetically,
                                    sortedByRecent
                            ]
                                    .inject(
                                            new DefaultUriBuilderFactory(siteRoot + "/site/listing?").builder(),
                                            { UriBuilder i, c -> c(i) }
                                    )
                                    .build()
                                    .toString()
                    )
                default: empty()
            }
        }

    }

    private class BuildModulePiecesQueryResponseMapper {

        Optional<String> accessToken
        CommercialPromotionLabelBuilder labelBuilder = new CommercialPromotionLabelBuilder(messageSource)

        BuildModulePiecesQueryResponseMapper(ContextInput context) {
            accessToken = ofNullable(context.accessToken)
        }

        def map(scala.collection.immutable.List<CmsPiece> pieces) {
            asJava(pieces)
                    .collect { transform(it) }
                    .findAll { it.isPresent() }
                    .collect { it.get() }
        }

        private Optional<Piece> transform(CmsPiece piece) {
            switch (piece) {
                case { it instanceof CmsBanner }:
                    return of(banner(piece as CmsBanner))
                case { it instanceof CmsProduct }:
                    return of(
                            accessToken
                                    .map { product(piece as CmsProduct) as Piece }
                                    .orElseGet { previewProduct(piece as CmsProduct) }
                    )
                case { it instanceof CmsBrand }:
                    return of(brand(piece as CmsBrand))
                case { it instanceof CmsSupplier }:
                    return of(supplier(piece as CmsSupplier))
                default: empty()
            }
        }

        private static AdBanner banner(CmsBanner banner) {
            new AdBanner(
                    id: banner.id(),
                    name: banner.name(),
                    desktop: asJava(banner.images()).get("desktop"),
                    mobile: asJava(banner.images()).get("mobile"),
                    link: toJava(banner.link())
            )
        }

        private ProductSearch product(CmsProduct product) {
            def country = product.manufacturer().country()
            def displays = asJava(product.options()).collect { display(it) }.toSet().toList()
            def prices = asJava(product.options()).collect { price(it, country) }
            new ProductSearch(
                    product.id().toLong(),
                    product.name().defaultEntry(),
                    displays.sort { it.units }?.getAt(0)?.ean,
                    toJava(product.description()).map { it.defaultEntry() }.orElse(null),
                    country,
                    new Category(
                            id: product.categorization().last().id().toLong(),
                            parentId: toJava(product.categorization().last().parent())
                                    .map { it.toLong() }.orElse(null),
                            name: product.categorization().last().name().defaultEntry(),
                            enabled: true,
                            isLeaf: true
                    ),
                    brand(product.brand()),
                    asJava(product.images()).collect { new Image(id: it) },
                    displays,
                    prices,
                    true,
                    toJava(product.favourite()).orElse(false) as boolean,
                    accessToken.orElse(null)
            )
        }

        private PreviewProductSearch previewProduct(CmsProduct product) {
            new PreviewProductSearch(this.product(product))
        }

        private static Brand brand(CmsBrand brand) {
            new Brand(
                    id: brand.id().toLong(),
                    name: brand.name().defaultEntry(),
                    logo: toJava(brand.logo()).orElse(null)
            )
        }

        private static PreviewSupplier supplier(CmsSupplier supplier) {
            new PreviewSupplier(
                    id: supplier.id().toLong(),
                    name: supplier.name(),
                    avatar: toJava(supplier.avatar()).orElse(null)
            )
        }

        private Price price(AvailableOption option, String countryId) {
            new Price(
                    id: option.id() as Integer,
                    supplier: supplier(option),
                    value: option.price().toBigDecimal(),
                    unitValue: option.price() / option.display().units(),
                    minUnits: option.requiredPurchaseUnits()._1() as Integer,
                    maxUnits: toJava(option.requiredPurchaseUnits()._2()).map { it as Integer }.orElse(0),
                    display: display(option),
                    configuration: new SupplierProductConfiguration(
                            disableMinAmountCount: option.minPurchaseAmountCountDisabled()
                    ),
                    commercialPromotions: toJava(option.commercialPromotion())
                            .flatMap { commercialPromotions(option, it, countryId) },
                    accessToken: this.accessToken.orElse(null),
                    countryId: countryId
            )
        }

        protected Optional<CommercialPromotions> commercialPromotions(AvailableOption option,
                                                                      CmsCommercialPromotion promotion,
                                                                      String countryId) {
            (of(
                    asJava(promotion.steps()).findResults { step ->
                        toJava(step.rewards().headOption())
                                .flatMap { toJava(it.items().headOption()) }
                                .filter { it instanceof CmsDiscount }
                                .map {
                                    def discount = (it as CmsDiscount)
                                    new DiscountStep(
                                            from: step.from(),
                                            to: toJava(step.to()).orElse(null),
                                            value: option.price().toBigDecimal() - discount.amount().toBigDecimal(),
                                            unitValue: option.price() / option.display().units() - discount.amount() / option.display().units(),
                                            percentage: discount.percentage().toBigDecimal(),
                                            countryId: countryId,
                                            minQuantityByProducts:
                                                    asJava(step.minQuantityByProducts()).collectEntries {
                                                        [it.key.toInteger(), it.value as Integer]
                                                    }
                                    )
                                }
                                .orElse(null)
                    }.toList()
            )
                    .filter { !it.empty }
                    .map { steps ->
                        new Discount(
                                id: promotion.id(),
                                description: promotion.description(),
                                expiration: new TimestampOutput(promotion.expiration().toString()),
                                label: labelBuilder.discount(steps),
                                remainingUses: promotion.remainingUses(),
                                applicationMode: ApplicationMode.valueOf(promotion.applicationMode()),
                                steps: steps,
                                linkedProducts: asJava(promotion.linkedProducts()).collect { it.toInteger() }
                        )
                    } | {
                of(
                        asJava(promotion.steps()).collect { step ->
                            new FreeProductStep(
                                    from: step.from(),
                                    to: toJava(step.to()).orElse(null),
                                    rewards: asJava(step.rewards()).findResults { node ->
                                        new RewardsNode(
                                                id: node.id(),
                                                parent: toJava(node.parent()),
                                                type: (node.nodeType() instanceof AndOperator) ?
                                                        RewardsNodeType.AND :
                                                        RewardsNodeType.OR,
                                                items: asJava(node.items()).collect { reward ->
                                                    if (reward instanceof CmsFreeProduct) {
                                                        def freeProduct = reward as CmsFreeProduct
                                                        def quantity = freeProduct.quantity()
                                                        def product = product(freeProduct.product())
                                                        def display = new Display(
                                                                id: freeProduct.display().id().toInteger(),
                                                                ean: freeProduct.display().ean(),
                                                                units: freeProduct.display().units()
                                                        )
                                                        if (quantity instanceof FixedQuantity) {
                                                            new FixedQuantityFreeProduct(
                                                                    product,
                                                                    display,
                                                                    (quantity as FixedQuantity).amount()
                                                            )
                                                        } else {
                                                            new MultipliedQuantityFreeProduct(
                                                                    product,
                                                                    display,
                                                                    (quantity as MultipliedQuantity).factor().toFloat()
                                                            )
                                                        }
                                                    } else null
                                                }
                                        )
                                    },
                                    minQuantityByProducts:
                                            asJava(step.minQuantityByProducts()).collectEntries {
                                                [it.key.toInteger(), it.value as Integer]
                                            }
                            )
                        }
                )
                        .filter { !it.empty }
                        .map { steps ->
                            new FreeProduct(
                                    id: promotion.id(),
                                    description: promotion.description(),
                                    expiration: new TimestampOutput(promotion.expiration().toString()),
                                    label: labelBuilder.freeProduct(),
                                    remainingUses: promotion.remainingUses(),
                                    applicationMode: ApplicationMode.valueOf(promotion.applicationMode()),
                                    steps: steps,
                                    linkedProducts: asJava(promotion.linkedProducts()).collect { it.toInteger() }
                            )
                        }
            })
                    .map { new CommercialPromotions(it) }
        }

        private Supplier supplier(AvailableOption option) {
            new Supplier(
                    id: option.supplier().id().toInteger(),
                    name: option.supplier().name(),
                    legalName: null,
                    avatar: toJava(option.supplier().avatar()).orElse(null),
                    deliveryZones: toJava(option.supplier().deliveryZones())
                            .map { asJava(it.toList()) }
                            .orElse([])
                            .collect {
                                new DeliveryZone(
                                        accessToken: this.accessToken.orElse(null),
                                        id: it.id().toInteger(),
                                        minAmount: it.requiredPurchaseAmount()._1().toBigDecimal(),
                                        maxAmount: toJava(it.requiredPurchaseAmount()._2())
                                                .map { it.toBigDecimal() }
                                                .orElse(null),
                                        deliveryCost: toJava(it.cost())
                                                .map { it.toBigDecimal() }
                                                .orElse(null)
                                )
                            },
                    rating: toJava(option.supplier().rating()).map {
                        new RatingScore(
                                count: it.count().toInteger(),
                                average: it.average().toDouble(),
                                percentage: it.percentage().toDouble()
                        )
                    }.orElse(null),
                    averageDeliveryDay: toJava(option.supplier().averageDeliveryTime())
                            .map { it.toDays().toString() },
                    accessToken: accessToken.orElse(null)
            )
        }

        private static display(AvailableOption option) {
            new Display(
                    id: option.display().id().toInteger(),
                    ean: option.display().ean(),
                    units: option.display().units()
            )
        }

    }

}
