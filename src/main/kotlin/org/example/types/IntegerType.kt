package org.example.types

import org.example.interfaces.Comparator
import org.example.interfaces.UserType
import java.io.BufferedReader
import java.io.InputStreamReader

class IntegerType : UserType {
    override fun typeName(): String = "Integer"

    override fun create(): Any = 0

    override fun clone(): Any = create()

    override fun readValue(input: InputStreamReader): Any {
        return try {
            val reader = BufferedReader(input)
            val line = reader.readLine()
            parseValue(line)
        } catch (e: Exception) {
            0
        }
    }

    override fun parseValue(ss: String): Any {
        return try {
            ss.trim().toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    override fun getTypeComparator(): Comparator = IntegerComparator()

    private class IntegerComparator : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            val i1 = o1 as Int
            val i2 = o2 as Int
            return i1.compareTo(i2)
        }
    }
}

