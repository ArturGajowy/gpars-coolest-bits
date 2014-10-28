package eu.thingsandstuff.awaitility

import com.jayway.awaitility.Awaitility
import com.jayway.awaitility.Duration
import com.jayway.awaitility.core.ConditionEvaluationListener
import com.jayway.awaitility.core.ConditionFactory
import com.jayway.awaitility.core.FieldSupplierBuilder
import com.jayway.awaitility.groovy.AwaitilitySupport

import java.util.concurrent.TimeUnit

trait AwaitilitySupportTrait {

    /**
     * This is to trigger ConditionFactory metaclass' enrichments performed in AwaitilitySupport's constructor
     */
    private static makeConditionFactorSpockCompatible = new AwaitilitySupport()

    static void catchUncaughtExceptionsByDefault() {
        Awaitility.catchUncaughtExceptionsByDefault()
    }

    static void doNotCatchUncaughtExceptionsByDefault() {
        Awaitility.doNotCatchUncaughtExceptionsByDefault()
    }

    static void reset() {
        Awaitility.reset()
    }

    static ConditionFactory await() {
        Awaitility.await()
    }

    static ConditionFactory await(String alias) {
        Awaitility.await(alias)
    }

    static ConditionFactory catchUncaughtExceptions() {
        Awaitility.catchUncaughtExceptions()
    }

    static ConditionFactory dontCatchUncaughtExceptions() {
        Awaitility.dontCatchUncaughtExceptions()
    }

    static ConditionFactory with() {
        Awaitility.with()
    }

    static ConditionFactory given() {
        Awaitility.given()
    }

    static ConditionFactory waitAtMost(Duration timeout) {
        Awaitility.waitAtMost(timeout)
    }

    static ConditionFactory waitAtMost(long value, TimeUnit unit) {
        Awaitility.waitAtMost(value, unit)
    }

    static void setDefaultPollInterval(long pollInterval, TimeUnit unit) {
        Awaitility.setDefaultPollInterval(pollInterval, unit)
    }

    static void setDefaultPollDelay(long pollDelay, TimeUnit unit) {
        Awaitility.setDefaultPollDelay(pollDelay, unit)
    }

    static void setDefaultTimeout(long timeout, TimeUnit unit) {
        Awaitility.setDefaultTimeout(timeout, unit)
    }

    static void setDefaultPollInterval(Duration pollInterval) {
        Awaitility.setDefaultPollInterval(pollInterval)
    }

    static void setDefaultPollDelay(Duration pollDelay) {
        Awaitility.setDefaultPollDelay(pollDelay)
    }

    static void setDefaultTimeout(Duration defaultTimeout) {
        Awaitility.setDefaultTimeout(defaultTimeout)
    }

    static void setDefaultConditionEvaluationListener(ConditionEvaluationListener defaultConditionEvaluationListener) {
        Awaitility.setDefaultConditionEvaluationListener(defaultConditionEvaluationListener)
    }

    static <S> S to(S object) {
        Awaitility.to(object)
    }

    static FieldSupplierBuilder fieldIn(Object object) {
        Awaitility.fieldIn(object)
    }

    static FieldSupplierBuilder fieldIn(Class<?> clazz) {
        Awaitility.fieldIn(clazz);
    }
}
