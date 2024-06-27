package cz.regulus.dotaznik.spravaFotek

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.strings.GenericStringsProvider
import cz.regulus.dotaznik.strings.strings
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
        val takePicture: GenericActivityResultLauncher<Uri, Boolean>,
        val pickMultipleMedia: GenericActivityResultLauncher<PickVisualMediaRequest, List<Uri>>,
    )

    val fotky = repo.fotky
        .map {
            it.map { (id, file) ->
                id to BitmapFactory.decodeFile(file.path).asImageBitmap()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    fun vyfotit(
        chyba: (String) -> Unit,
    ) = with(GenericStringsProvider) {
        viewModelScope.launch {
            val pocetDovolenychFotek = repo.pocetDovolenychFotek()

            if (pocetDovolenychFotek <= 0) {
                chyba(strings.fotkyMaximalneFotek)
                return@launch
            }

            val (id, newUri) = repo.uriIdNoveFotky()

            launchers.takePicture.launch(newUri) {
                if (it) viewModelScope.launch {
                    repo.pridalJsemFoto(id)
                }
            }
        }
    }

    fun vybrat(
        chyba: (String) -> Unit,
    ) = with(GenericStringsProvider) {
        viewModelScope.launch {
            val pocetDovolenychFotek = repo.pocetDovolenychFotek()

            if (pocetDovolenychFotek <= 0) {
                chyba(strings.fotkyMaximalneFotek)
                return@launch
            }

            launchers.pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) { uris ->
                val prekroceno = (uris.size - pocetDovolenychFotek).let { if (it < 0) 0 else it }

                if (prekroceno > 0) chyba(strings.fotkyMaximalneFotekPresazeno)

                viewModelScope.launch {
                    uris.dropLast(prekroceno).forEach { uri ->
                        repo.prekopirovat(uri)
                    }
                }
            }
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