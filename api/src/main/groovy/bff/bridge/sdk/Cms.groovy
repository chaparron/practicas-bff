package bff.bridge.sdk

import bff.bridge.CustomerBridge
import bff.model.*
import scala.Option
import wabi2b.cms.sdk.*
import wabi2b.cms.sdk.Banner as CmsBanner
import wabi2b.cms.sdk.Brand as CmsBrand
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

class Cms {

    private Sdk sdk
    private CustomerBridge customerBridge

    List<Module> find(HomeInput input) {
        def request =
                [tagged(input.tags)]
                        .inject(
                                homeModulesIn(input.country, false),
                                { query, builder ->
                                    builder(query) as FindHomeModulesQuery
                                }
                        )
        def response = sdk.query(request)
        new FindModulesQueryResponseMapper().map(response)
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
        def response = sdk.query(request)
        new FindModulesQueryResponseMapper().map(response)
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
                (ofNullable(context.coordinates) | {
                    maybeCustomer
                            .map { it.second as Address }
                            .map { new CoordinatesInput(lat: it.lat, lng: it.lon) }
                })
                        .map {
                            { BuildModulePiecesQuery query -> query.in(it.lat.toDouble(), it.lng.toDouble()) }
                        }
                        .orElse(IDENTITY)
        def request =
                [sizing, forCustomer, inLocation]
                        .inject(
                                piecesOf(module.id),
                                { query, builder -> builder(query) }
                        )
        def response = sdk.query(request)
        return new BuildModulePiecesQueryResponseMapper(context).map(response)
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

}

class FindModulesQueryResponseMapper {

    static List<Module> map(scala.collection.immutable.List<CmsModule> modules) {
        asJava(modules).collect {
            new Module(
                    id: it.id(),
                    tag: it.tag(),
                    title: toJava(it.title()).map { it.defaultEntry() }.orElse(null)
            )
        }
    }
}

class BuildModulePiecesQueryResponseMapper {

    Optional<String> accessToken

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
                desktop: asJava(banner.images()).get("desktop", null),
                mobile: asJava(banner.images()).get("mobile", null),
                link: toJava(banner.link()).orElse(null)
        )
    }

    private ProductSearch product(CmsProduct product) {
        def prices = asJava(product.options()).collect { price(it) }
        def displays = asJava(product.options()).collect { display(it) }.toSet().toList()
        new ProductSearch(
                id: product.id().toLong(),
                name: product.name().defaultEntry(),
                category: new Category(
                        id: product.categorization().last().id().toLong(),
                        parentId: toJava(product.categorization().last().parent())
                                .map { it.toLong() }.orElse(null),
                        name: product.categorization().last().name().defaultEntry(),
                        enabled: true,
                        isLeaf: true
                ),
                brand: brand(product.brand()),
                ean: displays.sort { it.units }?.getAt(0)?.ean,
                description: toJava(product.description()).map { it.defaultEntry() }.orElse(null),
                images: asJava(product.images()).collect { new Image(id: it) },
                displays: displays,
                prices: prices,
                minUnitsPrice: prices.min { Price a, Price b ->
                    (a.minUnits == b.minUnits) ? a.unitValue <=> b.unitValue : a.minUnits <=> b.minUnits
                },
                highlightedPrice: prices.min { it.unitValue },
                priceFrom: prices.min { it.value },
                title: product.name().defaultEntry(),
                country_id: product.manufacturer().country(),
                favorite: toJava(product.favourite()).orElse(false),
                accessToken: accessToken.orElse(null)
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

    private Price price(AvailableOption option) {
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
                accessToken: accessToken.orElse(null)
        )
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
                                    maxAmount: toJava(it.requiredPurchaseAmount()._2()
                                            .map { it.toBigDecimal() })
                                            .orElse(null),
                                    deliveryCost: toJava(it.cost()
                                            .map { it.toBigDecimal() })
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