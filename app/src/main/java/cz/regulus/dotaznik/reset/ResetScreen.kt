package cz.regulus.dotaznik.reset

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ResetScreen(
    navigator: DestinationsNavigator
) {
    navigator.navigateUp()
}