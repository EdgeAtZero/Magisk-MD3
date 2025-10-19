package me.edgeatzero.arch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Suppress("PropertyName")
abstract class BaseListViewModel<T : Any> : ViewModel() {

    var error by mutableStateOf<String?>(null)
        protected set

    var isLoading by mutableStateOf(false)
        protected set

    protected abstract val _data: SnapshotStateList<T>
    open val data: List<T> = _data

    init {
        isLoading = true
        viewModelScope.launch {
            load()
            isLoading = false
        }
    }

    open fun refresh() {
        isLoading = true
        viewModelScope.launch {
            _data.clear()
            try {
                load()
            } catch (e: Exception) {
                _data.clear()
                error = e.message
            }
            isLoading = false
        }
    }

    protected abstract suspend fun CoroutineScope.load()

}
