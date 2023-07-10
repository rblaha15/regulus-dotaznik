package cz.regulus.dotaznik.spravaFotek

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.regulus.dotaznik.R
import cz.regulus.dotaznik.Repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class FotkyViewModel(
    private val repo: Repository,
    private val launchers: Launchers,
) : ViewModel() {

    data class Launchers(
        inline val getTakePicture: ((Boolean) -> Unit) -> ManagedActivityResultLauncher<Uri, Boolean>,
        inline val getPickMultipleMedia: ((List<Uri>) -> Unit) -> ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>,
    )

    val fotky = repo.fotky
        .map {
            it.map { (id, file) ->
                id to BitmapFactory.decodeFile(file.path).asImageBitmap()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    fun vyfotit(
        chyba: (Int) -> Unit,
    ) {
        viewModelScope.launch {
            val pocetDovolenychFotek = repo.pocetDovolenychFotek()

            if (pocetDovolenychFotek <= 0) {
                chyba(R.string.fotky_maximalne_fotek)
                return@launch
            }

            val (id, newUri) = repo.uriIdNoveFotky()

            val takePicture = launchers.getTakePicture {
                viewModelScope.launch {
                    repo.pridalJsemFoto(id)
                }
            }

            takePicture.launch(newUri)
        }
    }

    fun vybrat(
        chyba: (Int) -> Unit,
    ) {
        viewModelScope.launch {
            val pocetDovolenychFotek = repo.pocetDovolenychFotek()

            if (pocetDovolenychFotek <= 0) {
                chyba(R.string.fotky_maximalne_fotek)
                return@launch
            }

            val pickMultipleMedia = launchers.getPickMultipleMedia { uris ->
                val prekrocenoO = (uris.size - pocetDovolenychFotek).let { if (it < 0) 0 else it }

                if (prekrocenoO > 0) chyba(R.string.fotky_maximalne_fotek_presazeno)

                viewModelScope.launch {
                    uris.dropLast(prekrocenoO).forEach { uri ->
                        repo.prekopirovat(uri)
                    }
                }
            }

            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    fun odebrat(
        id: Int,
    ) {
        viewModelScope.launch {
            repo.odebratFoto(id)
        }
    }
}