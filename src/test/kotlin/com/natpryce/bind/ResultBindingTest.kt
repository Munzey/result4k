package com.natpryce.bind

import com.natpryce.Failure
import com.natpryce.Success
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.natpryce.Result

class ResultBindingTest {

    @Test
    fun `GIVEN a ResultBinding which contains all successful binds THEN return successful Result`() {
        val result = binding<Int, Exception> {
            val x = successWithInt().bind()
            val y = successWithInt().bind()
            x + y
        }

        assertTrue(result is Success)
        assertEquals(2, result.value)
    }

    @Test
    fun `GIVEN a ResultBinding which contains all successful binds that are not of the same type WHEN the returning type matches the ResultBinding type THEN return successful Result`() {
        val result = binding<Int, Exception> {
            val x = successWithString().bind()
            val y = Success(x.toInt() + 2).bind()
            y
        }

        assertTrue(result is Success)
        assertEquals(3, result.value)
    }

    @Test
    fun `GIVEN a ResultBinding which contains a failing bind THEN return that bind's failure reason in Result`() {
        val someException = Exception()
        val result = binding<Int, Exception> {
            val x = successWithInt().bind()
            val e = failureWithInt(someException).bind()
            val y = successWithInt().bind()
            x + y
        }

        assertTrue(result is Failure)
        assertEquals(someException, result.reason)
    }

    @Test
    fun `GIVEN a ResultBinding which contains a function with a success type not matching the ResultBinding WHEN it fails to bind THEN return that bind's failure reason in Result`() {
        val someException = Exception()
        val result = binding<Int, Exception> {
            val x = successWithInt().bind()
            val e = failureWithString(someException).bind()
            val y = successWithInt().bind()
            x + y
        }

        assertTrue(result is Failure)
        assertEquals(someException, result.reason)
    }

    companion object {
        fun successWithInt(): Result<Int, Exception> = Success(1)
        fun successWithString(): Result<String, Exception> = Success("1")
        fun failureWithInt(exception: Exception): Result<Int, Exception> = Failure(exception)
        fun failureWithString(exception: Exception): Result<String, Exception> = Failure(exception)
    }
}
