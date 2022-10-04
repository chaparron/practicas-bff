package bff.resolver

import bff.JwtToken
import bff.bridge.BnplBridge
import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.ThirdPartyBridge
import bff.model.BnPlCustomerStatus
import bff.model.Country
import bff.model.Customer
import bff.model.CustomerStatus
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
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
import static bff.TestExtensions.validAccessToken
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.mockito.ArgumentMatchers.any
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
    @Mock
    private CountryBridge countryGatewayBridgeImpl

    private CustomerResolver sut
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
        when(countryBridge.getCountry(Mockito.any())).thenReturn(new Country(id: "ar",customerBranchesEnabled: false))
    }

    @Test
    void 'approved user from india without active status in bnpl provider should not have creditLine section'() {
        indianCustomer.accessToken = validAccessToken()
        def accessToken = indianCustomer.accessToken
        def userId = JwtToken.userIdFromToken(accessToken).toLong()
        when(featureFlagsSdk.isActiveForCountry(Mockito.any(), Mockito.any())).thenReturn(true)
        when(bnplBridge.customerStatus(any())).thenReturn(new BnPlCustomerStatus(userId, false))

        sut.profileSections(indianCustomer)
        assertNull(sut.profileSections(indianCustomer).find { it.id == 'CREDIT_LINES' })
    }

    @Test
    void 'CREDIT_LINES profileSection is returned whenever BNPL_FEATURE_FLAG is enabled'() {
        indianCustomer.accessToken = validAccessToken()
        def accessToken = indianCustomer.accessToken
        def userId = JwtToken.userIdFromToken(accessToken).toLong()
        when(featureFlagsSdk.isActiveForCountry(eq("BNPL_FEATURE_FLAG"), Mockito.any())).thenReturn(true)
        when(bnplBridge.customerStatus(any())).thenReturn(new BnPlCustomerStatus(userId, true))

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

}
