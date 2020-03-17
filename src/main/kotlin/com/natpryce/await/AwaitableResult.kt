package com.natpryce.await

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.onFailure
import com.natpryce.await.NoStackTraceException.AwaitFailure

class AwaitableResult<E> {

    private lateinit var failure: Failure<E>

    fun <T> Result<T, E>.await(): T {
        return this.onFailure {
            failure = it
            throw AwaitFailure
        }
    }

    companion object {
        fun <T, E> with(successBlock: AwaitableResult<E>.() -> T): Result<T, E> {
            val context = AwaitableResult<E>()
            val wrapResult: AwaitableResult<E>.() -> Result<T, E> = { Success(successBlock()) }
            return try {
                wrapResult(context)
            } catch (e: AwaitFailure) {
                context.failure
            }
        }
    }
}