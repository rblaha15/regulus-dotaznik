package cz.regulus.dotaznik

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.lang.Exception

@Single
class Repository(
    private val ctx: Context,
) {
    companion object {
        val KEY_PRIHLASEN = stringPreferencesKey("prihlasen")
        val KEY_STRANKY = stringPreferencesKey("stranky")
    }

    private val remoteConfig = Firebase.remoteConfig

    private val remoteConfigLoaded = flow {
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        supervisorScope {
            val result = try {
                remoteConfig.fetchAndActivate().await()
            } catch (e: Exception) {
                Toast.makeText(
                    ctx,
                    "Je potřeba připojení k internetu",
                    Toast.LENGTH_SHORT,
                ).show()
                throw e
            }
            emit(result)
        }
    }

    val lidi = remoteConfigLoaded.map {
        println(it)
        Json.decodeFromString<List<Clovek>>(remoteConfig["lidi"].asString()).also { println(it) }
    }
    val firmy = remoteConfigLoaded.map {
        Json.decodeFromString<List<Firma>>(remoteConfig["firmy"].asString())
    }

    private val prefs = PreferenceDataStoreFactory.create {
        ctx.preferencesDataStoreFile("prefs-DOTAZNIK")
    }

    val prihlasenState = prefs.data.map { preferences ->
        preferences[KEY_PRIHLASEN]?.let { Json.decodeFromString<PrihlasenState>(it) } ?: PrihlasenState.Odhasen
    }

    suspend fun prihlasit(uzivatel: Clovek) {
        prefs.edit {
            it[KEY_PRIHLASEN] = Json.encodeToString<PrihlasenState>(PrihlasenState.Prihlasen(uzivatel))
        }
    }

    suspend fun odhlasit() {
        prefs.edit {
            it[KEY_PRIHLASEN] = Json.encodeToString<PrihlasenState>(PrihlasenState.Odhasen)
        }
    }

    val stranky = prefs.data.map { preferences ->
        preferences[KEY_STRANKY]?.let { Json.decodeFromString<Stranky>(it) } ?: Stranky()
    }

    suspend fun upravitStranky(stranky: Stranky) {
        prefs.edit {
            it[KEY_STRANKY] = Json.encodeToString(stranky)
        }
    }
}