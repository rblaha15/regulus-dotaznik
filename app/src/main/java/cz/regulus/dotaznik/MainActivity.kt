package cz.regulus.dotaznik

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import cz.regulus.dotaznik.destinations.LogInScreenDestination
import cz.regulus.dotaznik.theme.DotaznikTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.io.IOException
import java.net.URL


class MainActivity : AppCompatActivity() {
    private val repo by inject<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val updateApp = {
            lifecycle.coroutineScope.launch(Dispatchers.IO) {
                val text = try {
                    withContext(Dispatchers.IO) {
                        URL("https://raw.githubusercontent.com/rblaha15/regulus-dotaznik/main/app/version.txt").openConnection().run {
                            doInput = true
                            getInputStream().bufferedReader().readLine() ?: "0.0.0"
                        }
                    }
                } catch (e: IOException) {
                    Firebase.crashlytics.recordException(e)
                    return@launch
                }

                CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .build()
                    .launchUrl(
                        this@MainActivity,
                        Uri.parse("https://github.com/rblaha15/regulus-dotaznik/releases/download/v$text/Dotaznik-$text.apk")
                    )
            }
            Unit
        }

        setContent {
            val navController = rememberNavController()

            val authState by repo.authenticationState.collectAsStateWithLifecycle(null)

            if (authState != null) DotaznikTheme(
                useDynamicColor = true
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface
                ) {
                    val isAppUpdateNeeded by repo.isAppUpdateNeeded.collectAsStateWithLifecycle(false)
                    if (isAppUpdateNeeded) {
                        var zobrazitDialog by remember { mutableStateOf(true) }

                        if (zobrazitDialog) AlertDialog(
                            onDismissRequest = {
                                zobrazitDialog = false
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        zobrazitDialog = false
                                        updateApp()
                                    }
                                ) {
                                    Text("Ano")
                                }
                            },
                            title = {
                                Text("Aktualizace aplikace")
                            },
                            text = {
                                Text("Je k dispozici nová verze aplikace, chcete ji aktualizovat?")
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        zobrazitDialog = false
                                    }
                                ) {
                                    Text("Ne")
                                }
                            },
                        )
                    }

                    DestinationsNavHost(navController = navController, navGraph = NavGraphs.root)
                }
            }

            LaunchedEffect(authState) {
                launch(Dispatchers.IO) {
                    if (authState is AuthenticationState.LoggedOut) {
                        withContext(Dispatchers.Main) {
                            navController.navigate(LogInScreenDestination)
                        }
                    }
                }
            }
        }
    }
}