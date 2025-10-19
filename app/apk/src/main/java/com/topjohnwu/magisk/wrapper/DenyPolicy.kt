package com.topjohnwu.magisk.wrapper

data class DenyPolicy(
    val packageName: String,
    val process: List<String>
)
