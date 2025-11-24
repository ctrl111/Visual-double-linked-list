package org.example.impl

import org.example.interfaces.Comparator
import org.example.types.IntegerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
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
    fun `bidirectional links stay consistent after sorting`() {
        val list = buildList(9, 7, 5, 3, 1, 2, 4, 6, 8)

        list.sort(comparator)

        list.assertBidirectionalIntegrity()
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 7, 8, 9), list.asIntList())
    }

    private fun buildList(vararg values: Int): DoublyLinkedList =
        DoublyLinkedList().apply { values.forEach { add(it) } }

    private fun DoublyLinkedList.asIntList(): List<Int> =
        toArrayList().map { it as Int }

    /**
     * Semi-transparent check: validate next/prev pointers via reflection.
     */
    private fun DoublyLinkedList.assertBidirectionalIntegrity() {
        val clazz = DoublyLinkedList::class.java
        val headField = clazz.getDeclaredField("head").apply { isAccessible = true }
        val tailField = clazz.getDeclaredField("tail").apply { isAccessible = true }
        val sizeField = clazz.getDeclaredField("size").apply { isAccessible = true }

        val head = headField.get(this)
        val tail = tailField.get(this)
        val expectedSize = sizeField.getInt(this)

        var count = 0
        var prevNode: Any? = null
        var currentNode = head
        while (currentNode != null) {
            count++
            val nodeClass = currentNode.javaClass
            val prevField = nodeClass.getDeclaredField("prev").apply { isAccessible = true }
            val nextField = nodeClass.getDeclaredField("next").apply { isAccessible = true }

            assertSame(prevNode, prevField.get(currentNode), "prev pointer must match for node $count")
            prevNode = currentNode
            currentNode = nextField.get(currentNode)
        }

        assertEquals(expectedSize, count, "node count should match list size")
        assertSame(prevNode, tail, "tail pointer should end on the last node")
    }
}


