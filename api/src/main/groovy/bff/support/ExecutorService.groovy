package bff.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.Supplier

@Component
class ExecutorService {

    @Autowired
    Executor executor

    def <U> CompletableFuture<U> doAsync(Supplier<U> supplier) {
        CompletableFuture.supplyAsync(supplier, executor)
    }

}
