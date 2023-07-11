package cz.regulus.dotaznik

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import cz.regulus.dotaznik.destinations.PrihlaseniSceenDestination
import cz.regulus.dotaznik.theme.DotaznikTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val repo by inject<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val navController = rememberNavController()

            val prihlasen by repo.prihlasenState.collectAsStateWithLifecycle(null)

            if (prihlasen != null) DotaznikTheme(
                useDynamicColor = true
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    DestinationsNavHost(navController = navController, navGraph = NavGraphs.root)
                }
            }

            LaunchedEffect(prihlasen) {
                launch(Dispatchers.IO) {
                    if (prihlasen is PrihlasenState.Odhasen) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(PrihlaseniSceenDestination)
                        }
                    }
                }
            }
        }
    }
}