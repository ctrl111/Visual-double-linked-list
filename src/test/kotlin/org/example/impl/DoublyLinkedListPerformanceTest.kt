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
        val sizes = listOf(5_000, 2_0000, 4_0000, 8_0000)
        val samples = sizes.map { size ->
            val data = IntArray(size) { Random.nextInt(0, size) }

            // 多次重复排序以获得“脏时间”（包含系统抖动的实际耗时）
            val repeats = 5
            val nanosPerRun = (1..repeats).map { runIndex ->
                val listForRun = DoublyLinkedList().apply { data.forEach { add(it) } }
                val nanos = measureNanoTime { listForRun.sort(comparator) }
                assertEquals(
                    data.sorted().toList(),
                    listForRun.asIntList(),
                    "sorted content mismatch for size=$size run=$runIndex"
                )
                nanos
            }
            val avgMillis = (nanosPerRun.average() / 1_000_000.0).toLong()
            val maxMillis = (nanosPerRun.maxOrNull() ?: 0L) / 1_000_000

            // 单独构造一份用于测量排序前后堆内存占用的列表
            val memoryList = DoublyLinkedList().apply { data.forEach { add(it) } }
            val runtime = Runtime.getRuntime()
            runtime.gc()
            val memoryBefore = runtime.totalMemory() - runtime.freeMemory()

            measureNanoTime { memoryList.sort(comparator) } // 这一次主要用于触发内存分配

            val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
            val memoryDelta = max(0L, memoryAfter - memoryBefore)

            PerfSample(
                size = size,
                avgMillis = avgMillis,
                maxMillis = maxMillis,
                memoryBytes = memoryDelta,
                nodeCount = memoryList.size()
            )
        }

        samples.forEach {
            println(
                "size=${it.size}, avgTime=${it.avgMillis} ms, " +
                    "maxTime=${it.maxMillis} ms, memory=${it.memoryBytes} bytes, nodes=${it.nodeCount}"
            )
        }

        // 简单检查平均时间随规模近似单调不减
        assertTrue(samples.zipWithNext().all { (prev, next) -> next.avgMillis >= prev.avgMillis })
    }

    private data class PerfSample(
        val size: Int,
        val avgMillis: Long,
        val maxMillis: Long,
        val memoryBytes: Long,
        val nodeCount: Int
    )

    private fun DoublyLinkedList.asIntList(): List<Int> =
        toArrayList().map { it as Int }
}


