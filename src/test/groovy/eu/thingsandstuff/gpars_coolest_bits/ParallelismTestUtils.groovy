package eu.thingsandstuff.gpars_coolest_bits

trait ParallelismTestUtils {

    static <T> List<T> shuffle(List<T> list) {
        Collections.shuffle(list)
        return list
    }

    static void spinFor(Integer millis) {
        def start = System.currentTimeMillis()
        while (System.currentTimeMillis() < start + millis) { /* spin it, baby! */ }
    }

    static timedCall(Closure closure) {
        def start = System.currentTimeMillis()
        def result = closure()
        def end = System.currentTimeMillis()
        return [end - start, result]
    }

}