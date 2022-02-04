package bff.resolver


import bff.bridge.sdk.Cms
import bff.model.ContextInput
import bff.model.Module
import bff.model.Piece
import bff.support.ExecutorService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.CompletableFuture

import static java.util.Optional.ofNullable

@Component
class ModuleResolver implements GraphQLResolver<Module> {

    @Autowired
    Cms cms
    @Autowired
    ExecutorService executor

    CompletableFuture<List<Piece>> pieces(Module module, ContextInput context, Integer size) {
        executor.doAsync {
            cms.build(module, context, ofNullable(size))
        }
    }

}
