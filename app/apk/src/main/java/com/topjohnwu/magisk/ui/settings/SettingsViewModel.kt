package com.topjohnwu.magisk.ui.settings

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

class SettingsViewModel : ViewModel() {

    val data = SnapshotStateList<SampleData>(10) { SampleData(it, "INDEX: $it") }

    init {
        viewModelScope.launch {
            delay(3000)
            val con = SampleCon(isOk = true)
            data[2].con = con
            println("HIDE")
            delay(3000)
            con.isOk = false
            data[2].con = con
            println("SHOW")
            delay(3000)
            data[2].con = SampleCon(isOk = true)
            println("SHOW NOW")
        }
    }

}
