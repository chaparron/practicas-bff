package bff.service

import bff.JwtToken
import bff.bridge.CountryBridge
import bff.model.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MoneyService {

    @Autowired
    CountryBridge countryBridge

    Money getMoney(String accessToken, BigDecimal amount) {
        new Money(countryBridge.getCountry(JwtToken.countryFromString(accessToken)).currency.code, amount)
    }

    Money getMoneyByCountry(String countryId, BigDecimal amount) {
        new Money(countryBridge.getCountry(countryId).currency.code, amount)
    }
}
