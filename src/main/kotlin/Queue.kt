package org.example
import kotlinx.atomicfu.*
import kotlinx.atomicfu.locks.ReentrantLock;

class Queue<T> {
    private var head: Node<T> = Node(null)
    private val tail = atomic(head)
    private val lock = ReentrantLock()

    private class Node<T>(var elem: T?) {
        var next: Node<T>? = null
    }

    fun enqueue(elem: T) {
        lock.lock()
        val newTail: Node<T> = Node(null)
        var updated = false
        while (!updated) {
            val oldTail: Node<T> = tail.value
            oldTail.next = newTail
            updated = tail.compareAndSet(oldTail, newTail)
            oldTail.elem = elem
        }
        lock.unlock()
    }

    fun isEmpty(): Boolean = head.next == null

    fun dequeue(): T? {
//        if (head.elem != null) {
//            val elem = head.elem
//            head = head.next!!
//            return elem
//        } else {
//            return null
//        }
//        return head.elem?.let { elem -> head = head.next!!; elem }
            lock.lock()
        try {
            return head.elem?.let { elem -> head = head.next!!; elem }
        } finally {
            lock.unlock()
        }
    }
}