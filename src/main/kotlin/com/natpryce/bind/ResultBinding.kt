package com.natpryce.bind

import com.natpryce.Result

interface ResultBinding<E> {
    fun <T> Result<T, E>.bind(): T
}
