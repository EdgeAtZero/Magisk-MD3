package me.edgeatzero.arch.util

import kotlinx.coroutines.flow.flow

fun <T> Iterable<T>.asIndexedFlow() = flow {
    forEachIndexed { index, value ->
        emit(index to value)
    }
}
