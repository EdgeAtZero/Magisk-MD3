package com.topjohnwu.magisk.ui.action

import com.topjohnwu.magisk.ui.navigation.MainDestination
import kotlinx.serialization.Serializable

@Serializable
data class ActionParams(
    val id: String,
    val name: String
) {
    constructor(dest: MainDestination.Action) : this(dest.id, dest.name)
}
