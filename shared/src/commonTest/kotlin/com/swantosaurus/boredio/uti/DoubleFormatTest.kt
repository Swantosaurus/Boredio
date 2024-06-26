package com.swantosaurus.boredio.uti

import com.swantosaurus.boredio.util.format
import kotlin.test.Test
import kotlin.test.assertEquals

class DoubleFormatTest {

    @Test
    fun testValuesAboveZero() {
        var testValue = 123.456
        var expectedValue = "123.456"
        var result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = 1.2
        expectedValue = "1.200"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        // Additional test cases
        testValue = 123.456789
        expectedValue = "123.456"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = 123.4
        expectedValue = "123.400"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = 0.123456
        expectedValue = "0.123"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = 0.1
        expectedValue = "0.100"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = 0.0
        expectedValue = "0.000"
        result = testValue.format(3)
        assertEquals(expectedValue, result)
    }


    @Test
    fun testValuesBelowZero() {
        var testValue = -123.456
        var expectedValue = "-123.456"
        var result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = -1.2
        expectedValue = "-1.200"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = -123.4
        expectedValue = "-123.400"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = -0.123456
        expectedValue = "-0.123"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = -0.1
        expectedValue = "-0.100"
        result = testValue.format(3)
        assertEquals(expectedValue, result)

        testValue = -0.0
        expectedValue = "0.000"
        result = testValue.format(3)
        assertEquals(expectedValue, result)
    }


}