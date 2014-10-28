package eu.thingsandstuff.gpars_coolest_bits

import groovyx.gpars.GParsPool
import spock.lang.Specification
import spock.lang.Unroll

class ParallelCollectionsAreParallelSpec extends Specification implements ParallelismTestUtils {

    static collection = shuffle((1..100).toList())
    static delayMillis = 10

    @Unroll
    def 'Parallel versions of collection methods: #methodName'() {
        given:
        def slowIterationBody = makeSlow(iterationBody)

        when:
        def (sequentialTime, sequentialResult) = timedCall {
            collection."${methodName}" slowIterationBody
        }

        def (parallelTime, parallelResult) = GParsPool.withPool {
            timedCall {
                collection."${methodName}Parallel" slowIterationBody
            }
        }

        then:
        sequentialResult == parallelResult
        sequentialTime > 2 * parallelTime

        where:
        methodName |  iterationBody
        "collect"  |  { it * 2 }
        "min"      |  { it }
        "max"      |  { -it }
        "count"    |  { it > collection.first() }
        "inject"   |  { a, b -> a + b }
    }


    static <T> Closure<T> makeSlow(Closure<T> computation) {
        return { Object[] arguments ->
            spinFor(delayMillis)
            computation(*arguments)
        }
    }

}
