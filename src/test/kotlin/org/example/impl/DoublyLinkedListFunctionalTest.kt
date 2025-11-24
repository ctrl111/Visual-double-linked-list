package org.example.impl

import org.example.interfaces.Comparator
import org.example.types.IntegerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class DoublyLinkedListFunctionalTest {

    private val comparator: Comparator = IntegerType().getTypeComparator()

    @ParameterizedTest(name = "{0}")
    @MethodSource("functionalScenarios")
    @DisplayName("functional coverage of sort inputs described in lab brief")
    fun sortMeetsFunctionalExpectations(
        @Suppress("UNUSED_PARAMETER") scenarioName: String,
        input: List<Int>
    ) {
        val list = DoublyLinkedList().apply { input.forEach { add(it) } }

        list.sort(comparator)

        assertEquals(input.sorted(), list.asIntList())
    }

    companion object {
        @JvmStatic
        fun functionalScenarios(): Stream<Arguments> = Stream.of(
            Arguments.of("all identical values", listOf(7, 7, 7, 7, 7)),
            Arguments.of("unsorted random data", listOf(3, 1, 4, 1, 5, 9, 2, 6)),
            Arguments.of("already sorted ascending", listOf(1, 2, 3, 4, 5, 6)),
            Arguments.of("reverse sorted (descending)", listOf(6, 5, 4, 3, 2, 1)),
            Arguments.of("single repeating group", listOf(2, 3, 2, 3, 2)),
            Arguments.of("multiple repeating groups", listOf(4, 4, 1, 1, 2, 2, 3, 3)),
            Arguments.of("extreme value in the middle", listOf(5, 1, 10, 2, 3)),
            Arguments.of("extreme value at the start", listOf(10, 1, 2, 3, 4)),
            Arguments.of("extreme value at the end", listOf(1, 2, 3, 4, 10)),
            Arguments.of("duplicated extremes", listOf(10, 1, 10, 2, 10, 3))
        )
    }

    private fun DoublyLinkedList.asIntList(): List<Int> =
        toArrayList().map { it as Int }
}


