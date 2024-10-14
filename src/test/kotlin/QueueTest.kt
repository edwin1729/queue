package org.example

import org.jetbrains.kotlinx.lincheck.*
import org.jetbrains.kotlinx.lincheck.annotations.*
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.*
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.*
import org.junit.jupiter.api.Test
import java.util.concurrent.*

class QueueTest {
    private val queue = Queue<Int>()

    @Operation
    fun enqueue(value: Int) = queue.enqueue(value)

    @Operation(nonParallelGroup = "consumers")
    fun dequeue() = queue.dequeue()

    // Run Lincheck in the model checking testing mode
    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)

    // Run Lincheck in the stress testing mode
    @Test
    fun stressTest(): Unit = StressOptions().check(this::class)
}