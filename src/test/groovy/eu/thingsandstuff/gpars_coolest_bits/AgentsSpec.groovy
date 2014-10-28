package eu.thingsandstuff.gpars_coolest_bits

import eu.thingsandstuff.awaitility.AwaitilitySupportTrait
import groovy.util.logging.Slf4j
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Dataflow
import spock.lang.Specification

import static java.util.concurrent.TimeUnit.SECONDS

@Slf4j
class AgentsSpec extends Specification implements AwaitilitySupportTrait, ParallelismTestUtils {

    def setup() {
        setDefaultTimeout(1, SECONDS)
    }

    //warning: first docs example for agents is *at most* pseudo-code...
    def 'updating immutable state'() {
        given:
        def initialValue = 0
        Agent<Integer> agent = new Agent(initialValue)

        when:
        agent.send { /*async!*/
            updateValue(it + 1)
        }

        then:
        agent.val == initialValue + 1
    }

    def 'updating mutable state'() {
        given:
        def initialValue = []
        Agent<List<Integer>> agent = new Agent(initialValue)

        when:
        agent.send {
            it.add(42)
        }

        then:
        agent.val == [42]
        agent.val.is(initialValue)
    }

    def 'equivalent syntax for executing an action within an agent'() {
        Agent<Integer> agent = new Agent(0)

        when:
        agent << {
            updateValue(1)
        }

        then:
        agent.val == 1

        when:
        agent {
            updateValue(-1)
        }

        then:
        agent.val == -1
    }


    def 'providing a copy method'() {
        given:
        def initialValue = 42
        Agent<Integer> agent = new Agent(initialValue, { -it })
        def expectedValue = -initialValue
        def asyncResult

        expect:
        agent.instantVal == expectedValue
        agent.val == expectedValue

        when:
        agent.valAsync {
            asyncResult = it
        }

        then:
        await().until {
            asyncResult == expectedValue
        }
    }

    def 'Agents serialize (as in DB transactions, not as in @Serializable) commands sent to them'() {
        given:
        def agent = Agent.fairAgent([]) //Agents can be fair since construction
//        def agent = Agent.agent([]) //by default Agents are unfair
//        agent.makeFair() //Agents can be fair since we ask them. BTW: there's no way back ;)

        when:
        Dataflow.task {
            log.info 'Scheduling addition of 1'
            agent.send {
                log.info 'Adding 1'
                it << 1
                log.info 'Added 1'
            }
        }
        Dataflow.task {
            spinFor(250)
            log.info 'Scheduling addition of 2'
            agent.send {
                log.info 'Adding 2'
                spinFor(500)
                it << 2
                log.info 'Added 2'
            }
        }
        Dataflow.task {
            spinFor(500)
            log.info 'Scheduling addition of 3'
            agent.send {
                log.info 'Adding 3'
                spinFor(1500)
                it << 3
                log.info 'Added 3'
            }
            log.info 'Scheduling addition of 4'
            agent.send {
                log.info 'Adding 4'
                it << 4
                log.info 'Added 4'
            }
            log.info 'Scheduled addition of 3 and 4'
        }

        then:
        spinFor(100)
        logAndReturn('Checking val: ') { agent.val } == [1]
        log.info 'Awaiting val == [1, 2]'
        await().until {
            logAndReturn('Checking val: ') { agent.val } == [1, 2]
        }
        logAndReturn('Checking instantVal: ') { agent.instantVal } == [1, 2, 3]
        logAndReturn('Checking val: ') { agent.val } == [1, 2, 3, 4]
    }

    private static logAndReturn(def prefix, Closure callback) {
        log.info prefix
        def result = callback()
        log.info prefix + result
        result
    }

    def 'exceptions thrown during update execution can cause some confusing (lack of) results'() {
        given:
        def initialValue = []
        Agent<List<Integer>> agent = new Agent(initialValue)

        when:
        agent.send {
            it.ooops_I_misspelled_a_method_name()
        }

        then:
        noExceptionThrown() //no surprise here, it's asynchronous after all...

        when:
        def newValue = agent.val

        then: //surprise (?)
        noExceptionThrown()
        newValue == old(initialValue)

        //one should be careful with method names and any possible errors
        //when scheduling updates in an agent...
    }

    def 'errors can be retrieved, but only SYNCHRONOUSLY...'() {
        given:
        def initialValue = []
        Agent<List<Integer>> agent = new Agent(initialValue)

        when:
        agent.send {
            it.ooops_I_misspelled_a_method_name()
        }

        then:
        await().until {
            def errors = agent.getErrors()
            errors.size() == 1 && errors.grep(MissingMethodException)
        }
        agent.getErrors() == [] //getErrors clears the exception history!
    }

    //encapsulation: silly (extends agent, call privates - DemoAdvancedThreadSafeCounter.groovy)
    // and proper (delegation - DemoCart)
}