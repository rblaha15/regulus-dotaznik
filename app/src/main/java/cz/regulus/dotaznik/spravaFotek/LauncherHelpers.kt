package cz.regulus.dotaznik.spravaFotek

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

fun interface GenericActivityResultLauncher<I, O> {
    fun launch(input: I, callback: ActivityResultCallback<O>)
}

@Composable
@Suppress("UNCHECKED_CAST")
fun <I, O> rememberResultLauncher(contract: ActivityResultContract<I, O>): GenericActivityResultLauncher<I, O> {
    var i = rememberSaveable { 0 }

    val launcher = rememberLauncherForActivityResult(contract) {
        val cb = callbacks[i] as ActivityResultCallback<O>
        cb.onActivityResult(it)
    }

    return GenericActivityResultLauncher { input, callback ->
        i = callbacks.size
        callbacks.add(callback as ActivityResultCallback<*>)
        launcher.launch(input)
    }
}

val callbacks = mutableListOf<ActivityResultCallback<*>>()