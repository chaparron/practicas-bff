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
        if(amount) {
            return new Money(countryBridge.getCountry(JwtToken.countryFromString(accessToken)).currency.code, amount)
        }
        return null
    }

    Money getMoneyByCountry(String countryId, BigDecimal amount) {
        if(amount) {
            return new Money(countryBridge.getCountry(countryId).currency.code, amount)
        }
        return null
    }
}
