package eu.thingsandstuff.gpars_coolest_bits

import groovy.util.logging.Slf4j
import groovyx.gpars.group.DefaultPGroup
import groovyx.gpars.group.NonDaemonPGroup

@Slf4j
class AgentPGroupAndItsDaemonity implements ParallelismTestUtils {

    //Motto: If a tree falls in a forest and no one is around to hear it,
    // does it make a sound?
    public static void main(String[] args) {
        given:
        def group = new DefaultPGroup()
//        def group = new NonDaemonPGroup()
        def agent = group.agent(42)

        agent {
            spinFor(1000)
            log.info "I'm done"
        }

//        agent.await()
        log.info "Bye"
//        group.shutdown()
    }
}
