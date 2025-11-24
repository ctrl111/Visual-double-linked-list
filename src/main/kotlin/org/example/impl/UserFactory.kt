package org.example.impl

import org.example.interfaces.UserType
import org.example.types.IntegerType
import org.example.types.Point2DType
import org.example.types.StringType

class UserFactory {
    fun getTypeNameList(): ArrayList<String> {
        return arrayListOf("Integer", "String", "Point2D")
    }

    fun getBuilderByName(name: String): UserType? {
        return when (name) {
            "Integer" -> IntegerType()
            "String" -> StringType()
            "Point2D" -> Point2DType()
            else -> null
        }
    }
}

