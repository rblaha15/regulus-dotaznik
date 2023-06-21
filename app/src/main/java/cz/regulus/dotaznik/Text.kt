package cz.regulus.dotaznik

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.Serializable

@Serializable
sealed class Text {
    @Serializable
    data class Plain(
        val value: String,
    ) : Text()

    @Serializable
    data class StringRes(
        @androidx.annotation.StringRes val id: Int,
        val args: List<String> = emptyList(),
    ) : Text()

    @Serializable
    class Mix(
        vararg val parts: Text,
    ) : Text()

    context(Context)
    fun asString(): String = when (this) {
        is Plain -> value
        is StringRes -> getString(id, *args.toTypedArray())
        is Mix -> (parts.joinToString("") { it.asString() })
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
