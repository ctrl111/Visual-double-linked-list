package org.example.impl

import org.example.interfaces.Comparator
import org.example.types.IntegerType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.random.Random
import kotlin.system.measureNanoTime

class DoublyLinkedListPerformanceTest {

    private val comparator: Comparator = IntegerType().getTypeComparator()

    @Test
    fun `performance profile across growing datasets`() {
        val sizes = listOf(100, 1_000, 5_000, 10_000)
        val samples = sizes.map { size ->
            val data = IntArray(size) { Random.nextInt(0, size) }
            val list = DoublyLinkedList().apply { data.forEach { add(it) } }

            val runtime = Runtime.getRuntime()
            runtime.gc()
            val memoryBefore = runtime.totalMemory() - runtime.freeMemory()

            val nanos = measureNanoTime { list.sort(comparator) }

            val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
            val memoryDelta = max(0L, memoryAfter - memoryBefore)

            assertEquals(data.sorted().toList(), list.asIntList(), "sorted content mismatch for size=$size")

            PerfSample(
                size = size,
                millis = nanos / 1_000_000,
                memoryBytes = memoryDelta,
                nodeCount = list.size()
            )
        }

        samples.forEach {
            println("size=${it.size}, time=${it.millis} ms, memory=${it.memoryBytes} bytes, nodes=${it.nodeCount}")
        }

        assertTrue(samples.zipWithNext().all { (prev, next) -> next.millis >= prev.millis })
    }

    private data class PerfSample(
        val size: Int,
        val millis: Long,
        val memoryBytes: Long,
        val nodeCount: Int
    )

    private fun DoublyLinkedList.asIntList(): List<Int> =
        toArrayList().map { it as Int }
}


