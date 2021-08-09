package bff.configuration

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.class)
class ConfigUtilsTest {

    private static final String HIDDEN_FIELD_VALUE = "*****"

    @Test
    void getHiddenValueByKeyHidesPasswordTest() {
        List<String> hiddenAttrs = ["password"]
        def variables = [
                input: [
                        Password: "value1",
                        password: "valueToHide",
                        passworD: "value2"
                ]
        ]
        def value = ConfigUtils.getHiddenValueByKey(hiddenAttrs, variables.entrySet().iterator().next())

        def expected = [
                Password: "value1",
                password: HIDDEN_FIELD_VALUE,
                passworD: "value2"
        ]

        Assert.assertEquals(expected, value)
    }

    @Test
    void getHiddenValueByKeyHidesMultipleFieldsTest() {
        List<String> hiddenAttrs = ["password", "currentPassword", "newPassword"]
        def variables = [
                input: [
                        anotherInput: [
                                field1: "value1",
                                password: "valueToHide",
                                lastInput: [
                                        currentPassword: "valueToHide",
                                        newPassword: "valueToHide",
                                        field2: "value2"
                                ]
                        ]
                ]
        ]
        def value = ConfigUtils.getHiddenValueByKey(hiddenAttrs, variables.entrySet().iterator().next())

        def expected = [
                anotherInput: [
                        field1: "value1",
                        password: HIDDEN_FIELD_VALUE,
                        lastInput: [
                                currentPassword: HIDDEN_FIELD_VALUE,
                                newPassword: HIDDEN_FIELD_VALUE,
                                field2: "value2"
                        ]
                ]
        ]

        Assert.assertEquals(expected, value)
    }

    @Test
    void getHiddenValueByKeyHidesPasswordSingleVariableTest() {
        List<String> hiddenAttrs = ["password"]
        def variables = [
                password: "valueToHide"
        ]
        def value = ConfigUtils.getHiddenValueByKey(hiddenAttrs, variables.entrySet().iterator().next())

        def expected = HIDDEN_FIELD_VALUE

        Assert.assertEquals(expected, value)
    }

    @Test
    void getHiddenValueByKeyHidesSingleVariableMapTest() {
        List<String> hiddenAttrs = ["password"]
        def variables = [
                password: [
                        "attrKey": "attrValue"
                ]
        ]
        def value = ConfigUtils.getHiddenValueByKey(hiddenAttrs, variables.entrySet().iterator().next())

        def expected = HIDDEN_FIELD_VALUE

        Assert.assertEquals(expected, value)
    }
}
