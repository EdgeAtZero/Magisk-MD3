package me.edgeatzero.arch.util

private val CACHED_REGEX_0 = "([a-z0-9])([A-Z])".toRegex()
private val CACHED_REGEX_1 = "([A-Z])([A-Z][a-z])".toRegex()
private const val CACHED_REPLACE = "$1_$2"

fun String.toSnakeCase(): String =
    replace(CACHED_REGEX_0, CACHED_REPLACE).replace(CACHED_REGEX_1, CACHED_REPLACE).lowercase()
