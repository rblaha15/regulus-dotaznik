package com.regulus.dotaznik

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class Text {
    data class Plain(
        val value: String,
    ) : Text()

    data class StringRes(
        @androidx.annotation.StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : Text()

    class Mix(
        vararg val parts: Text,
    ) : Text()

    fun asString(context: Context): String =
        when (this) {
            is Plain -> value
            is StringRes -> context.getString(id, *args.toTypedArray())
            is Mix -> (parts.joinToString("") { it.asString(context) })
        }

    @Composable
    fun asString(): String =
        when (this) {
            is Plain -> value
            is StringRes -> stringResource(id, *args.toTypedArray())
            is Mix -> {
                parts.mapIndexed { _, it -> it.asString() }.joinToString("")
            }
        }

    companion object {
        val Int.text: Text get() = StringRes(this)
    }
}
