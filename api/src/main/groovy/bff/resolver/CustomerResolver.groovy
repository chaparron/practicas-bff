package bff.resolver

import bff.bridge.BnplBridge
import bff.bridge.CountryBridge
import bff.bridge.CustomerBridge
import bff.bridge.ThirdPartyBridge
import bff.bridge.http.CountryGatewayBridgeImpl
import bff.model.*
import bff.service.bnpl.BnplProvidersService
import com.coxautodev.graphql.tools.GraphQLResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import wabi2b.sdk.featureflags.FeatureFlagsSdk
import wabi2b.sdk.regional.RegionalConfigSdk

@Component
@Slf4j
class CustomerResolver implements GraphQLResolver<Customer> {

    @Autowired
    CustomerBridge customerBridge
    @Autowired
    CountryBridge countryBridge
    @Autowired
    ThirdPartyBridge thirdPartyBridge
    @Autowired
    FeatureFlagsSdk featureFlagsSdk
    @Autowired
    BnplProvidersService bnplProvidersService
    @Autowired
    BnplBridge bnplBridge

    List<VerificationDocument> verificationDocuments(Customer customer) {
        customer.verificationDocuments ?: customerBridge.findVerificationDocs(customer.accessToken)
    }

    List<Address> addresses(Customer customer) {
        customer.addresses ?: customerBridge.findAddressesByCustomerAccessToken(customer.accessToken)
    }

    List<ProfileSection> profileSections(Customer customer) {
        if (!customer.country_id) return []
        List<ProfileSection> ps = new ArrayList<ProfileSection>()

        ps.push(new ProfileSection(id: "STORE_INFORMATION"))
        ps.push(new ProfileSection(id: "PERSONAL_INFORMATION"))
        ps.push(new ProfileSection(id: "DOCUMENTS"))

        if (customer.customerStatus != CustomerStatus.APPROVED) {
            return ps
        }

        ps.push(new ProfileSection(id: "NOTIFICATIONS"))
        ps.push(new ProfileSection(id: "ORDERS"))
        ps.push(new ProfileSection(id: "SUGGESTED_ORDER"))

        if (featureFlagsSdk.isActiveForCountry("RETAILER_INFORMATION", customer.country_id)) {
            ps.push(new ProfileSection(id: "INVOICES"))
        }

        if (featureFlagsSdk.isActiveForCountry("BNPL_FEATURE_FLAG", customer.country_id)) {
            def status = bnplBridge.customerStatus(customer.accessToken)
            if (status.active) {
                ps.push(new ProfileSection(id: "CREDIT_LINES"))
            }
        }

        if (countryBridge.getCountry(customer.country_id).customerBranchesEnabled
                && customer.storeType == StoreType.MAIN_OFFICE) {
            ps.push(new ProfileSection(id: "BRANCH_OFFICE"))
        } else if (customer.storeType == StoreType.MAIN_OFFICE) {
            ps.push(new ProfileSection(id: "MY_ADDRESSES"))
        }
        if (customer.country_id == 'my') {
            ps.push(new ProfileSection(id: "QR_PAYMENTS"))
            ps.push(new ProfileSection(id: "PAY_WITH_QR"))
        }

        ps
    }

    Boolean hasOrders(Customer customer) {
        if (customer.customerStatus == CustomerStatus.APPROVED && customer.accessToken) {
            customerBridge.customerHasOrders(new AccessTokenInput(accessToken: customer.accessToken))
        }
        return false
    }

    Country country(Customer customer) {
        countryBridge.getCountry(customer.country_id)
    }

    User user(Customer customer) {
        if (customer.user.username == null) {
            return customerBridge.getUserById(customer.accessToken, customer.user.id)
        }
        return customer.user
    }

    boolean marketingEnabled(Customer customer) {
        if (customer.marketingEnabledForcedInResponse != null) {
            return customer.marketingEnabledForcedInResponse
        }
        thirdPartyBridge.findCustomerConsent(customer.id.toLong(), customer.accessToken)
    }

}
