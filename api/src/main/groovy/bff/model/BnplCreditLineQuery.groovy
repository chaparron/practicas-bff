package bff.model


import bff.bridge.BnplBridge
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

import java.util.concurrent.CompletableFuture

@Component
@Slf4j
class BnplCreditLineQuery implements GraphQLQueryResolver {

    @Autowired
    private BnplBridge bnplBridge

    CompletableFuture<CreditLinesResult> getCreditLines(CreditLinesRequestInput input) {
        Mono.just(bnplBridge.userBalance(input.accessToken)).toFuture()
    }
}
