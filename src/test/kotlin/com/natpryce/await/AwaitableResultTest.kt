package com.natpryce.await

import com.natpryce.Failure
import com.natpryce.Success
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.natpryce.Result

class AwaitableResultTest {

    @Test
    fun `GIVEN an AwaitableResult which contains all successes WHEN awaiting THEN return successful Result`() {
        val result = AwaitableResult.with<Int, Exception> {
            val x = successWithInt().await()
            val y = successWithInt().await()
            x + y
        }

        assertTrue(result is Success)
        assertEquals(2, result.value)
    }

    @Test
    fun `GIVEN an AwaitableResult which contains all successes that are not of the same type WHEN the returning type matches the AwaitableResult type THEN return successful Result`() {
        val result = AwaitableResult.with<Int, Exception> {
            val x = successWithString().await()
            val y = Success(x.toInt() + 2).await()
            y
        }

        assertTrue(result is Success)
        assertEquals(3, result.value)
    }

    @Test
    fun `GIVEN an AwaitableResult which contains a failing await WHEN evaluating THEN return that failure Result`() {
        val someException = Exception()
        val result = AwaitableResult.with<Int, Exception> {
            val x = successWithInt().await()
            val e = failureWithInt(someException).await()
            val y = successWithInt().await()
            x + y
        }

        assertTrue(result is Failure)
        assertEquals(someException, result.reason)
    }

    @Test
    fun `GIVEN an AwaitableResult which contains a result that will fail on await with a wrong success type WHEN evaluating THEN return that failure Result`() {
        val someException = Exception()
        val result = AwaitableResult.with<Int, Exception> {
            val x = successWithInt().await()
            val e = failureWithString(someException).await()
            val y = successWithInt().await()
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
