package cz.regulus.dotaznik

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import cz.regulus.dotaznik.dotaznik.Firma
import cz.regulus.dotaznik.dotaznik.Stranky
import cz.regulus.dotaznik.prihlaseni.Zamestnanec
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File

@Single
class Repository(
    private val ctx: Context,
) {
    companion object {
        private val KEY_PRIHLASEN = stringPreferencesKey("prihlasen")
        private val KEY_STRANKY = stringPreferencesKey("stranky")
        private val KEY_FOTKY = stringSetPreferencesKey("fotkyIds")

        private const val MAX_POCET_FOTEK = 5
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
                try {
                    remoteConfig.activate().await()
                } catch (e: Exception) {
                    Toast.makeText(
                        ctx,
                        "Je potřeba připojení k internetu",
                        Toast.LENGTH_SHORT,
                    ).show()
                    throw e
                }
            }
            emit(result)
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val lidi = remoteConfigLoaded.map {
        json.decodeFromString<List<Zamestnanec>>(remoteConfig["lidi"].asString())
    }
    val firmy = remoteConfigLoaded.map {
        json.decodeFromString<List<Firma>>(remoteConfig["firmy"].asString())
    }

    private val prefs = PreferenceDataStoreFactory.create {
        ctx.preferencesDataStoreFile("prefs-DOTAZNIK")
    }

    val prihlasenState = prefs.data.map { preferences ->
        preferences[KEY_PRIHLASEN]?.let { json.decodeFromString<PrihlasenState>(it) } ?: PrihlasenState.Odhasen
    }

    suspend fun prihlasit(uzivatel: Uzivatel) {
        prefs.edit {
            it[KEY_PRIHLASEN] = json.encodeToString<PrihlasenState>(PrihlasenState.Prihlasen(uzivatel))
        }
    }

    suspend fun odhlasit() {
        prefs.edit {
            it[KEY_PRIHLASEN] = json.encodeToString<PrihlasenState>(PrihlasenState.Odhasen)
        }
    }

    val stranky = prefs.data.map { preferences ->
        preferences[KEY_STRANKY]?.let { json.decodeFromString<Stranky>(it) } ?: Stranky()
    }

    suspend fun upravitStranky(stranky: Stranky) {
        prefs.edit {
            it[KEY_STRANKY] = json.encodeToString(stranky)
        }
    }

    private val photoIds = prefs.data.map { preferences ->
        (preferences[KEY_FOTKY] ?: setOf()).toList().map { it.toInt() }
    }

    private suspend fun newPhotoId() = photoIds.first().maxOrNull()?.plus(1) ?: 0

    private fun MutablePreferences.pridatId(id: Int) {
        this[KEY_FOTKY] = (this[KEY_FOTKY] ?: setOf()).toList().plus(id.toString()).toSet()
    }
    private fun MutablePreferences.odebratId(id: Int) {
        this[KEY_FOTKY] = (this[KEY_FOTKY] ?: setOf()).toList().plus(id.toString()).toSet()
    }

    val fotky = photoIds.map { photoIds ->
        photoIds.map { id ->
            id to File(ctx.filesDir, "photo${id}.jpg")
        }
    }

    suspend fun prekopirovat(
        uri: Uri,
    ) {
        prefs.edit { preferences ->
            val novyPocet = (preferences[KEY_FOTKY]?.size ?: 0) + 1
            require(novyPocet <= MAX_POCET_FOTEK)
            val newId = newPhotoId()

            ctx.contentResolver.openInputStream(uri)!!.use { input ->
                File(ctx.filesDir, "photo${newId}.jpg").outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            preferences.pridatId(newId)
        }
    }

    suspend fun pridalJsemFoto(id: Int) {
        prefs.edit { preferences ->
            val novyPocet = (preferences[KEY_FOTKY]?.size ?: 0) + 1
            assert(novyPocet <= MAX_POCET_FOTEK)

            assert(File(ctx.filesDir, "photo${id}.jpg").exists())
            preferences.pridatId(id)
        }
    }

    suspend fun odebratFoto(
        id: Int,
    ) {
        prefs.edit { preferences ->

            val file = File(ctx.filesDir, "photo${id}.jpg")
            file.delete()
            preferences.odebratId(id)
        }
    }

    suspend fun pripravitFotkyNaExport() {
        photoIds.first().forEachIndexed { i, id ->
            val oldFile = File(ctx.filesDir, "photo${id}.jpg")
            val newFile = File(ctx.filesDir, "fotka ${i + 1}.jpg")
            if (newFile.exists()) newFile.delete()
            oldFile.copyTo(newFile)
        }
    }

    suspend fun odstranitVsechnyFotky() {
        fotky.first().forEach {
            it.second.delete()
        }

        prefs.edit {
            it[KEY_FOTKY] = emptySet()
        }

    }

    suspend fun uriIdNoveFotky(): Pair<Int, Uri> {
        val novyPocet = (prefs.data.first()[KEY_FOTKY]?.size ?: 0) + 1
        require(novyPocet <= MAX_POCET_FOTEK)
        val newId = newPhotoId()

        val newFile = File(ctx.filesDir, "photo${newId}.jpg")
        return newId to FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", newFile)
    }

    suspend fun pocetDovolenychFotek(): Int {
        val pocet = prefs.data.first()[KEY_FOTKY]?.size ?: 0
        return MAX_POCET_FOTEK - pocet
    }
}