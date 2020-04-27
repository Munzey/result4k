package com.natpryce.bind

import com.natpryce.Failure
import com.natpryce.Result
import com.natpryce.Success
import com.natpryce.onFailure
import com.natpryce.bind.NoStackTraceException.BindFailure

/**
 * Calls the specified function [block] with [ResultBindingImpl] as its receiver and returns a [Result] of either [Success] with value [T] or [Failure] with reason [E].
 *
 * [ResultBinding.bind] extension function is made available to any [Result] object inside this [block].
 * [ResultBinding.bind] will attempt to unwrap the [Result] to return its [Success] value locally.
 * If the result is a [Failure], then the [binding] block terminates with that [ResultBinding.bind] and returns a [Result] containing the reason of the failed binding.
 *
 * This allows for easy unwrapping of result type objects along a happy path.
 * for examples, see tests
 * @sample com.natpryce.bind.ResultBindingTest
 *
 */
inline fun <T, E> binding(crossinline block: ResultBinding<E>.() -> T): Result<T, E> {
    return ResultBindingImpl.with(block)
}

@PublishedApi
internal class ResultBindingImpl<E> : ResultBinding<E> {

    lateinit var failure: Failure<E>

    override fun <T> Result<T, E>.bind(): T {
        return this.onFailure {
            failure = it
            throw BindFailure
        }
    }

    @PublishedApi
    internal companion object {
        inline fun <T, E> with(crossinline block: ResultBindingImpl<E>.() -> T): Result<T, E> {
            val context = ResultBindingImpl<E>()
            val wrapResult: ResultBindingImpl<E>.() -> Result<T, E> = { Success(block()) }
            return try {
                with(context, wrapResult)
            } catch (e: BindFailure) {
                context.failure
            }
        }
    }
}
