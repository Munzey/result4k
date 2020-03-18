package com.natpryce.await

internal sealed class NoStackTraceException : Exception() {
    override fun fillInStackTrace(): Throwable {
        return this
    }

    object AwaitFailure : NoStackTraceException()
}
