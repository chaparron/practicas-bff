package bff.model


import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

import static bff.model.ApplicationMode.*
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

@RunWith(MockitoJUnitRunner.class)
class CommercialPromotionApplicationModeTest {

    @Test
    void 'lineal should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                LINEAL.appliesTo(
                        [anyStep(1, 5, Map.of())],
                        [anyProductCart(3, 1, 1)]
                )
        )
    }

    @Test
    void 'lineal should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                LINEAL.appliesTo(
                        [anyStep(1, 5, Map.of())],
                        [anyProductCart(3, 1, 5)]
                )
        )
    }

    @Test
    void 'lineal should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                LINEAL.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 1, 7)]
                )
        )
    }

    @Test
    void 'lineal should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                LINEAL.appliesTo(
                        [anyStep(10, 15, Map.of())],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 1, 9)
                        ]
                )
        )
    }

    @Test
    void 'lineal should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                LINEAL.appliesTo(
                        [anyStep(10, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 1, 9)
                        ]
                )
        )
    }

    @Test
    void 'lineal global should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                LINEAL_GLOBAL.appliesTo(
                        [anyStep(10, 15, Map.of())],
                        [anyProductCart(3, 5, 2)]
                )
        )
    }

    @Test
    void 'lineal global should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                LINEAL_GLOBAL.appliesTo(
                        [anyStep(10, 20, Map.of())],
                        [anyProductCart(3, 5, 4)]
                )
        )
    }

    @Test
    void 'lineal global should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                LINEAL_GLOBAL.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 6, 1)]
                )
        )
    }

    @Test
    void 'lineal global should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                LINEAL_GLOBAL.appliesTo(
                        [anyStep(10, 20, Map.of())],
                        [
                                anyProductCart(3, 2, 4),
                                anyProductCart(4, 5, 2)
                        ]
                )
        )
    }

    @Test
    void 'lineal global should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                LINEAL_GLOBAL.appliesTo(
                        [anyStep(10, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 5, 2)
                        ]
                )
        )
    }

    @Test
    void 'progressive should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE.appliesTo(
                        [anyStep(1, 5, Map.of())],
                        [anyProductCart(3, 1, 1)]
                )
        )
    }

    @Test
    void 'progressive should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE.appliesTo(
                        [anyStep(1, 5, Map.of())],
                        [anyProductCart(3, 1, 5)]
                )
        )
    }

    @Test
    void 'progressive should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 1, 7)]
                )
        )
    }

    @Test
    void 'progressive should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                PROGRESSIVE.appliesTo(
                        [anyStep(10, 15, Map.of())],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 1, 9)
                        ]
                )
        )
    }

    @Test
    void 'progressive should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                PROGRESSIVE.appliesTo(
                        [anyStep(10, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 1, 9)
                        ]
                )
        )
    }

    @Test
    void 'progressive global should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE_GLOBAL.appliesTo(
                        [anyStep(10, 15, Map.of())],
                        [anyProductCart(3, 5, 2)]
                )
        )
    }

    @Test
    void 'progressive global should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE_GLOBAL.appliesTo(
                        [anyStep(10, 20, Map.of())],
                        [anyProductCart(3, 5, 4)]
                )
        )
    }

    @Test
    void 'progressive global should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                PROGRESSIVE_GLOBAL.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 6, 1)]
                )
        )
    }

    @Test
    void 'progressive global should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                PROGRESSIVE_GLOBAL.appliesTo(
                        [anyStep(10, 20, Map.of())],
                        [
                                anyProductCart(3, 2, 4),
                                anyProductCart(4, 5, 2)
                        ]
                )
        )
    }

    @Test
    void 'progressive global should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                PROGRESSIVE_GLOBAL.appliesTo(
                        [anyStep(10, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 5, 2)
                        ]
                )
        )
    }

    @Test
    void 'slabbed should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                SLABBED.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [anyProductCart(3, 5, 5)]
                )
        )
    }

    @Test
    void 'slabbed should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                SLABBED.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [anyProductCart(3, 5, 15)]
                )
        )
    }

    @Test
    void 'slabbed should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                SLABBED.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 6, 10)]
                )
        )
    }

    @Test
    void 'slabbed should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                SLABBED.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [
                                anyProductCart(3, 2, 4),
                                anyProductCart(4, 2, 1)
                        ]
                )
        )
    }

    @Test
    void 'slabbed should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                SLABBED.appliesTo(
                        [anyStep(5, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 5, 10)
                        ]
                )
        )
    }

    @Test
    void 'slabbed global should apply when at least one step lower bound is reached by a single product selection'() {
        assertTrue(
                SLABBED_GLOBAL.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [anyProductCart(3, 5, 1)]
                )
        )
    }

    @Test
    void 'slabbed global should apply when at least one step upper bound is reached by a single product selection'() {
        assertTrue(
                SLABBED_GLOBAL.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [anyProductCart(3, 5, 3)]
                )
        )
    }

    @Test
    void 'slabbed global should apply when at last step with no upper bound is reached by a single product selection'() {
        assertTrue(
                SLABBED_GLOBAL.appliesTo(
                        [
                                anyStep(1, 5, Map.of()),
                                anyStep(5, null, Map.of())
                        ],
                        [anyProductCart(3, 5, 2)]
                )
        )
    }

    @Test
    void 'slabbed global should apply when at least one step is reached by a multiple product selection'() {
        assertTrue(
                SLABBED_GLOBAL.appliesTo(
                        [anyStep(5, 15, Map.of())],
                        [
                                anyProductCart(3, 3, 2),
                                anyProductCart(4, 4, 1)
                        ]
                )
        )
    }

    @Test
    void 'slabbed global should not apply if minimum products quantity is satisfied'() {
        assertFalse(
                SLABBED_GLOBAL.appliesTo(
                        [anyStep(5, 15, Map.of(3, 2))],
                        [
                                anyProductCart(3, 1, 1),
                                anyProductCart(4, 5, 10)
                        ]
                )
        )
    }

    private static def anyStep(Integer from, Integer to, Map<Integer, Integer> minQuantityByProducts) {
        new DiscountStep(from, to, minQuantityByProducts, 100.0, 25.0, 0.0, "ar")
    }

    private static def anyProductCart(Integer id, Integer units, Integer quantity) {
        new ProductCart(
                new ProductSearch(id: id),
                new Price(display: new Display(units: units)),
                quantity
        )
    }

}
