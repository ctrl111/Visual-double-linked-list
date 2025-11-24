package org.example.interfaces

import java.io.InputStreamReader

interface UserType {
    fun typeName(): String
    fun create(): Any
    fun clone(): Any
    fun readValue(input: InputStreamReader): Any
    fun parseValue(ss: String): Any
    fun getTypeComparator(): Comparator
}

