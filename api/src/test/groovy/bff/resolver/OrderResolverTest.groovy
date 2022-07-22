package bff.resolver

import bff.bridge.OrderBridge
import bff.model.Order
import bff.model.PaymentModeType
import bff.service.MoneyService
import com.coxautodev.graphql.tools.GraphQLResolver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

import static bff.TestExtensions.anyOrder
import static bff.model.OrderStatus.PENDING
import static java.util.Collections.emptyList

@RunWith(MockitoJUnitRunner)
class OrderResolverTest implements GraphQLResolver<Order> {
    @Mock
    private OrderBridge orderBridge
    @Mock
    private MoneyService moneyService

    private OrderResolver sut

    @Before
    void setup() {
        sut = new OrderResolver(
                orderBridge: orderBridge,
                moneyService: moneyService
        )
    }

    @Test
    void 'given total_money between 1 and 100 when paymentMode then return PAY_NOW'() {
        testPaymentMode(BigDecimal.valueOf(50.744), PaymentModeType.PAY_NOW)
    }

    @Test
    void 'given total_money between 101 and 200 when paymentMode then return PAY_LATER'() {
        testPaymentMode(BigDecimal.valueOf(150.744), PaymentModeType.PAY_LATER)
    }

    @Test
    void 'given total_money between 201 and 300 when paymentMode then return PAY_NOW_OR_LATER'() {
        testPaymentMode(BigDecimal.valueOf(250.744), PaymentModeType.PAY_NOW_OR_LATER)
    }

    @Test
    void 'given total_money between 301 and 400 when paymentMode then return NONE'() {
        testPaymentMode(BigDecimal.valueOf(350.744), PaymentModeType.NONE)
    }

    @Test
    void 'given total_money greater than 400 when paymentMode then return PAY_NOW_OR_LATER'() {
        testPaymentMode(BigDecimal.valueOf(4550.744), PaymentModeType.PAY_NOW_OR_LATER)
    }

    void testPaymentMode(BigDecimal amount, PaymentModeType paymentModeType) {
        def order = anyOrder(PENDING, emptyList(), amount)
        def expected = paymentModeType
        def result = sut.paymentMode(order)
        assert expected == result.paymentType
    }
}