package bff.model

import com.fasterxml.jackson.databind.util.StdDateFormat
import groovy.transform.EqualsAndHashCode

import java.time.ZoneId
import java.time.format.DateTimeFormatter

@EqualsAndHashCode
class TimestampOutput {
    Date value

    TimestampOutput(String date) {
        value = new StdDateFormat().withColonInTimeZone(true).parse(date)
    }

    String getValue(TimestampFormat format, String zoneId) {
        if (value)
            DateTimeFormatter.ofPattern(format.pattern()).format(value.toInstant().atZone(ZoneId.of(zoneId)))
    }

    String getIsoutc() {
        DateTimeFormatter.ofPattern(TimestampFormat.DATE_ISO.pattern()).format(value.toInstant().atZone(ZoneId.of("UTC")))
    }

}

enum TimestampFormat {

    DATE_ONLY{
        String pattern() {
            "yyyMMddZ"
        }
    },
    DATE_ISO {
        String pattern() {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        }
    }

    abstract String pattern()
}