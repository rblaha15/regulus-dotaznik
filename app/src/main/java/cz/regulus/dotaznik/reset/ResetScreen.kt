package cz.regulus.dotaznik.reset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@Destination
fun ResetScreen(
    navigator: DestinationsNavigator
) {
    LaunchedEffect(Unit) {
        delay(500)
        launch(Dispatchers.Main) {
            navigator.navigateUp()
        }
    }
}