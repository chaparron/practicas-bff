package bff.resolver

import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.ThirdPartyBridge
import bff.model.Customer
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import wabi2b.sdk.featureflags.FeatureFlagsSdk

import static bff.TestExtensions.anyCustomerWithIdAndAccessToken
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.mockito.Mockito.when

@RunWith(MockitoJUnitRunner)
class CustomerResolverTest implements GraphQLResolver<Customer> {
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
    private def sut
    def indianCustomer = anyCustomerWithIdAndAccessToken("in")

    @Before
    void setup(){
        sut = new CustomerResolver(
                customerBridge: customerBridge,
                countryBridge: countryBridge,
                thirdPartyBridge: thirdPartyBridge,
                featureFlagsSdk: featureFlagsSdk,
                bnplProvidersService: bnplProvidersService
        )
        when(featureFlagsSdk.isActiveForCountry(Mockito.any(), Mockito.any())).thenReturn(false)
    }

    @Test
    void 'user from india without bnpl provider shouldnt have creditLine section'(){
        when(bnplProvidersService.currentUserHasBnplWallet(indianCustomer.accessToken)).thenReturn(false)
        sut.profileSections(indianCustomer)
        assertNull(sut.profileSections(indianCustomer).find {it.id == 'CREDIT_LINES'})
    }

    @Test
    void 'user from india with bnpl provider should have creditLine section'(){
        when(bnplProvidersService.currentUserHasBnplWallet(indianCustomer.accessToken)).thenReturn(true)

        assertNotNull(sut.profileSections(indianCustomer).find {it.id == 'CREDIT_LINES'})
    }

    @Test
    void 'user not from india shouldnt have creditLine section'(){
        def notIndianCustomer = anyCustomerWithIdAndAccessToken("ar")
        assertNull(sut.profileSections(notIndianCustomer).find {it.id == 'CREDIT_LINES'})
    }
}
