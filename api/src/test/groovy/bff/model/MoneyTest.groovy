package bff.model

import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.assertEquals

@Ignore("Jvm 11 is required to work")
class MoneyTest {

    @Test
    void 'get money for Peso'() {
        def money = new Money("ARS", new BigDecimal(1))

        assertEquals('$', money.symbol("es-AR"))
        assertEquals('$ 1,00', money.text("es-AR"))

        assertEquals('ARS', money.symbol("es-ES"))
        assertEquals('1,00 ARS', money.text("es-ES"))

        assertEquals('ARS', money.symbol("en-US"))
        assertEquals('ARS1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Euro'() {
        def money = new Money("EUR", new BigDecimal(1))

        assertEquals('€', money.symbol("es-ES"))
        assertEquals('1,00 €', money.text("es-ES"))

        assertEquals('€', money.symbol("it-IT"))
        assertEquals('1,00 €', money.text("it-IT"))

        assertEquals('€', money.symbol("fr-FR"))
        assertEquals('1,00 €', money.text("fr-FR"))

        assertEquals('€', money.symbol("pt-PT"))
        assertEquals('1,00 €', money.text("pt-PT"))

        assertEquals('€', money.symbol("de-DE"))
        assertEquals('1,00 €', money.text("de-DE"))

        assertEquals('€', money.symbol("el-GR"))
        assertEquals('1,00 €', money.text("el-GR"))

        assertEquals('€', money.symbol("en-BE"))
        assertEquals('1,00 €', money.text("en-BE"))

        assertEquals('€', money.symbol("en-NL"))
        assertEquals('€ 1,00', money.text("en-NL"))

        assertEquals('€', money.symbol("en-GB"))
        assertEquals('€1.00', money.text("en-GB"))

        assertEquals('€', money.symbol("en-US"))
        assertEquals('€1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Pound sterling'() {
        def money = new Money("GBP", new BigDecimal(1))

        assertEquals('£', money.symbol("en-GB"))
        assertEquals('£1.00', money.text("en-GB"))

        assertEquals('GBP', money.symbol("es-ES"))
        assertEquals('1,00 GBP', money.text("es-ES"))

        assertEquals('£', money.symbol("it-IT"))
        assertEquals('1,00 £', money.text("it-IT"))

        assertEquals('£', money.symbol("en-US"))
        assertEquals('£1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Turkish Lira'() {
        def money = new Money("TRY", new BigDecimal(1))

        assertEquals('₺', money.symbol("tr-TR"))
        assertEquals('₺1,00', money.text("tr-TR"))

        assertEquals('TRY', money.symbol("en-US"))
        assertEquals('TRY1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Egypt Lira'() {
        def money = new Money("EGP", new BigDecimal(1))

        assertEquals('ج.م.‏', money.symbol("ar-EG"))
        assertEquals('ج.م.‏ ١٫٠٠', money.text("ar-EG"))

        assertEquals('EGP', money.symbol("en-US"))
        assertEquals('EGP1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Philippines Peso'() {
        def money = new Money("PHP", new BigDecimal(1))

        assertEquals('₱', money.symbol("en-PH"))
        assertEquals('₱1.00', money.text("en-PH"))

        assertEquals('PHP', money.symbol("en-US"))
        assertEquals('PHP1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Morocco dirham'() {
        def money = new Money("MAD", new BigDecimal(1))

        assertEquals('د.م.‏', money.symbol("ar-MA"))
        assertEquals('د.م.‏ 1,00', money.text("ar-MA"))

        assertEquals('MAD', money.symbol("en-US"))
        assertEquals('MAD1.00', money.text("en-US"))
    }

    @Test
    void 'get money for ruble'() {
        def money = new Money("RUB", new BigDecimal(1))

        assertEquals('₽', money.symbol("ru-RU"))
        assertEquals('1,00 ₽', money.text("ru-RU"))

        assertEquals('RUB', money.symbol("en-US"))
        assertEquals('RUB1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Dominican peso'() {
        def money = new Money("DOP", new BigDecimal(1))

        assertEquals('RD$', money.symbol("es-DO"))
        assertEquals('RD$1.00', money.text("es-DO"))

        assertEquals('DOP', money.symbol("en-US"))
        assertEquals('DOP1.00', money.text("en-US"))
    }

    @Test
    void 'get money for rand'() {
        def money = new Money("ZAR", new BigDecimal(1))

        assertEquals('R', money.symbol("en-ZA"))
        assertEquals('R1,00', money.text("en-ZA"))

        assertEquals('ZAR', money.symbol("en-US"))
        assertEquals('ZAR1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Serbian dinar'() {
        def money = new Money("RSD", new BigDecimal(1))

        assertEquals('дин.', money.symbol("sr-RS"))
        assertEquals('1,00 дин.', money.text("sr-RS"))

        assertEquals('RSD', money.symbol("en-US"))
        assertEquals('RSD1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Bangladeshi Taka'() {
        def money = new Money("BDT", new BigDecimal(1))

        assertEquals('৳', money.symbol("bn-BD"))
        assertEquals('১.০০৳', money.text("bn-BD"))

        assertEquals('BDT', money.symbol("en-US"))
        assertEquals('BDT1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Ringgit'() {
        def money = new Money("MYR", new BigDecimal(1))

        assertEquals('RM', money.symbol("ms-MY"))
        assertEquals('RM1.00', money.text("ms-MY"))

        assertEquals('MYR', money.symbol("en-US"))
        assertEquals('MYR1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Algerian dinar'() {
        def money = new Money("DZD", new BigDecimal(1))

        assertEquals('د.ج.‏', money.symbol("ar-DZ"))
        assertEquals('د.ج.‏ 1,00', money.text("ar-DZ"))

        assertEquals('DZD', money.symbol("en-US"))
        assertEquals('DZD1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Colombian peso'() {
        def money = new Money("COP", new BigDecimal(1))

        assertEquals('$', money.symbol("es-CO"))
        assertEquals('$ 1,00', money.text("es-CO"))

        assertEquals('COP', money.symbol("en-US"))
        assertEquals('COP1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Vietnamese dong'() {
        def money = new Money("VND", new BigDecimal(1))

        assertEquals('₫', money.symbol("vi-VN"))
        assertEquals('1 ₫', money.text("vi-VN"))

        assertEquals('₫', money.symbol("en-US"))
        assertEquals('₫1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Nigerian naira'() {
        def money = new Money("NGN", new BigDecimal(1))

        assertEquals('₦', money.symbol("en-NG"))
        assertEquals('₦1.00', money.text("en-NG"))

        assertEquals('NGN', money.symbol("en-US"))
        assertEquals('NGN1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Brazilian real'() {
        def money = new Money("BRL", new BigDecimal(1))

        assertEquals('R$', money.symbol("pt-BR"))
        assertEquals('R$ 1,00', money.text("pt-BR"))

        assertEquals('R$', money.symbol("en-US"))
        assertEquals('R$1.00', money.text("en-US"))
    }

    @Test
    void 'get money for United States dollar'() {
        def money = new Money("USD", new BigDecimal(1))

        assertEquals('$', money.symbol("en-US"))
        assertEquals('$1.00', money.text("en-US"))

        assertEquals('$', money.symbol("en-ES"))
        assertEquals('$1.00', money.text("en-ES"))

        assertEquals('US$', money.symbol("es-ES"))
        assertEquals('1,00 US$', money.text("es-ES"))
    }

    @Test
    void 'get money for Peruvian sol'() {
        def money = new Money("PEN", new BigDecimal(1))

        assertEquals('S/', money.symbol("es-PE"))
        assertEquals('S/1.00', money.text("es-PE"))

        assertEquals('PEN', money.symbol("en-US"))
        assertEquals('PEN1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Chilean peso'() {
        def money = new Money("CLP", new BigDecimal(1))

        assertEquals('$', money.symbol("es-CL"))
        assertEquals('$1', money.text("es-CL"))

        assertEquals('CLP', money.symbol("en-US"))
        assertEquals('CLP1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Singapore dollar'() {
        def money = new Money("SGD", new BigDecimal(1))

        assertEquals('$', money.symbol("en-SG"))
        assertEquals('$1.00', money.text("en-SG"))

        assertEquals('SGD', money.symbol("en-US"))
        assertEquals('SGD1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Kenyan shilling'() {
        def money = new Money("KES", new BigDecimal(1))

        assertEquals('Ksh', money.symbol("en-KE"))
        assertEquals('Ksh1.00', money.text("en-KE"))

        assertEquals('KES', money.symbol("en-US"))
        assertEquals('KES1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Indian rupee'() {
        def money = new Money("INR", new BigDecimal(1))

        assertEquals('₹', money.symbol("bn-IN"))
        assertEquals('১.০০₹', money.text("bn-IN"))

        assertEquals('₹', money.symbol("en-US"))
        assertEquals('₹1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Mexican peso'() {
        def money = new Money("MXN", new BigDecimal(1))

        assertEquals('$', money.symbol("es-MX"))
        assertEquals('$1.00', money.text("es-MX"))

        assertEquals('MX$', money.symbol("en-US"))
        assertEquals('MX$1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Uruguayan peso'() {
        def money = new Money("UYU", new BigDecimal(1))

        assertEquals('$', money.symbol("es-UY"))
        assertEquals('$ 1,00', money.text("es-UY"))

        assertEquals('UYU', money.symbol("en-US"))
        assertEquals('UYU1.00', money.text("en-US"))
    }

    @Test
    void 'get money for Czech koruna'() {
        def money = new Money("CZK", new BigDecimal(1))

        assertEquals('Kč', money.symbol("cs-CZ"))
        assertEquals('1,00 Kč', money.text("cs-CZ"))

        assertEquals('CZK', money.symbol("en-US"))
        assertEquals('CZK1.00', money.text("en-US"))
    }
}
