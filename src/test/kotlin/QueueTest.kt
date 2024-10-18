package org.example

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class QueueTest {
    private val queue = Queue<Int>()

    @Operation
    fun enqueue(value: Int) = queue.enqueue(value)

    @Operation(nonParallelGroup = "consumers")
    fun dequeue() = queue.dequeue()

    @Operation
    fun size() = queue.size()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)

    @Test
    fun stressTest(): Unit = StressOptions().check(this::class)
}