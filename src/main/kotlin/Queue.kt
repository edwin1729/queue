package org.example

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

// Implementation notes:
// In dequeue(), the size == 1 case is the reason a linearizable implementation with only atomics,
// that is without locks or spin loops, is not possible
// Multiple producers can enqueue concurrently by shifting an atomic tail pointer, and setting the next
// pointer could happen asynchronously. However, this would break linearizability, since dequeue might sometimes
// return null, if the next pointer has not yet been set

// In summary, a faster non-linearizable queue is, I believe, still acceptable
class Queue<T> : SynchronizedObject() {
    private var head: Node<T> = Node(null) // There is always a null node at the end ready to be assigned on enqueue
    private var tail = head
    private val size = atomic(0)

    private class Node<T>(var elem: T?) {
        var next: Node<T>? = null
    }

    fun enqueue(elem: T) {
        val newTail: Node<T> = Node(null)
        synchronized(this) {
            size.incrementAndGet()
            tail.elem = elem
            tail.next = newTail
            tail = newTail
        }
    }

    fun size(): Int {
        return size.value
    }

    // precondition: size > 0 and synchronization for size == 1
    private fun unsafeDequeue(): T {
        size.decrementAndGet()
        val ret = head.elem!! // size > 0 guarantees existence
        head = head.next!! // same
        return ret
    }

    fun dequeue(): T? {
        if (size.value == 0) {
            return null
        }

        return if (size.value == 1) {
            synchronized(this) {
                return@synchronized unsafeDequeue()
            }
        } else {
            unsafeDequeue()
        }
    }
}
