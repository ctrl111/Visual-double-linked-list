package org.example.types

import org.example.interfaces.Comparator
import org.example.interfaces.UserType
import java.io.BufferedReader
import java.io.InputStreamReader

class StringType : UserType {
    override fun typeName(): String = "String"

    override fun create(): Any = ""

    override fun clone(): Any = create()

    override fun readValue(input: InputStreamReader): Any {
        return try {
            val reader = BufferedReader(input)
            reader.readLine() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    override fun parseValue(ss: String): Any = ss

    override fun getTypeComparator(): Comparator = StringComparator()

    private class StringComparator : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            val s1 = o1 as String
            val s2 = o2 as String
            return s1.compareTo(s2)
        }
    }
}

