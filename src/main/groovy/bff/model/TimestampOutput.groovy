package bff.model

import com.fasterxml.jackson.databind.util.StdDateFormat

import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TimestampOutput {
    Date value

    TimestampOutput(String date) {
        value = new StdDateFormat().withColonInTimeZone(true).parse(date)
    }

    String getValue(TimestampFormat format, String zoneId) {
        if (value)
            DateTimeFormatter.ofPattern(format.pattern()).format(value.toInstant().atZone(ZoneId.of(zoneId)))
    }
}

enum TimestampFormat {

    DATE_ONLY{
        String pattern() {
            "yyyMMddZ"
        }
    },
    DATE_ISO{

        String pattern() {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        }
    }

    abstract String pattern()
}