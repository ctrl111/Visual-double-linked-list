package org.example.impl

import org.example.interfaces.Comparator
import org.example.types.IntegerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DoublyLinkedListStructuralTest {

    private val comparator: Comparator = IntegerType().getTypeComparator()

    @Test
    fun `sort is a no-op for an empty list`() {
        val list = DoublyLinkedList()

        list.sort(comparator)

        assertEquals(0, list.size())
        assertEquals(emptyList<Int>(), list.asIntList())
    }

    @Test
    fun `sort is a no-op for a single element list`() {
        val list = buildList(42)

        list.sort(comparator)

        assertEquals(listOf(42), list.asIntList())
    }

    @Test
    fun `sort handles alternating pattern and remains idempotent`() {
        val list = buildList(5, 1, 4, 2, 3)

        list.sort(comparator)
        assertEquals(listOf(1, 2, 3, 4, 5), list.asIntList())

        list.sort(comparator) // second pass should keep order identical
        assertEquals(listOf(1, 2, 3, 4, 5), list.asIntList())
    }

    @Test
    fun `sort keeps already sorted list unchanged`() {
        val list = buildList(1, 2, 3, 4, 5)

        list.sort(comparator)

        assertEquals(listOf(1, 2, 3, 4, 5), list.asIntList())
    }

    @Test
    fun `sort correctly handles reverse sorted list`() {
        val list = buildList(5, 4, 3, 2, 1)

        list.sort(comparator)

        assertEquals(listOf(1, 2, 3, 4, 5), list.asIntList())
    }

    private fun buildList(vararg values: Int): DoublyLinkedList =
        DoublyLinkedList().apply { values.forEach { add(it) } }

    private fun DoublyLinkedList.asIntList(): List<Int> =
        toArrayList().map { it as Int }

}
