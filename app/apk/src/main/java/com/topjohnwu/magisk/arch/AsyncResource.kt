package com.topjohnwu.magisk.arch

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow

sealed class AsyncResource<out T : Any>(
    open val data: T? = null,
    open val message: String? = null
) {

    object Loading : AsyncResource<Nothing>()

    data class Failed(
        override val message: String
    ) : AsyncResource<Nothing>()

    data class Success<out T : Any>(
        override val data: T
    ) : AsyncResource<T>()

}

fun <T : Any> MutableStateFlow<AsyncResource<T>>.setLoading() {
    value = AsyncResource.Loading
}

fun <T : Any> MutableStateFlow<AsyncResource<T>>.setFailed(message: String) {
    value = AsyncResource.Failed(message)
}

fun <T : Any> MutableStateFlow<AsyncResource<T>>.setSuccess(data: T) {
    value = AsyncResource.Success(data)
}

fun <T : Any, R : Any> AsyncResource<T>.transformSuccess(block: (T) -> R): AsyncResource<R> =
    when (this) {
        AsyncResource.Loading -> AsyncResource.Loading
        is AsyncResource.Failed -> AsyncResource.Failed(this.message)
        is AsyncResource.Success<*> -> AsyncResource.Success(block(data))
    }

fun <T : Any, R : Any> AsyncResource<T>.transformFailed(block: (T) -> R): AsyncResource<R> =
    when (this) {
        AsyncResource.Loading -> AsyncResource.Loading
        is AsyncResource.Failed -> AsyncResource.Failed(this.message)
        is AsyncResource.Success<*> -> AsyncResource.Success(block(data))
    }
