package bff.resolver

import bff.bridge.BnplBridge
import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.ThirdPartyBridge
import bff.model.CreditLines
import bff.model.Customer
import bff.model.CustomerStatus
import bff.model.Money
import bff.model.SuperMoneyCreditLine
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import com.google.common.collect.Lists
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import wabi.sdk.impl.CustomSdkException
import wabi2b.sdk.featureflags.FeatureFlagsSdk

import static bff.TestExtensions.anyCustomerWithIdAndAccessToken
import static java.math.BigDecimal.TEN
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.mockito.ArgumentMatchers.eq
import static org.mockito.Mockito.when

class CustomerResolverTest implements GraphQLResolver<Customer> {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private CustomerBridge customerBridge
    @Mock
    private CountryBridge countryBridge
    @Mock
    private ThirdPartyBridge thirdPartyBridge
    @Mock
    private FeatureFlagsSdk featureFlagsSdk
    @Mock
    private BnplProvidersService bnplProvidersService
    @Mock
    private BnplBridge bnplBridge
    private def sut
    def indianCustomer = anyCustomerWithIdAndAccessToken("in")

    @Before
    void setup() {
        sut = new CustomerResolver(
                customerBridge: customerBridge,
                countryBridge: countryBridge,
                thirdPartyBridge: thirdPartyBridge,
                featureFlagsSdk: featureFlagsSdk,
                bnplProvidersService: bnplProvidersService,
                bnplBridge: bnplBridge
        )
        when(featureFlagsSdk.isActiveForCountry(Mockito.any(), Mockito.any())).thenReturn(false)
    }

    @Test
    void 'approved user from india without bnpl provider shouldnt have creditLine section'() {
        when(featureFlagsSdk.isActiveForCountry(Mockito.any(), Mockito.any())).thenReturn(true)
        when(bnplBridge.userBalance(indianCustomer.accessToken)).thenReturn(new CreditLines(
                credits: Lists.newArrayList(
                        new SuperMoneyCreditLine(
                                approvedLimit: new Money("INR", TEN + TEN),
                                toRepay: new Money("INR", TEN),
                                remaining: new Money("INR", TEN)
                        )
                )
        ))
        when(bnplProvidersService.currentUserHasBnplWallet(indianCustomer.accessToken)).thenReturn(false)
        sut.profileSections(indianCustomer)
        assertNull(sut.profileSections(indianCustomer).find { it.id == 'CREDIT_LINES' })
    }

    @Test
    void 'CREDIT_LINES profileSection is returned whenever BNPL_FEATURE_FLAG is enabled'() {
        when(featureFlagsSdk.isActiveForCountry(eq("BNPL_FEATURE_FLAG"), Mockito.any())).thenReturn(true)
        when(bnplProvidersService.currentUserHasBnplWallet(indianCustomer.accessToken)).thenReturn(true)
        when(bnplBridge.userBalance(indianCustomer.accessToken)).thenReturn(new CreditLines(
                credits: Lists.newArrayList(
                        new SuperMoneyCreditLine(
                                approvedLimit: new Money("INR", TEN + TEN),
                                toRepay: new Money("INR", TEN),
                                remaining: new Money("INR", TEN)
                        )
                )
        ))

        assertNotNull(sut.profileSections(indianCustomer).find { it.id == 'CREDIT_LINES' })
    }

    @Test
    void 'CREDIT_LINES profileSection is not returned whenever BNPL_FEATURE_FLAG is disabled'() {
        when(featureFlagsSdk.isActiveForCountry(eq("BNPL_FEATURE_FLAG"), Mockito.any())).thenReturn(false)
        assertNull(sut.profileSections(indianCustomer).find { it.id == 'CREDIT_LINES' })
    }

    @Test
    void 'approved user not from india shouldnt have creditLine section'() {
        def notIndianCustomer = anyCustomerWithIdAndAccessToken("ar", CustomerStatus.APPROVED)
        assertNull(sut.profileSections(notIndianCustomer).find { it.id == 'CREDIT_LINES' })
    }

    @Test
    void 'approved user from india without active status in bnpl provider shouldnt have creditLine section'() {
        when(featureFlagsSdk.isActiveForCountry(Mockito.any(), Mockito.any())).thenReturn(true)
        when(bnplBridge.userBalance(indianCustomer.accessToken)).thenThrow(CustomSdkException)
        sut.profileSections(indianCustomer)
        assertNull(sut.profileSections(indianCustomer).find { it.id == 'CREDIT_LINES' })
    }
}
