package eu.thingsandstuff.gpars_coolest_bits

import groovyx.gpars.GParsPool
import groovyx.gpars.ParallelEnhancer
import groovyx.gpars.pa.AbstractPAWrapper
import groovyx.gpars.pa.PAWrapper
import spock.lang.Specification
import spock.util.mop.ConfineMetaClassChanges

class ParallelCollectionsSpec extends Specification {

    def list = (1..100).toList()
    def iterationBody = { it * 2 }
    def expectedResult = list.collect(iterationBody)

    def 'Parallel collections using withPool'() {
        when:
        def result = GParsPool.withPool {
            list.collectParallel(iterationBody)
        }

        then:
        result == expectedResult
    }

    def 'Other parallel collections methods'() {
        when:
        GParsPool.withPool {
            list.eachParallel(iterationBody)
            list.eachWithIndexParallel { element, index -> }
            list.collectManyParallel { [it, it + 1] }
            list.findAllParallel(iterationBody)
            list.findAnyParallel(iterationBody)
            list.findParallel(iterationBody)
            list.everyParallel(iterationBody)
            list.anyParallel(iterationBody)
            list.grepParallel(iterationBody)
            list.groupByParallel(iterationBody)
            list.foldParallel { acc, elem -> acc + elem } /* DEPRECATED! use injectParallel instead */
            list.injectParallel { acc, elem -> acc + elem }
            list.minParallel(iterationBody)
            list.maxParallel(iterationBody)
            list.sumParallel() /* no closure version, must use fold or collect */
            list.splitParallel(iterationBody)
            list.countParallel(iterationBody)
        }

        then:
        noExceptionThrown()
    }

    @ConfineMetaClassChanges(ArrayList)
    def 'Parallel collections using ParallelEnhancer on a class (not recommended)'() {
        given:
        ParallelEnhancer.enhanceClass(ArrayList)

        when:
        def result = list.collectParallel(iterationBody)

        then:
        result == expectedResult

        when:
        def otherList = [] + list

        then:
        otherList.collectParallel(iterationBody) == expectedResult

        // - does not work for interfaces
        // - seems to work for superclasses (even more not recommended)
        // - enhancing more than one class in an inheritance chain seems to blow things up
    }

    def 'Parallel collections using ParallelEnhancer on an instance'() {
        given:
        ParallelEnhancer.enhanceInstance(list)

        when:
        def result = list.collectParallel(iterationBody)

        then:
        result == expectedResult

        when:
        def otherList = [1, 2, 3]
        otherList.collectParallel(iterationBody)


        then:
        thrown(MissingMethodException)
    }

}
