import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.OnConsoleStatusListener
import groovy.transform.Field

import static ch.qos.logback.classic.Level.INFO

@Field String systemOutAppender = 'systemOutAppender'

displayStatusOnConsole()
scan('5 seconds')
setupAppenders()
setupLoggers()

def displayStatusOnConsole() {
    statusListener OnConsoleStatusListener
}

def setupAppenders() {
    def logPatternFormat = "%d{HH:mm:ss.SSS} [%25thread] | %msg%n"

    appender(systemOutAppender, ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = logPatternFormat
        }
    }
}

def setupLoggers() {
    root INFO, [systemOutAppender]
}