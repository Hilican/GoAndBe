package com.github.hilican.goandbe

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val x = 0
        var y = false
        if (x in 1..10) {
            y = true
        }
        for (z in 9 downTo 0 step 3) {
            print(z)
        }
        print("\n")
        val i = 10
        val s = "Kotlin"
        println("i = $i")
        println("Length of $s is ${s.length}")
        assertEquals(true, y)
    }
}