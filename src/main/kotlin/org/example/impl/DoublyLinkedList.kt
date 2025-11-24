package org.example.impl

import org.example.interfaces.Comparator
import org.example.interfaces.DoWith
import org.example.interfaces.TestIt

class DoublyLinkedList {
    
    private class Node(
        var data: Any,
        var prev: Node? = null,
        var next: Node? = null
    )

    private var head: Node? = null
    private var tail: Node? = null
    private var size: Int = 0

    fun add(data: Any) {
        val newNode = Node(data)
        if (head == null) {
            head = newNode
            tail = newNode
        } else {
            tail?.next = newNode
            newNode.prev = tail
            tail = newNode
        }
        size++
    }

    fun get(index: Int): Any? {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        }

        var current = head
        repeat(index) {
            current = current?.next
        }
        return current?.data
    }

    fun insert(index: Int, data: Any) {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        }

        if (index == size) {
            add(data)
            return
        }

        val newNode = Node(data)
        if (index == 0) {
            newNode.next = head
            head?.prev = newNode
            head = newNode
        } else {
            var current = head
            repeat(index) {
                current = current?.next
            }
            newNode.prev = current?.prev
            newNode.next = current
            current?.prev?.next = newNode
            current?.prev = newNode
        }
        size++
    }

    fun remove(index: Int) {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Index: $index, Size: $size")
        }

        when {
            size == 1 -> {
                head = null
                tail = null
            }
            index == 0 -> {
                head = head?.next
                head?.prev = null
            }
            index == size - 1 -> {
                tail = tail?.prev
                tail?.next = null
            }
            else -> {
                var current = head
                repeat(index) {
                    current = current?.next
                }
                val target = current ?: return
                target.prev?.next = target.next
                target.next?.prev = target.prev
            }
        }
        size--
    }

    fun size(): Int = size

    fun forEach(action: DoWith) {
        var current = head
        while (current != null) {
            action.doWith(current.data)
            current = current.next
        }
    }

    fun firstThat(test: TestIt): Any? {
        var current = head
        while (current != null) {
            if (test.testIt(current.data)) {
                return current.data
            }
            current = current.next
        }
        return null
    }

    fun sort(comparator: Comparator) {
        if (size <= 1) return

        head = mergeSort(head, comparator)

        tail = head
        while (tail?.next != null) {
            tail = tail?.next
        }
    }

    private fun mergeSort(head: Node?, comparator: Comparator): Node? {
        if (head?.next == null) {
            return head
        }

        val middle = getMiddle(head)
        val rightHead = middle?.next
        middle?.next = null
        rightHead?.prev = null

        val left = mergeSort(head, comparator)
        val right = mergeSort(rightHead, comparator)

        return merge(left, right, comparator)
    }

    private fun getMiddle(head: Node?): Node? {
        var slow = head
        var fast = head

        while (fast?.next?.next != null) {
            slow = slow?.next
            fast = fast.next?.next
        }

        return slow
    }

    private fun merge(left: Node?, right: Node?, comparator: Comparator): Node? {
        val dummy = Node(Any())
        var current = dummy
        var l = left
        var r = right

        while (l != null && r != null) {
            if (comparator.compare(l.data, r.data) <= 0) {
                current.next = l
                l.prev = current
                l = l.next
            } else {
                current.next = r
                r.prev = current
                r = r.next
            }
            current = current.next!!
        }

        when {
            l != null -> {
                current.next = l
                l.prev = current
            }
            r != null -> {
                current.next = r
                r.prev = current
            }
        }

        val result = dummy.next
        result?.prev = null

        return result
    }

    fun toArrayList(): ArrayList<Any?> {
        val list = ArrayList<Any?>()
        var current = head
        while (current != null) {
            list.add(current.data)
            current = current.next
        }
        return list
    }
}

