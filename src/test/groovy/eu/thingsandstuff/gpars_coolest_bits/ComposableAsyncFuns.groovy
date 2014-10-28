package eu.thingsandstuff.gpars_coolest_bits

import groovy.util.logging.Slf4j
import groovyx.gpars.AsyncFun
import groovyx.gpars.GParsPool

@Slf4j
class ComposableAsyncFuns {

    public static void main(String[] args) {
        log.info "Computation result is: " + GParsPool.withPool {
            new Computations().compute()
//            Promise result = new Computations().compute()
//            result.get()
        }
    }
}

@Slf4j
class Computations implements ParallelismTestUtils {

    Closure compute = {
        def positives = [1, 2, 3]
        def negatives = [-1, -2]
        //TODO extract vars @demo
        minus(sum(squares(positives)), minus(sum(negatives), sum(squares([0])))).get()
    }

    @AsyncFun
    Closure minus = { a, b ->
        doSlowly("subtracting $a - $b") {
            a - b - sum([9000]).get()
        }
    }

    @AsyncFun
    Closure sum = { elements ->
        doSlowly("summing $elements") {
            elements.sum()
        }
    }

    @AsyncFun
    Closure squares = { elements ->
        doSlowly("squaring $elements") {
            elements.collect { it ** 2 }
        }
    }

    private static doSlowly(String message, Closure closure) {
        log.info "Started $message"
        spinFor(100)
        def result = closure()
        log.info "Finished $message, result is $result"
        result
    }
}
