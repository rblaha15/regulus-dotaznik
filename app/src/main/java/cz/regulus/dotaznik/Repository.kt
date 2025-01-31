package cz.regulus.dotaznik

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import cz.regulus.dotaznik.dotaznik.Company
import cz.regulus.dotaznik.dotaznik.Sites
import cz.regulus.dotaznik.prihlaseni.Employee
import cz.regulus.dotaznik.strings.strings
import io.github.z4kn4fein.semver.toVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.io.File
import java.io.IOException
import java.net.URL


@Single
class Repository(
    private val ctx: Context,
) {
    companion object {
        private val KEY_LOGGED_IN = stringPreferencesKey("loggedIn")
        private val KEY_FIRST_START = booleanPreferencesKey("poprve")
        private val KEY_SITES = stringPreferencesKey("stranky")
        private val KEY_PHOTOS = stringSetPreferencesKey("fotkyIds")

        private const val MAX_PHOTO_AMOUNT = 5


        private val json = Json {
            ignoreUnknownKeys = true
        }

        private inline fun <reified T> T.toJson() = json.encodeToString(this)
        private inline fun <reified T> String.fromJson() = try {
            json.decodeFromString<T>(this)
        } catch (e: SerializationException) {
            null
        }
    }

    val isDebug = BuildConfig.DEBUG || '-' in BuildConfig.VERSION_NAME

    private val remoteConfig = Firebase.remoteConfig

    private val isRemoteConfigLoaded = flow {
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
                        strings.internetConnectionNeeded,
                        Toast.LENGTH_SHORT,
                    ).show()
                    throw e
                }
            }
            emit(result)
        }
    }

    val people = isRemoteConfigLoaded.map {
        remoteConfig["lidi"].asString().fromJson<List<Employee>>()!!
    }
    val companies = isRemoteConfigLoaded.map {
        remoteConfig["firmy"].asString().fromJson<List<Company>>()!!
    }
    private val products = isRemoteConfigLoaded.map {
        remoteConfig["products"].asString().fromJson<Products>()!!
    }

    private val prefs = PreferenceDataStoreFactory.create {
        ctx.preferencesDataStoreFile("prefs-DOTAZNIK")
    }

    val firstStart = prefs.data.map { preferences ->
        preferences[KEY_FIRST_START] ?: true
    }

    suspend fun started() {
        prefs.edit {
            it[KEY_FIRST_START] = false
        }
    }

    val authenticationState = prefs.data.map { preferences ->
        preferences[KEY_LOGGED_IN]?.fromJson<AuthenticationState>() ?: AuthenticationState.LoggedOut
    }

    suspend fun logIn(user: User) {
        prefs.edit {
            it[KEY_LOGGED_IN] = AuthenticationState.LoggedIn(user).toJson<AuthenticationState>()
        }
    }

    suspend fun logOut() {
        prefs.edit {
            it[KEY_LOGGED_IN] = AuthenticationState.LoggedOut.toJson<AuthenticationState>()
        }
    }

    val sites = prefs.data.map { preferences ->
        preferences[KEY_SITES]?.fromJson<Sites>() ?: Sites()
    }.combine(products) { sites, products ->
        sites with products
    }

    suspend fun editSites(sites: Sites) {
        prefs.edit {
            it[KEY_SITES] = sites.toJson()
        }
    }

    private val photoIds = prefs.data.map { preferences ->
        (preferences[KEY_PHOTOS] ?: setOf()).toList().map { it.toInt() }.sorted()
    }

    private suspend fun newPhotoId() = photoIds.first().maxOrNull()?.plus(1) ?: 0

    private fun MutablePreferences.addPhotoId(id: Int) {
        this[KEY_PHOTOS] = this[KEY_PHOTOS].orEmpty().toList().plus(id.toString()).sortedBy { it.toInt() }.toSet()
    }

    private fun MutablePreferences.removePhotoId(id: Int) {
        this[KEY_PHOTOS] = this[KEY_PHOTOS].orEmpty().toList().minus(id.toString()).sortedBy { it.toInt() }.toSet()
    }

    val photos = photoIds.map { photoIds ->
        photoIds.map { id ->
            id to File(ctx.filesDir, "photo${id}.jpg")
        }
    }

    suspend fun copyPhotoTuInternalStorage(
        uri: Uri,
    ) {
        prefs.edit { preferences ->
            val newPhotoCount = (preferences[KEY_PHOTOS]?.size ?: 0) + 1
            require(newPhotoCount <= MAX_PHOTO_AMOUNT)
            val newId = newPhotoId()

            ctx.contentResolver.openInputStream(uri)!!.use { input ->
                File(ctx.filesDir, "photo${newId}.jpg").outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            preferences.addPhotoId(newId)
        }
    }

    suspend fun registerTakenPhoto(id: Int) {
        prefs.edit { preferences ->
            val newPhotoCount = (preferences[KEY_PHOTOS]?.size ?: 0) + 1
            assert(newPhotoCount <= MAX_PHOTO_AMOUNT)

            assert(File(ctx.filesDir, "photo${id}.jpg").exists())
            preferences.addPhotoId(id)
        }
    }

    suspend fun removePhoto(
        id: Int,
    ) {
        prefs.edit { preferences ->
            val file = File(ctx.filesDir, "photo${id}.jpg")
            file.delete()
            preferences.removePhotoId(id)
        }
    }

    suspend fun getPhotosForExport(): List<File> {
        return photoIds.first().mapIndexed { i, id ->
            val oldFile = File(ctx.filesDir, "photo${id}.jpg")
            val newFile = File(ctx.filesDir, "fotka ${i + 1}.jpg")
            if (newFile.exists()) newFile.delete()
            oldFile.copyTo(newFile)
            newFile
        }
    }

    suspend fun deleteAllPhotos() {
        photos.first().forEach {
            it.second.delete()
        }

        prefs.edit {
            it[KEY_PHOTOS] = emptySet()
        }
    }

    suspend fun getIdAndUriOfNewPhoto(): Pair<Int, Uri> {
        val newCount = (prefs.data.first()[KEY_PHOTOS]?.size ?: 0) + 1
        require(newCount <= MAX_PHOTO_AMOUNT)
        val newId = newPhotoId()

        val newFile = File(ctx.filesDir, "photo${newId}.jpg")
        return newId to FileProvider.getUriForFile(ctx, "${ctx.packageName}.provider", newFile)
    }

    suspend fun remainingPhotoSlots(): Int {
        val count = prefs.data.first()[KEY_PHOTOS]?.size ?: 0
        return MAX_PHOTO_AMOUNT - count
    }

    private suspend fun isAppUpdateNeeded(): Boolean {
        if (isDebug) return false

        val text = try {
            withContext(Dispatchers.IO) {
                URL("https://raw.githubusercontent.com/rblaha15/regulus-dotaznik/main/app/version.txt").openConnection().run {
                    doInput = true
                    getInputStream().bufferedReader().readLine() ?: "0.0.0"
                }
            }
        } catch (e: IOException) {
            Firebase.crashlytics.recordException(e)
            return false
        }

        val localVersion = BuildConfig.VERSION_NAME.toVersion(false)
        val newestVersion = text.toVersion(false)

        return localVersion < newestVersion
    }

    val isAppUpdateNeeded = flow {
        emit(isAppUpdateNeeded())
    }
}

infix fun Sites.with(products: Products) = copy(products = products)