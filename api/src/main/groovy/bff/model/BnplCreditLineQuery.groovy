package bff.model

import bff.JwtToken
import bnpl.sdk.BnPlSdk
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.CompletableFuture

import static bff.model.CreditLines.fromSdk

@Component
@Slf4j
class BnplCreditLineQuery implements GraphQLQueryResolver {
    static String BNPL_PROPERTY_PREFIX = "bnpl."
    @Autowired
    private BnPlSdk bnPlSdk;

    CompletableFuture<CreditLinesResult> getCreditLines(CreditLinesRequestInput input) {
        def userId = JwtToken.userIdFromToken(input.getAccessToken());
        bnPlSdk.fetchBalance(userId, input.getAccessToken())
                .map{
                    fromSdk(it)
                }
                .toFuture();
    }
}
