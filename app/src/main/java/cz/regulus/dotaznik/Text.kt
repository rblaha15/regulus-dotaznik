package cz.regulus.dotaznik

import android.content.Context
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Text {
    @Serializable
    @SerialName("plain")
    data class Plain(
        val value: String,
    ) : Text()

    @Serializable
    @SerialName("resource")
    data class StringRes(
        @androidx.annotation.StringRes val id: Int,
        val args: List<String> = emptyList(),
    ) : Text()

    @Serializable
    @SerialName("mix")
    class Mix(
        vararg val parts: Text,
    ) : Text()

    context(Context)
    fun asString(): String = when (this) {
        is Plain -> value
        is StringRes -> getString(id, *args.toTypedArray())
        is Mix -> (parts.joinToString("") { it.asString() })
    }
    context(Resources)
    fun asString(): String = when (this) {
        is Plain -> value
        is StringRes -> getString(id, *args.toTypedArray())
        is Mix -> (parts.joinToString("") { it.asString() })
    }
}

operator fun Text.plus(toText: Text): Text.Mix = Text.Mix(this, toText)

@Composable
fun Text.composeString(): String =
    when (this) {
        is Text.Plain -> value
        is Text.StringRes -> stringResource(id, *args.toTypedArray())
        is Text.Mix -> {
            parts.mapIndexed { _, it -> it.composeString() }.joinToString("")
        }
    }

fun Int.toText(): Text = Text.StringRes(this)
fun Int.toText(vararg args: String): Text = Text.StringRes(this, args.toList())
fun String.toText(): Text = Text.Plain(this)