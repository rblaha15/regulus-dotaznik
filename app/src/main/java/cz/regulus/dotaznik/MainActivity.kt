package cz.regulus.dotaznik

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
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

//    companion object {
//        const val VERZE: Int = 4310
//    }

//    override fun onResume() {
//        super.onResume()
//
//        prefsPrihlaseni.edit {
//            putInt("verze", VERZE)
//        }
//    }

    private val repo by inject<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        if (prefsPrihlaseni.getInt("verze", 0) < VERZE) {
//            prefsPrihlaseni.edit {
//                putBoolean("prihlasen", false)
//            }
//        }

        setContent {
            val navController = rememberNavController()

            val prihlasen by repo.prihlasenState.collectAsStateWithLifecycle(false)

            CompositionLocalProvider(LocalMainActivity provides this) {
                DotaznikTheme(
                    useDynamicColor = false
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        DestinationsNavHost(navController = navController, navGraph = NavGraphs.root)
                    }
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

val LocalMainActivity = staticCompositionLocalOf<MainActivity> {
    error("CompositionLocal LocalMainActivity not present")
}