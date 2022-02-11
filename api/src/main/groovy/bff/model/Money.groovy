package bff.model

import groovy.transform.Immutable
import groovy.util.logging.Slf4j

import java.text.NumberFormat

@Immutable
@Slf4j
class Money {

    String currency
    BigDecimal amount

    String symbol(String languageTag) {
        Currency.getInstance(currency).getSymbol(Locale.forLanguageTag(languageTag))
    }

    String text(String languageTag) {
        try {
            def format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag(languageTag))
            format.setCurrency(Currency.getInstance(currency))
            format.format(amount)
        } catch (Throwable e) {
            log.error("Error on money.text() for {} with languageTag {}", this, languageTag)
            throw e
        }
    }
}
