package cz.regulus.dotaznik.spravaFotek

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.regulus.dotaznik.Repository
import cz.regulus.dotaznik.strings.strings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import kotlin.time.Duration.Companion.seconds

@KoinViewModel
class PhotosViewModel(
    private val repo: Repository,
    private val launchers: Launchers,
) : ViewModel() {
    data class Launchers(
        val takePicture: GenericActivityResultLauncher<Uri, Boolean>,
        val pickMultipleMedia: GenericActivityResultLauncher<PickVisualMediaRequest, List<Uri>>,
    )

    val photos = repo.photos
        .map {
            it.map { (id, file) ->
                id to BitmapFactory.decodeFile(file.path).asImageBitmap()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList())

    fun takePicture(
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val remainingSlots = repo.remainingPhotoSlots()

            if (remainingSlots <= 0) {
                error(strings.photos.maxPhotosReached)
                return@launch
            }

            val (id, newUri) = repo.getIdAndUriOfNewPhoto()

            launchers.takePicture.launch(newUri) {
                if (it) viewModelScope.launch {
                    repo.registerTakenPhoto(id)
                }
            }
        }
    }

    fun choosePhoto(
        error: (String) -> Unit,
    ) {
        viewModelScope.launch {
            val remainingSlots = repo.remainingPhotoSlots()

            if (remainingSlots <= 0) {
                error(strings.photos.maxPhotosReached)
                return@launch
            }

            launchers.pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) { uris ->
                val overshoot = (uris.size - remainingSlots).let { if (it < 0) 0 else it }

                if (overshoot > 0) error(strings.photos.maxPhotosOvershoot)

                viewModelScope.launch {
                    uris.dropLast(overshoot).forEach { uri ->
                        repo.copyPhotoTuInternalStorage(uri)
                    }
                }
            }
        }
    }

    fun removePhoto(
        id: Int,
    ) {
        viewModelScope.launch {
            repo.removePhoto(id)
        }
    }
}