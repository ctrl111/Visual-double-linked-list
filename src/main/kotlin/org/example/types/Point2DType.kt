package org.example.types

import org.example.interfaces.Comparator
import org.example.interfaces.UserType
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

class Point2DType : UserType {

    data class Point2D(val x: Double, val y: Double) {
        fun distanceFromOrigin(): Double = sqrt(x * x + y * y)

        override fun toString(): String = String.format("(%.2f,%.2f)", x, y)
    }

    override fun typeName(): String = "Point2D"

    override fun create(): Any = Point2D(0.0, 0.0)

    override fun clone(): Any = create()

    override fun readValue(input: InputStreamReader): Any {
        return try {
            val reader = BufferedReader(input)
            val line = reader.readLine()
            parseValue(line)
        } catch (e: Exception) {
            create()
        }
    }

    override fun parseValue(ss: String): Any {
        return try {
            // 支持 "(x,y)" 或 "x,y" 格式
            val clean = ss.trim()
                .replace("(", "")
                .replace(")", "")
                .replace(" ", ",")

            val parts = clean.split(",")
            if (parts.size != 2) {
                throw IllegalArgumentException("Требуется два значения координат.")
            }

            val x = parts[0].trim().toDouble()
            val y = parts[1].trim().toDouble()

            Point2D(x, y)
        } catch (e: Exception) {
            System.err.println("Ошибка парсинга 2D-точки: ${e.message}")
            create()
        }
    }

    override fun getTypeComparator(): Comparator = Point2DComparator()

    private class Point2DComparator : Comparator {
        override fun compare(o1: Any?, o2: Any?): Int {
            val p1 = o1 as Point2D
            val p2 = o2 as Point2D

            val dist1 = p1.distanceFromOrigin()
            val dist2 = p2.distanceFromOrigin()

            return dist1.compareTo(dist2)
        }
    }
}

