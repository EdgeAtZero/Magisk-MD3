package com.topjohnwu.magisk.ui.settings

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

data class SampleData(
    val id: Int,
    val name: String
) {
    var con by mutableStateOf<SampleCon?>(null, neverEqualPolicy())

    val isNotEnabled by derivedStateOf { con?.isOk == true  }

}

class SampleCon(
    var isOk: Boolean
)

class SettingsViewModel(app: Application) : AndroidViewModel(app), DIAware {

    override val di by closestDI()

    val data = mutableStateListOf<String>()

    init {
        viewModelScope.launch {
            data.addAll(listOf("A", "B", "C"))
            delay(3000)
            println("GO")
            data.add("A")
        }
    }

}
