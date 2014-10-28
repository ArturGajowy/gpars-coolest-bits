package eu.thingsandstuff.gpars_coolest_bits

import groovyx.gpars.GParsPool
import groovyx.gpars.pa.PAWrapper
import spock.lang.Specification

class ParallelCollectionsMapReduceSpec extends Specification {

    String[] words = "foo bar baz foo kaboom".split()

    def 'Map-Reduce: filter & map, operations independence'() {
        def fWords, firstLetters
        GParsPool.withPool {
            PAWrapper<String[]> wordsParallel = words.parallel
            fWords = wordsParallel.filter { it.startsWith('f') }.collection
            firstLetters = wordsParallel.map { it.charAt(0) }.collection
            /* the whole computation does not start till we force it using .collection */
        }

        expect:
        fWords == ['foo', 'foo']
        firstLetters == ['f', 'b', 'b', 'f', 'k']
    }

    def 'Map-Reduce: reduce, max (see also: min, sum, size)'() {
        def reduction, longest
        GParsPool.withPool {
            PAWrapper<String[]> wordsParallel = words.parallel
            reduction = wordsParallel.reduce('' /* this must be neutral element */) { joined, word -> joined + word }
            longest = wordsParallel.max { it.size() }
        }


        expect:
        [1, 2, 3, 4].inject(9) { a, b -> a + b } == 9 + 1 + 2 + 3 + 4

        reduction == 'foobarbazfookaboom'
        longest == 'kaboom'
    }

    def 'Map-Reduce: groupBy'() {
        Map<Integer, String> byLength
        GParsPool.withPool {
            PAWrapper<String[]> wordsParallel = words.parallel
            byLength = wordsParallel.groupBy { it.size() }
        }

        expect:
        byLength[3] as Set == ['foo', 'bar', 'baz', 'foo'] as Set
        byLength[6] == ['kaboom']
    }

    def 'Map-Reduce: combine'() {
        Map<String, Integer> expectedWordCounts =
            [foo: 2, bar: 1, baz: 1, kaboom: 1]
        Map<String, Integer> wordCounts
        List<Map.Entry<String, Integer>> wordCountsEntries

        String[] words = "foo bar baz foo kaboom".split()

        GParsPool.withPool {
            PAWrapper<String[]> wordsParallel = words.parallel

            wordCountsEntries = wordsParallel
                .map { [it, 1] }
                .groupBy { it[0] }
                .getParallel()
                .map { it.value = it.value.size(); it }
                .sort { it.value }
                .collection

            wordCounts = wordsParallel
                .map { [it, 1] }
                .combine(0) { sum, value -> sum + value }
        }

        expect:
        wordCounts == expectedWordCounts
        wordCountsEntries as Set == expectedWordCounts.entrySet()
    }

}
