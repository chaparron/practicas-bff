package bff.service

import bff.JwtToken
import bff.model.CreditLineProvider
import bff.model.CreditProvider
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class BnplProvidersService {

    def knownUsers = ["2456", "1725", "1510"]

    List<CreditLineProvider> creditLineProvidersFor(String accessToken) {
        def userId = JwtToken.userIdFromToken(accessToken)

        knownUsers.contains(userId) ? [new CreditLineProvider(provider: CreditProvider.SUPERMONEY)] : null
    }
}
