package me.edgeatzero.arch.util

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.reflect.typeOf

inline fun <reified T> navType(
    isNullableAllowed: Boolean = typeOf<T>().isMarkedNullable
) = typeOf<T>() to object : NavType<T>(isNullableAllowed) {

    override fun get(bundle: SavedState, key: String): T? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun put(bundle: SavedState, key: String, value: T) =
        bundle.putString(key, serializeAsValue(value))

    override fun parseValue(value: String): T =
        Json.decodeFromString(Base64.getUrlDecoder().decode(value).decodeToString())

    override fun serializeAsValue(value: T): String =
        Base64.getUrlEncoder().encodeToString(Json.encodeToString(value).encodeToByteArray())

}
